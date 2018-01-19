package com.fmning.wpi_csa.webService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by fangmingning
 * On 11/10/17.
 */

public class WCImageManager {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getImage(final Context context, int id, final OnGetImageDoneListener listener){

        RequestParams params = new RequestParams();
        params.put("id", id);

        client.get(WCUtils.serviceBase + WCUtils.pathGetImage, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                String error = "";
                if (image == null) {
                    error = new String(responseBody);
                    if(error.equals("")){
                        error = context.getString(R.string.unknown_error);
                    }
                }
                listener.OnGetImageDone(error, image);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.OnGetImageDone(context.getString(R.string.server_down_error), null);
            }

        });
    }

    public static void saveTypeUniqueImg(final Context context, Bitmap image, String type, int compressRate,
                                         final OnUploadImageDoneListener listener) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, compressRate, byteArrayOutputStream);
        String base64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            params.put("type", type);
            params.put("image", base64);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathSaveTUImage, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnUploadImageDone(error, -1);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnUploadImageDone("", response.getInt("imageId"));
                            }
                        } catch(JSONException e){
                            listener.OnUploadImageDone(context.getString(R.string.respond_format_error), -1);
                        } catch(Exception e){
                            listener.OnUploadImageDone(context.getString(R.string.unknown_error), -1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnUploadImageDone(context.getString(R.string.server_down_error), -1);
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


    public interface OnGetImageDoneListener {
        void OnGetImageDone(String error, Bitmap image);
    }

    public interface OnUploadImageDoneListener {
        void OnUploadImageDone(String error, int id);
    }
}
