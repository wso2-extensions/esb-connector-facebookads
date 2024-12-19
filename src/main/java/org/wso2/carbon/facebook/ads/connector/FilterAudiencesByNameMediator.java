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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilterAudiencesByNameMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(FilterAudiencesByNameMediator.class);
    public static final String FILTER_BY_NAME = "filterByName";
    public static final String DATA = "data";
    public static final String PAGING = "paging";
    public static final String NAME = "name";

    @Override
    public boolean mediate(MessageContext synCtx) {
        String filterByName = (String) ConnectorUtils.lookupTemplateParamater(synCtx, FILTER_BY_NAME);
        if (filterByName == null || filterByName.trim().isEmpty()) {
            log.warn("filterByName parameter is null or empty. Skipping filtering.");
            return true;
        }
        try {
            Axis2MessageContext axis2MessageContext = (Axis2MessageContext) synCtx;
            org.apache.axis2.context.MessageContext axis2MC = axis2MessageContext.getAxis2MessageContext();

            if (JsonUtil.hasAJsonPayload(axis2MC)) {
                String jsonString = JsonUtil.jsonPayloadToString(axis2MC);
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                JsonArray dataArray = jsonObject.getAsJsonArray(DATA);
                if (dataArray == null) {
                    log.warn("No audiences are found.");
                    return true;
                }

                JsonArray filteredData = new JsonArray();
                for (JsonElement element : dataArray) {
                    if (element.isJsonObject()) {
                        JsonObject dataObj = element.getAsJsonObject();
                        JsonElement nameElement = dataObj.get(NAME);
                        if (nameElement != null && nameElement.isJsonPrimitive()) {
                            String name = nameElement.getAsString();
                            if (name.contains(filterByName)) {
                                filteredData.add(dataObj);
                            }
                        }
                    }
                }

                jsonObject.add(DATA, filteredData);

                // Remove the "paging" section from the JSON object
                if (jsonObject.has(PAGING)) {
                    jsonObject.remove(PAGING);
                }

                String filteredJsonString = jsonObject.toString();

                // Set the filtered JSON back as the payload
                JsonUtil.getNewJsonPayload(axis2MC, filteredJsonString, true, true);
            } else {
                log.warn("No JSON payload found in the message context.");
            }
        } catch (Exception e) {
            log.error("Error while filtering JSON payload by name: " + filterByName, e);
            return false;
        }
        return true;
    }
}
