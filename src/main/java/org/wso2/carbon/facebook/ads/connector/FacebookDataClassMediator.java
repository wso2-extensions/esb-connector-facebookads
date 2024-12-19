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

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FacebookDataClassMediator extends AbstractMediator {
    public static final String PROPERTIES = "properties";
    public static final String INPUT_STRUCTURE = "inputStructure";
    public static final String FACEBOOK_API_COMPATIBLE = "FACEBOOK_API_COMPATIBLE";

    @Override
    public boolean mediate(MessageContext synCtx) {
        String inputStructure = (String) ConnectorUtils.lookupTemplateParamater(synCtx, INPUT_STRUCTURE);
        String jsonString = (String) ConnectorUtils.lookupTemplateParamater(synCtx, PROPERTIES);

        // set the payload as it is for FACEBOOK_API_COMPATIBLE input structure
        // executes if the inputStructure is not given or is empty too
        if (FACEBOOK_API_COMPATIBLE.equals(inputStructure)
                || inputStructure == null
                || inputStructure.trim().isEmpty()) {
            synCtx.setProperty(Constants.HASHED_PAYLOAD, jsonString);
            return true;
        }

        if (jsonString == null || jsonString.isEmpty()) {
            return true;
        }
        JSONArray inputArray = new JSONArray(jsonString);
        JSONObject finalObj = FacebookDataProcessor.processData(inputArray);
        synCtx.setProperty(Constants.HASHED_PAYLOAD, finalObj.toString());
        return true;
    }
}
