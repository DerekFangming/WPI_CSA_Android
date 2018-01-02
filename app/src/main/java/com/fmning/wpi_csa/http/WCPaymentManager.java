package com.fmning.wpi_csa.http;

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

    public static void makePayment(final Context context, String type, int id, double amount,
                                   final OnMakePaymentListener listener) {
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            params.put("type", type);
            params.put("id", id);
            params.put("amount", amount);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathMakePayment, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnMakePaymentDone(error, null, null, -1, null);
                            } else {
                                int ticketId = -1;
                                String ticketStr = null;

                                try {
                                    ticketId = response.getInt("ticketId");
                                } catch (JSONException ignored){}
                                try {
                                    ticketStr = response.getString("ticket");
                                } catch (JSONException ignored){}

                                listener.OnMakePaymentDone("", response.getString("status"),
                                        response.getString("ticketStatus"), ticketId, ticketStr);
                            }
                        } catch(JSONException e){
                            listener.OnMakePaymentDone(context.getString(R.string.respond_format_error),
                                    null, null, -1, null);
                        } catch(Exception e){
                            listener.OnMakePaymentDone(context.getString(R.string.unknown_error),
                                    null, null, -1, null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnMakePaymentDone(context.getString(R.string.server_down_error),
                                null, null, -1, null);
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
        void OnMakePaymentDone(String error, String status, String ticketStatus, int ticketId, String ticket);
    }
}
