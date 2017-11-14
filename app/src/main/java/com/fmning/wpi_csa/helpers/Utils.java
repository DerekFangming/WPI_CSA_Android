package com.fmning.wpi_csa.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.fmning.wpi_csa.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fangmingning on 11/2/17.
 */

public class Utils {

    //Format: AppMajorVerion.AppSubVersion.ContentVersion
    //Update this number results server version update
    public static final String BASE_VERSION = "1.03.001";


    private static ProgressDialog loadingDialog;

    public static AppMode APP_MODE = AppMode.OFFLINE;

    /**
     UTC Time formatter that converts iso-8601 with millisecond.
     Mainly used to convert full iso-8601 time string from server to date in UTC timezone
     Examples:
     iso8601DateUTC("2017-01-09T17:34:12.215Z") returns UTC Date 2017-01-09 17:34:12
     iso8601FullUTC.format(AboveDate) returns 2017-01-09T12:34:12.215-05:00
     */
    private static final SimpleDateFormat iso8601FullUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     UTC Time formatter that converts iso-8601 without millisecond.
     Mainly used to convert abbreviated iso-8601 time string from server to date in UTC timezone
     Examples:
     iso8601DateUTC("2017-01-09T17:34:12Z") returns UTC Date 2017-01-09 17:34:12
     iso8601AbbrUTC.format(AboveDate) returns 2017-01-09T17:34:12Z
     */
    private static final SimpleDateFormat iso8601AbbrUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     Formatter with local time zone that converts simplified date
     Mainly used to convert UTC Date object to local time string and display on UI, vice versa
     This local time zone will handle all timezone changes.
     Examples:

     EDT: Adding offsets for GMT-4 between Mar to Nov
     abbrLocalZone.parse("2006/05/01 10:41:00") returns 2006-05-01 14:41:00 UTC
     abbrLocalZone.format(AboveDate)  returns 2006/05/01 10:41:00

     EST: Adding offsets for GMT-G between Nov to Mar
     abbrLocalZone.parse("2006/12/01 10:41:00") returns 2006-12-01 15:41:00 UTC
     abbrLocalZone.format(AboveDate) returns 2006/05/01 10:41:00
     */
    private static final SimpleDateFormat abbrLocalZone = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static Date iso8601DateUTC(String date){
        //abbrLocalZone.setTimeZone(TimeZone.getDefault());

        try{
            iso8601FullUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601FullUTC.parse(date);
        } catch (ParseException e){
            try{
                iso8601AbbrUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
                return iso8601AbbrUTC.parse(date);
            } catch (ParseException ex){
                return new Date(0L);
            }
        }
    }

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
        Log.d("csa.debug", msg);
    }
}


