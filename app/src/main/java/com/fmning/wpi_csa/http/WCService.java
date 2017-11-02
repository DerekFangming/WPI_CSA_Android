package com.fmning.wpi_csa.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fangmingning on 11/2/17.
 */

public class WCService {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void checkSoftwareVersion(String version, OnJsonCompleteListener listner){

        final OnJsonCompleteListener onJsonCompleteListener = listner;

        if(WCUtils.localMode){
            onJsonCompleteListener.onJsonComplete(RequestMocker.getFakeResponse(WCUtils.pathGetVersionInfo));
            return;
        }
        if(!WCUtils.isNetworkAvailable()){
            onJsonCompleteListener.onJsonComplete(WCUtils.serverDownResponse);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("version", version);

        client.get(WCUtils.serviceBase + WCUtils.pathGetVersionInfo, params,
                new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                onJsonCompleteListener.onJsonComplete(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                onJsonCompleteListener.onJsonComplete(WCUtils.serverDownResponse);
            }
        });
    }
}
