/*
 *  Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.facebook.ads.connector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.synapse.SynapseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FacebookDataProcessor {

    private static final Log log = LogFactory.getLog(FacebookDataProcessor.class);

    private static final Set<String> ALLOWED_PII_TYPES = new HashSet<>(Arrays.asList(
            "EXTERN_ID", "EMAIL", "PHONE", "GEN", "DOBY", "DOBM", "DOBD",
            "LN", "FN", "FI", "CT", "ST", "ZIP", "MADID", "COUNTRY", "DOB"
    ));

    private static final Map<String, String> FIELD_NAME_MAPPING = new HashMap<>();
    private static final Map<String, String> US_STATE_MAP = new HashMap<>();
    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();

    static {
        FIELD_NAME_MAPPING.put("extern_id", "EXTERN_ID");
        FIELD_NAME_MAPPING.put("email", "EMAIL");
        FIELD_NAME_MAPPING.put("phone", "PHONE");
        FIELD_NAME_MAPPING.put("gen", "GEN");
        FIELD_NAME_MAPPING.put("doby", "DOBY");
        FIELD_NAME_MAPPING.put("dobm", "DOBM");
        FIELD_NAME_MAPPING.put("dobd", "DOBD");
        FIELD_NAME_MAPPING.put("ln", "LN");
        FIELD_NAME_MAPPING.put("fn", "FN");
        FIELD_NAME_MAPPING.put("fi", "FI");
        FIELD_NAME_MAPPING.put("ct", "CT");
        FIELD_NAME_MAPPING.put("st", "ST");
        FIELD_NAME_MAPPING.put("zip", "ZIP");
        FIELD_NAME_MAPPING.put("madid", "MADID");
        FIELD_NAME_MAPPING.put("country", "COUNTRY");
        FIELD_NAME_MAPPING.put("dob", "DOB");

        // All US states and DC
        US_STATE_MAP.put("alabama", "al");
        US_STATE_MAP.put("alaska", "ak");
        US_STATE_MAP.put("arizona", "az");
        US_STATE_MAP.put("arkansas", "ar");
        US_STATE_MAP.put("california", "ca");
        US_STATE_MAP.put("colorado", "co");
        US_STATE_MAP.put("connecticut", "ct");
        US_STATE_MAP.put("delaware", "de");
        US_STATE_MAP.put("florida", "fl");
        US_STATE_MAP.put("georgia", "ga");
        US_STATE_MAP.put("hawaii", "hi");
        US_STATE_MAP.put("idaho", "id");
        US_STATE_MAP.put("illinois", "il");
        US_STATE_MAP.put("indiana", "in");
        US_STATE_MAP.put("iowa", "ia");
        US_STATE_MAP.put("kansas", "ks");
        US_STATE_MAP.put("kentucky", "ky");
        US_STATE_MAP.put("louisiana", "la");
        US_STATE_MAP.put("maine", "me");
        US_STATE_MAP.put("maryland", "md");
        US_STATE_MAP.put("massachusetts", "ma");
        US_STATE_MAP.put("michigan", "mi");
        US_STATE_MAP.put("minnesota", "mn");
        US_STATE_MAP.put("mississippi", "ms");
        US_STATE_MAP.put("missouri", "mo");
        US_STATE_MAP.put("montana", "mt");
        US_STATE_MAP.put("nebraska", "ne");
        US_STATE_MAP.put("nevada", "nv");
        US_STATE_MAP.put("newhampshire", "nh");
        US_STATE_MAP.put("newjersey", "nj");
        US_STATE_MAP.put("newmexico", "nm");
        US_STATE_MAP.put("newyork", "ny");
        US_STATE_MAP.put("northcarolina", "nc");
        US_STATE_MAP.put("northdakota", "nd");
        US_STATE_MAP.put("ohio", "oh");
        US_STATE_MAP.put("oklahoma", "ok");
        US_STATE_MAP.put("oregon", "or");
        US_STATE_MAP.put("pennsylvania", "pa");
        US_STATE_MAP.put("rhodeisland", "ri");
        US_STATE_MAP.put("southcarolina", "sc");
        US_STATE_MAP.put("southdakota", "sd");
        US_STATE_MAP.put("tennessee", "tn");
        US_STATE_MAP.put("texas", "tx");
        US_STATE_MAP.put("utah", "ut");
        US_STATE_MAP.put("vermont", "vt");
        US_STATE_MAP.put("virginia", "va");
        US_STATE_MAP.put("washington", "wa");
        US_STATE_MAP.put("westvirginia", "wv");
        US_STATE_MAP.put("wisconsin", "wi");
        US_STATE_MAP.put("wyoming", "wy");
        US_STATE_MAP.put("district of columbia", "dc");

        // If the country is not present here, default to lowercase
        COUNTRY_MAP.put("unitedstates", "us");
        COUNTRY_MAP.put("us", "us");
        COUNTRY_MAP.put("unitedkingdom", "gb");
        COUNTRY_MAP.put("greatbritain", "gb");
        COUNTRY_MAP.put("china", "cn");
        COUNTRY_MAP.put("canada", "ca");
        COUNTRY_MAP.put("france", "fr");
        COUNTRY_MAP.put("germany", "de");
    }

    static JSONObject processData(JSONArray inputArray) {
        Map<String, Integer> fieldCounts = new HashMap<>();

        // Determine maximum occurrences of each field
        for (int i = 0; i < inputArray.length(); i++) {
            JSONObject entry = inputArray.getJSONObject(i);
            Iterator<String> keys = entry.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String mappedKey = FIELD_NAME_MAPPING.getOrDefault(key.toLowerCase().replaceAll("\\.\\d+", ""),
                        key.toUpperCase().replaceAll("\\.\\d+", ""));
                if (key.toLowerCase().contains("uid") || key.toLowerCase().contains("externid")) {
                    mappedKey = "EXTERN_ID";
                }
                if (ALLOWED_PII_TYPES.contains(mappedKey)) {
                    int currentMax = fieldCounts.getOrDefault(mappedKey, 0);
                    int keyCount = countOccurrences(entry, key);
                    fieldCounts.put(mappedKey, Math.max(currentMax, keyCount));
                } else {
                    log.warn("Ignoring unsupported PII type: " + key);
                }
            }
        }

        // Build schema
        List<String> schemaList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : fieldCounts.entrySet()) {
            String field = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                schemaList.add(field);
            }
        }

        Collections.sort(schemaList);
        JSONArray schemaArray = new JSONArray(schemaList);
        JSONArray dataArray = new JSONArray();

        // Build data rows
        for (int i = 0; i < inputArray.length(); i++) {
            JSONObject entry = inputArray.getJSONObject(i);
            String country = entry.optString("country", null);
            Map<String, Integer> fieldOccurrenceCounter = new HashMap<>();
            JSONArray dataRow = new JSONArray();

            for (String field : schemaList) {
                int occurrence = fieldOccurrenceCounter.getOrDefault(field, 0);
                fieldOccurrenceCounter.put(field, occurrence + 1);

                String originalField = getOriginalFieldName(field);
                String foundValue = getOccurrenceValue(entry, originalField, occurrence, field);
                String normalized = normalizeField(field, foundValue, country);
                String finalValue = normalized.isEmpty() ? "" : (requiresHashing(field) ? hashIfNotAlreadyHashed(normalized) : normalized);
                dataRow.put(finalValue);
            }
            dataArray.put(dataRow);
        }

        JSONObject payloadObj = new JSONObject();
        payloadObj.put("schema", schemaArray);
        payloadObj.put("data", dataArray);

        JSONObject finalObj = new JSONObject();
        finalObj.put("payload", payloadObj);
        return finalObj;
    }

    private static int countOccurrences(JSONObject entry, String key) {
        int keyCount = 0;
        String baseKey = key.replaceAll("\\.\\d+", "");
        while (true) {
            String fieldKey = baseKey + (keyCount == 0 ? "" : "." + keyCount);
            if (entry.has(fieldKey)) {
                keyCount++;
            } else {
                break;
            }
        }
        return keyCount;
    }

    private static String getOccurrenceValue(JSONObject entry, String originalField, int occurrence, String piiType) {
        if ("EXTERN_ID".equals(piiType)) {
            // Gather all fields that could represent EXTERN_ID: extern_id, uid, externid
            List<String> externIdFields = new ArrayList<>();
            for (Object objKey : entry.keySet()) {
                String key = (String) objKey;
                String lower = key.toLowerCase();
                String baseKey = key.replaceAll("\\.\\d+", "");
                if (lower.contains("uid") || lower.contains("externid") || lower.contains("extern_id")) {
                    externIdFields.add(key);
                }
            }
            // Sort them
            externIdFields.sort((a, b) -> {
                String baseA = a.replaceAll("\\.\\d+", "");
                String baseB = b.replaceAll("\\.\\d+", "");
                int cmp = baseA.compareTo(baseB);
                if (cmp == 0) {
                    // Compare occurrence suffixes if any
                    int idxA = getSuffixIndex(a);
                    int idxB = getSuffixIndex(b);
                    return Integer.compare(idxA, idxB);
                }
                return cmp;
            });

            int foundIndex = 0;
            for (String f : externIdFields) {
                // Count occurrences of each matched field
                // If a field base name matches multiple occurrences, we handle them too
                String baseF = f.replaceAll("\\.\\d+", "");
                int maxCount = countOccurrences(entry, baseF);
                for (int i = 0; i < maxCount; i++) {
                    String candidate = baseF + (i == 0 ? "" : "." + i);
                    if (!entry.has(candidate)) break;
                    if (foundIndex == occurrence) {
                        return entry.optString(candidate, "");
                    }
                    foundIndex++;
                }
            }

            return "";
        } else {
            int foundIndex = 0;
            for (int attempt = 0; ; attempt++) {
                String candidate = originalField + (attempt == 0 ? "" : "." + attempt);
                if (!entry.has(candidate)) break;
                if (foundIndex == occurrence) {
                    return entry.optString(candidate, "");
                }
                foundIndex++;
            }
            return "";
        }
    }

    private static int getSuffixIndex(String key) {
        Matcher m = Pattern.compile("\\.(\\d+)$").matcher(key);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                log.error("Error occurred while getting suffix index: ", e);
                throw new SynapseException("Error occurred while getting suffix index: " + e.getMessage(), e);
            }
        }
        return 0;
    }

    private static String getOccurrenceValue(JSONObject entry, String originalField, int occurrence) {
        return getOccurrenceValue(entry, originalField, occurrence, "");
    }

    private static String getOriginalFieldName(String piiType) {
        for (Map.Entry<String, String> entry : FIELD_NAME_MAPPING.entrySet()) {
            if (entry.getValue().equals(piiType)) {
                return entry.getKey();
            }
        }
        return piiType.toLowerCase();
    }

    private static boolean requiresHashing(String fieldName) {
        return !fieldName.equalsIgnoreCase("MADID") &&
                !fieldName.equalsIgnoreCase("FI") &&
                !fieldName.equalsIgnoreCase("PAGE_SCOPED_USER_ID");
    }

    private static Map<String, String> parsePhoneMeta(String val) {
        Map<String, String> meta = new HashMap<>();
        Pattern p = Pattern.compile("(alreadyHasPhoneCode|countryCode|phoneCode|phoneNumber)\\s*:\\s*([^;]+)");
        Matcher m = p.matcher(val);
        while (m.find()) {
            String key = m.group(1).trim();
            String rawValue = m.group(2).trim();
            if (rawValue.startsWith("'") && rawValue.endsWith("'")) {
                rawValue = rawValue.substring(1, rawValue.length() - 1);
            } else if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                rawValue = rawValue.substring(1, rawValue.length() - 1);
            }
            meta.put(key, rawValue);
        }
        return meta;
    }

    // Normalize phone numbers
    private static String normalizePhone(String val, String country) {
        Map<String, String> meta = parsePhoneMeta(val);
        if (meta.isEmpty()) {
            val = val.replaceAll("\\D+", "");
            if ("us".equalsIgnoreCase(country)) {
                val = val.replaceFirst("^0+", "");
                if (!val.isEmpty() && !val.startsWith("1")) {
                    val = "1" + val;
                }
            }
            return val;
        } else {
            String alreadyHasPhoneCodeStr = meta.get("alreadyHasPhoneCode");
            String countryCode = meta.get("countryCode");
            String phoneCode = meta.get("phoneCode");
            String phoneNumber = meta.get("phoneNumber");

            if (phoneNumber == null) {
                val = val.replaceAll("\\D+", "");
                if ("us".equalsIgnoreCase(country)) {
                    val = val.replaceFirst("^0+", "");
                    if (!val.isEmpty() && !val.startsWith("1")) {
                        val = "1" + val;
                    }
                }
                return val;
            }

            phoneNumber = phoneNumber.replaceAll("\\D+", "");
            boolean alreadyHasPhoneCode = alreadyHasPhoneCodeStr != null && alreadyHasPhoneCodeStr.equalsIgnoreCase("true");

            if (!alreadyHasPhoneCode) {
                if (phoneCode != null && !phoneCode.isEmpty()) {
                    phoneCode = phoneCode.replaceAll("\\D+", "");
                    phoneNumber = phoneCode + phoneNumber;
                } else if ("us".equalsIgnoreCase(countryCode)) {
                    phoneNumber = phoneNumber.replaceFirst("^0+", "");
                    if (!phoneNumber.isEmpty() && !phoneNumber.startsWith("1")) {
                        phoneNumber = "1" + phoneNumber;
                    }
                }
            }
            return phoneNumber;
        }
    }

    // Normalize the cities
    private static String normalizeCity(String val) {
        return val.replaceAll("[^a-z]", "");
    }

    // Normalize the states
    private static String normalizeState(String val, String country) {
        String clean = val.replaceAll("[^a-z]", "");
        if ("us".equalsIgnoreCase(country)) {
            if (clean.length() == 2 && US_STATE_MAP.containsValue(clean)) {
                return clean;
            }
            String stateCode = US_STATE_MAP.get(clean);
            if (stateCode != null) {
                return stateCode;
            }
        }
        return clean;
    }

    // Normalize the gender
    private static String normalizeGender(String val) {
        Set<String> maleSynonyms = new HashSet<>(Arrays.asList("m", "male", "man", "boy"));
        Set<String> femaleSynonyms = new HashSet<>(Arrays.asList("f", "female", "woman", "girl"));

        if (maleSynonyms.contains(val)) {
            return "m";
        } else if (femaleSynonyms.contains(val)) {
            return "f";
        }
        return "m";
    }

    // Normalize the countries
    private static String normalizeCountry(String val) {
        val = val.replaceAll("[^a-z]", "");
        String countryCode = COUNTRY_MAP.get(val);
        if (countryCode != null) {
            return countryCode;
        }
        return val;
    }

    // Normalize the dob
    private static String parseDOBToYYYYMMDD(String val) {
        String[] parts = val.split("[/\\-–]+");
        if (parts.length < 3) {
            return val;
        }

        int[] nums = new int[3];
        for (int i = 0; i < 3; i++) {
            try {
                nums[i] = Integer.parseInt(parts[i].replaceAll("\\D", ""));
            } catch (NumberFormatException e) {
                return val;
            }
        }

        int a = nums[0], b = nums[1], c = nums[2];
        int year, month, day;

        if (String.valueOf(c).length() == 4) {
            year = c;
            if (a <= 12 && b <= 31) {
                month = a; day = b;
            } else if (b <= 12 && a <=31) {
                month = b; day = a;
            } else {
                return val;
            }
        } else if (String.valueOf(a).length() == 4) {
            year = a;
            if (b <= 12 && c <=31) {
                month = b; day = c;
            } else if (c <=12 && b <=31) {
                month = c; day = b;
            } else {
                return val;
            }
        } else if (String.valueOf(b).length() == 4) {
            year = b;
            if (a <= 12 && c <=31) {
                month = a; day = c;
            } else if (c <=12 && a <=31) {
                month = c; day = a;
            } else {
                return val;
            }
        } else {
            return val;
        }

        if (month < 1 || month > 12 || day < 1 || day > 31) {
            return val;
        }
        return String.format("%04d%02d%02d", year, month, day);
    }

    private static String normalizeDOB(String val) {
        return parseDOBToYYYYMMDD(val);
    }

    private static String normalizeDOBY(String val) {
        if (val.matches("\\d{2}")) {
            int year = Integer.parseInt(val);
            if (year < 50) {
                year += 2000;
            } else {
                year += 1900;
            }
            val = String.valueOf(year);
        }
        return val;
    }

    private static String normalizeDOBM(String val) {
        if (val.matches("\\d+")) {
            int month = Integer.parseInt(val);
            if (month < 1 || month > 12) {
                return "";
            }
            return String.format("%02d", month);
        }
        return "";
    }

    private static String normalizeDOBD(String val) {
        if (val.matches("\\d+")) {
            int day = Integer.parseInt(val);
            if (day < 1 || day > 31) {
                return "";
            }
            return String.format("%02d", day);
        }
        return "";
    }

    private static String normalizeField(String fieldName, String value, String country) {
        if (value == null) {
            return "";
        }
        String val = value.trim().toLowerCase();

        switch (fieldName) {
            case "EMAIL":
                return val;

            case "PHONE":
                val = normalizePhone(val, country);
                return val;

            case "FN":
            case "LN":
                val = val.replaceAll("[^\\p{L}]", "");
                return val;

            case "ZIP":
                val = val.replaceAll("\\s+", "");
                if ("us".equalsIgnoreCase(country)) {
                    val = val.replaceAll("[^0-9]", "");
                    if (val.length() > 5) {
                        val = val.substring(0, 5);
                    }
                }
                return val;

            case "CT":
                val = normalizeCity(val);
                return val;

            case "ST":
                val = normalizeState(val, country);
                return val;

            case "COUNTRY":
                val = normalizeCountry(val);
                return val;

            case "DOB":
                val = normalizeDOB(val);
                return val;

            case "DOBY":
                val = normalizeDOBY(val);
                return val;

            case "DOBM":
                val = normalizeDOBM(val);
                return val;

            case "DOBD":
                val = normalizeDOBD(val);
                return val;

            case "GEN":
                val = normalizeGender(val);
                return val;

            default:
                return val;
        }
    }

    private static String hashString(String input) {
        if (input == null || input.isEmpty()) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error hashing string: " + input, e);
            return "";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String hashIfNotAlreadyHashed(String input) {
        if (input.matches("^[0-9a-f]{64}$")) {
            return input;
        }
        return hashString(input);
    }
}
