package com.fmning.wpi_csa.webService;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Fangming
 * On 1/1/2018.
 */

public class WCPaymentManager {

    private static AsyncHttpClient client = new AsyncHttpClient();

    @SuppressWarnings("SameParameterValue")
    public static void checkPaymentStatus(final Context context, String type, int id, final OnCheckPaymentStatusListener listener) {
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            params.put("type", type);
            params.put("id", id);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathCheckPaymentStatus, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnCheckPaymentStatusDone(error, null, -1);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                int ticketId = -1;

                                try {
                                    ticketId = response.getInt("ticketId");
                                } catch (JSONException ignored){}

                                listener.OnCheckPaymentStatusDone("", response.getString("status"), ticketId);
                            }
                        } catch(JSONException e){
                            listener.OnCheckPaymentStatusDone(context.getString(R.string.respond_format_error), null, -1);
                        } catch(Exception e){
                            listener.OnCheckPaymentStatusDone(context.getString(R.string.unknown_error), null, -1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        listener.OnCheckPaymentStatusDone(context.getString(R.string.server_down_error), null, -1);
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


    @SuppressWarnings("SameParameterValue")
    public static void makePayment(final Context context, String type, int id, double amount, String method,
                                   String nonce, final OnMakePaymentListener listener) {
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            params.put("type", type);
            params.put("id", id);
            params.put("amount", amount);
            if (method != null) {
                params.put("method", method);
                params.put("nonce", nonce == null ? "Unknown" : nonce);
            }
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathMakePayment, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnMakePaymentDone(error, null, -1, null);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                int ticketId = -1;
                                String ticketStr = null;

                                try {
                                    ticketId = response.getInt("ticketId");
                                } catch (JSONException ignored){}
                                try {
                                    ticketStr = response.getString("ticket");
                                } catch (JSONException ignored){}

                                listener.OnMakePaymentDone("", response.getString("status"),
                                        ticketId, ticketStr);
                            }
                        } catch(JSONException e){
                            listener.OnMakePaymentDone(context.getString(R.string.respond_format_error),
                                    null, -1, null);
                        } catch(Exception e){
                            listener.OnMakePaymentDone(context.getString(R.string.unknown_error),
                                    null, -1, null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        listener.OnMakePaymentDone(context.getString(R.string.server_down_error),
                                null, -1, null);
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

    public interface OnMakePaymentListener {
        void OnMakePaymentDone(String error, String status, int ticketId, String ticket);
    }

    public interface OnCheckPaymentStatusListener {
        void OnCheckPaymentStatusDone(String error, String status, int ticketId);
    }

}
