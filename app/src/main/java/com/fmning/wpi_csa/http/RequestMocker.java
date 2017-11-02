package com.fmning.wpi_csa.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fangmingning on 11/2/17.
 */

public class RequestMocker {

    public static JSONObject getFakeResponse(String requestPath) {
        try{
            switch(requestPath){
                case WCUtils.pathGetVersionInfo:
                    return new JSONObject("{\"error\":\"\", \"status\":\"OK\"}");
                default:
                    return null;
            }
        }catch(JSONException e){
            return null;
        }
    }
}
