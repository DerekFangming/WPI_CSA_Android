package com.fmning.wpi_csa.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.fmning.wpi_csa.R;

/**
 * Created by fangmingning on 11/2/17.
 */

public class Utils {

    //Format: AppMajorVerion.AppSubVersion.ContentVersion
    //Update this number results server version update
    public static final String BASE_VERSION = "1.03.001";


    private static ProgressDialog loadingDialog;

    public static AppMode APP_MODE = AppMode.OFFLINE;

    public static void checkVerisonInfoAndLoginUser(Context context, boolean showAlert){

    }

    public static void showLoadingIndicator(Context context){
        if (loadingDialog != null){
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View indicator = inflater.inflate(R.layout.view_loading_indicator, null);

        loadingDialog = new ProgressDialog(context, R.style.DialogStyle);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        loadingDialog.setContentView(indicator);

    }

    public static void hideLoadingIndicator(){
        if(loadingDialog == null){
            return;
        }

        loadingDialog.hide();
        loadingDialog = null;
    }

    public static void logMsg(String msg){
        Log.d("haha", msg);
    }
}


