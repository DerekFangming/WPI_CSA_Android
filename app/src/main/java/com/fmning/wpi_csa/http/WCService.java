package com.fmning.wpi_csa.http;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.http.objects.WCUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fangmingning
 * On 11/2/17.
 */

public class WCService {
    public static WCUser currentUser;

    private static AsyncHttpClient client = new AsyncHttpClient();

    @SuppressWarnings("ConstantConditions")
    public static void checkSoftwareVersion(final Context context, String version, final OnCheckSoftwareVersionListener listener){


        if(WCUtils.localMode){
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathGetVersionInfo);
            listener.OnCheckSoftwareVersionDone((String)mock.get(0), (String)mock.get(1), (String)mock.get(2),
                    (String)mock.get(3), (String)mock.get(4));
            return;
        }

        RequestParams params = new RequestParams();
        params.put("version", version);

        client.get(WCUtils.serviceBase + WCUtils.pathGetVersionInfo, params,
                new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnCheckSoftwareVersionDone(error, "", "", "", "");
                    } else {
                        listener.OnCheckSoftwareVersionDone(response.getString("status"), response.getString("title"),
                                response.getString("message"), response.getString("updates"),
                                response.getString("newVersion"));
                    }
                } catch(JSONException e){
                    listener.OnCheckSoftwareVersionDone(context.getString(R.string.respond_format_error),
                            "", "", "", "");
                } catch(Exception e){
                    listener.OnCheckSoftwareVersionDone(context.getString(R.string.unknown_error),
                            "", "", "", "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                listener.OnCheckSoftwareVersionDone(context.getString(R.string.server_down_error),
                        "", "", "", "");
            }
        });
    }

    public interface OnCheckSoftwareVersionListener {
        void OnCheckSoftwareVersionDone(String status, String title, String msg, String updates, String version);
    }
}
