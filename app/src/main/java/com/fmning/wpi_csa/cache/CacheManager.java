package com.fmning.wpi_csa.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fangmingning
 * On 11/6/17.
 */

public class CacheManager {
    private static final String IMG_CACHE = "imageCache";
    private static final String IMG_CACHE_SUB_PATH = "/imageCache/";
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

    public static void getImage(String name, final Context context, final OnCacheGetImageDoneListener listener){
        int id = 0;
        if (name.startsWith("WCImage_")){
            try {
                id = Integer.parseInt(name.replace("WCImage_", ""));
            } catch (NumberFormatException e) {
                listener.OnCacheGetImageDone(context.getString(R.string.number_format_error), null);
                return;
            }

        }else {
            int resourceId = context.getResources().getIdentifier(
                    FilenameUtils.removeExtension(name), "drawable", context.getPackageName());
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceId);
            listener.OnCacheGetImageDone("", bm);
            return;
        }
        String appRootPath = context.getApplicationInfo().dataDir;

        /*Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(appRootPath + IMG_CACHE_SUB_PATH + "test.jpg");
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        /*AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", 1);
        client.get("https://wcservice.fmning.com/get_image", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                String error = "";
                if (image == null) {
                    error = new String(responseBody);
                    if(error == null || error.equals("")){
                        error = context.getString(R.string.unknown_error);
                    }
                }
                listener.OnCacheGetImageDone(error, image);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.OnCacheGetImageDone(context.getString(R.string.server_down_error), null);
            }
        });*/
    }


    public interface OnCacheGetImageDoneListener {
        void OnCacheGetImageDone(String error, Bitmap image);
    }
}
