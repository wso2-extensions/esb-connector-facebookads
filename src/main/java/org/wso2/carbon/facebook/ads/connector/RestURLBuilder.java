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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RestURLBuilder extends AbstractConnector {

    private static final Gson gson = new Gson();
    private static final String encoding = "UTF-8";
    private static final String URL_PATH = "uri.var.urlPath";
    private static final String URL_QUERY = "uri.var.urlQuery";
    private String operationPath = "";
    private String pathParameters = "";
    private String queryParameters = "";

    private static final Map<String, String> parameterNameMap = new HashMap<String, String>() {{

        put("summary", "summary");
        put("objectCount", "object_count");
        put("adAccountId", "ad_account_id");
        put("customAudienceId", "custom_audience_id");
        put("campaignId", "campaign_id");
        put("beforeDate", "before_date");
        put("updatedSince", "updated_since");
        put("effectiveStatus", "effective_status");
        put("deleteStrategy", "delete_strategy");
        put("adId", "ad_id");
        put("datePreset", "date_preset");
        put("adSetId", "ad_set_id");
        put("fields", "fields");
        put("properties", "properties");
        put("isCompleted", "is_completed");
        put("timeRange", "time_range");
    }};

    public String getOperationPath() {

        return operationPath;
    }

    public void setOperationPath(String operationPath) {

        this.operationPath = operationPath;
    }

    public String getPathParameters() {

        return pathParameters;
    }

    public void setPathParameters(String pathParameters) {

        this.pathParameters = pathParameters;
    }

    public String getQueryParameters() {

        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {

        this.queryParameters = queryParameters;
    }

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        try {
            String accessToken = (String) messageContext.getProperty(Constants.PROPERTY_ACCESS_TOKEN);
            if (StringUtils.isBlank(accessToken)) {
                String errorMessage = Constants.GENERAL_ERROR_MSG + "Access token is not set.";
                Utils.setErrorPropertiesToMessage(messageContext, Constants.ErrorCodes.INVALID_CONFIG, errorMessage);
                handleException(errorMessage, messageContext);
            }

            String urlPath = getOperationPath();
            if (StringUtils.isNotEmpty(this.pathParameters)) {
                String[] pathParameterList = getPathParameters().split(",");
                for (String pathParameter : pathParameterList) {
                    String name = parameterNameMap.get(pathParameter);
                    String paramValue = (String) getParameter(messageContext, pathParameter);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String encodedParamValue = URLEncoder.encode(paramValue, encoding);
                        urlPath = urlPath.replace("{" + name + "}", encodedParamValue);
                    } else {
                        String errorMessage = Constants.GENERAL_ERROR_MSG + "Mandatory parameter '" + pathParameter + "' is not set.";
                        Utils.setErrorPropertiesToMessage(messageContext, Constants.ErrorCodes.INVALID_CONFIG, errorMessage);
                        handleException(errorMessage, messageContext);
                    }
                }
            }

            StringJoiner urlQuery = new StringJoiner("&");
            urlQuery.add("?" + Constants.ACCESS_TOKEN + Constants.EQUAL_SIGN + URLEncoder.encode(accessToken, encoding));
            if (StringUtils.isNotEmpty(this.queryParameters)) {
                String[] queryParameterList = getQueryParameters().split(",");
                for (String queryParameter : queryParameterList) {
                    String paramValue = (String) getParameter(messageContext, queryParameter);
                    if (StringUtils.isNotEmpty(paramValue)) {
                        String name = parameterNameMap.get(queryParameter);
                        if ("properties".equals(name)) {
                            processJSONProperties(urlQuery, parseJsonStringToMap(paramValue));
                            continue;
                        }
                        String encodedParamValue = URLEncoder.encode(paramValue, encoding);
                        urlQuery.add(name + Constants.EQUAL_SIGN + encodedParamValue);
                    }
                }
            }

            messageContext.setProperty(URL_PATH, urlPath);
            messageContext.setProperty(URL_QUERY, urlQuery.toString());
        } catch (UnsupportedEncodingException e) {
            String errorMessage = Constants.GENERAL_ERROR_MSG + "Error occurred while constructing the URL query.";
            Utils.setErrorPropertiesToMessage(messageContext, Constants.ErrorCodes.GENERAL_ERROR, errorMessage);
            handleException(errorMessage, messageContext);
        }
    }

    /**
     * Parse the JSON string to a Map.
     *
     * @param jsonString JSON string
     * @return Map<String, Object> Map of JSON properties
     *
     */
    private static Map<String, Object> parseJsonStringToMap(String jsonString) {
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(jsonString, type);
    }

    /**
     * Process JSON properties and add them to the URL query.
     *
     * @param urlQuery URL query
     * @param params   Map of JSON properties
     * @throws UnsupportedEncodingException If an error occurs while encoding the URL
     */
    private static void processJSONProperties(StringJoiner urlQuery, Map<String, Object> params) throws UnsupportedEncodingException {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = URLEncoder.encode(entry.getKey(), encoding);
            Object value = entry.getValue();
            if (value instanceof Map) {
                String jsonStr = gson.toJson(value);
                urlQuery.add(key + Constants.EQUAL_SIGN + jsonStr);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    String itemValue = URLEncoder.encode(item.toString(), encoding);
                    urlQuery.add(key + Constants.EQUAL_SIGN + itemValue);
                }
            } else {
                String encodedValue = URLEncoder.encode(value.toString(), encoding);
                urlQuery.add(key + Constants.EQUAL_SIGN + encodedValue);
            }
        }
    }
}
