package org.wso2.carbon.facebook.ads.connector;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FacebookDataClassMediator extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext synCtx) {
        String jsonString = (String) ConnectorUtils.lookupTemplateParamater(synCtx, "properties");
        if (jsonString == null || jsonString.isEmpty()) {
            return true;
        }

        JSONArray inputArray = new JSONArray(jsonString);

        JSONObject finalObj = FacebookDataProcessor.processData(inputArray);
        synCtx.setProperty("HASHED_PAYLOAD", finalObj.toString());
        return true;
    }
}
