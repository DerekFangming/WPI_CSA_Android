package com.fmning.wpi_csa.cache;

import android.content.Context;

import com.fmning.wpi_csa.helpers.Utils;

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
    public static void localDirInitiateSetup(Context context){
        String dbPath = context.getApplicationInfo().dataDir;
        File dbFilePath = new File(dbPath + "/" + Database.NAME);
        if(!dbFilePath.exists()){

            try
            {
                InputStream inputStream = context.getAssets().open(Database.NAME);
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
                Utils.logMsg("Copied successfully");
            } catch (IOException e){
                Utils.logMsg("IO Exception when copying database files");//TODO
            }
        } else {
            Utils.logMsg("Already copied");
        }
    }
}
