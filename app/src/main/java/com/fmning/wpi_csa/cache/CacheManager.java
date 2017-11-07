package com.fmning.wpi_csa.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fmning.wpi_csa.helpers.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fangmingning
 * On 11/6/17.
 */

public class CacheManager {
    private static final String IMG_CACHE = "imageCache";
    //private static final String PDF_CACHE = "pdfCache";

    public static void localDirInitiateSetup(Context context){
        String appRootPath = context.getApplicationInfo().dataDir;

        File dbFolder = new File(appRootPath + "/databases");
        dbFolder.mkdir();

        File dbFilePath = new File(appRootPath + DatabaseOpenHelper.DB_SUB_PATH);

        try {
            InputStream inputStream = context.getAssets().open(DatabaseOpenHelper.DB_NAME);
            OutputStream outputStream = new FileOutputStream(dbFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))>0)
            {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Utils.logMsg("Copied successfully");//Copy can be done without deleting
        } catch (IOException e){
            Utils.logMsg("IO Exception when copying database files");//TODO
        }

        File imageCachePath = new File(appRootPath + File.separator + IMG_CACHE);

        try {
            if (imageCachePath.exists()) {
                FileUtils.cleanDirectory(imageCachePath);
                Utils.logMsg("deleted");
            } else {
                imageCachePath.mkdir();
                Utils.logMsg("ok");
            }
        } catch (IOException e){
            Utils.logMsg("IO Exception when creating image cache");//TODO
        }
    }

    public static void getImage(String name, final OnCacheGetImageDoneListener listener){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", 1000);
        client.get("https://wcservice.fmning.com/get_image", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                listener.OnCacheGetImageDone("", image);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.OnCacheGetImageDone(responseBody.toString(), null);
            }
        });
    }


    public interface OnCacheGetImageDoneListener {
        void OnCacheGetImageDone(String error, Bitmap image);
    }
}
