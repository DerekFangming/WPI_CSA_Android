package com.fmning.wpi_csa.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fmning.wpi_csa.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

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


    public interface OnGetImageDoneListener {
        void OnGetImageDone(String error, Bitmap image);
    }
}
