package com.fmning.wpi_csa.webService;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.webService.objects.WCUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

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
        params.put("device", "android");

        client.get(WCUtils.serviceBase + WCUtils.pathGetVersionInfo, params,
                new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnCheckSoftwareVersionDone(error, "", "", "", "");
                    } else {
                        String status = "";
                        String title = "";
                        String message = "";
                        String updates = "";
                        String newVersion = "";

                        try{
                            status = response.getString("status");
                        }catch (JSONException ignored){}
                        try{
                            title = response.getString("title");
                        }catch (JSONException ignored){}
                        try{
                            message = response.getString("message");
                        }catch (JSONException ignored){}
                        try{
                            updates = response.getString("updates");
                        }catch (JSONException ignored){}
                        try{
                            newVersion = response.getString("newVersion");
                        }catch (JSONException ignored){}
                        listener.OnCheckSoftwareVersionDone(status, title, message, updates, newVersion);
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

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
            }
        });
    }

    public static void reportSGProblem(final Context context, int menuId, String accessToken, String email,
                                       String report, final OnReportProblemListener listener) {
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("menuId", menuId);
            params.put("report", report);
            if (email != null) {
                params.put("email", email);
            }
            if (accessToken != null) {
                params.put("accessToken", accessToken);
            }
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathCreateReport, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnReportProblemDone(error);
                            } else {
                                listener.OnReportProblemDone("");
                            }
                        } catch(JSONException e){
                            listener.OnReportProblemDone(context.getString(R.string.respond_format_error));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        listener.OnReportProblemDone(context.getString(R.string.server_down_error));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
                    }
                });
    }

    public static void getTicket(final Context context, int id, final OnGetTicketListener listener) {
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("id", id);
            params.put("accessToken", WCService.currentUser.accessToken);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathGetTicket, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnGetTicketDone(error, null);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnGetTicketDone("", response.getString("ticket"));
                            }
                        } catch(JSONException e){
                            listener.OnGetTicketDone(context.getString(R.string.respond_format_error), null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        listener.OnGetTicketDone(context.getString(R.string.server_down_error), null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        onFailure(statusCode, headers, throwable.getLocalizedMessage(), throwable);
                    }
                });
    }


    public interface OnCheckSoftwareVersionListener {
        void OnCheckSoftwareVersionDone(String status, String title, String msg, String updates, String version);
    }

    public interface OnReportProblemListener {
        void OnReportProblemDone(String error);
    }

    public interface OnGetTicketListener {
        void OnGetTicketDone(String error, String ticket);
    }

}
