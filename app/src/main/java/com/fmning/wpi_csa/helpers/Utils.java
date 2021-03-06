package com.fmning.wpi_csa.helpers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.activities.MainTabActivity;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.cache.Database;
import com.fmning.wpi_csa.webService.WCService;
import com.fmning.wpi_csa.webService.WCUserManager;
import com.fmning.wpi_csa.webService.objects.WCUser;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by fangmingning
 * On 11/2/17.
 */

public class Utils {

    //This is the context from MainTabActivity
    //This is used to get screen dimentions, access strings, etc.
    //This is NOT used to show UI elements since it may NOT be the current active activity
    private static Context mainContext;

    //Format: AppMajorVerion.AppSubVersion.ContentVersion
    //Update this number results server version update
    private static final String baseVersion = "1.10.001";

    //All application parameters are declared here
    private static final String appVersion = "appVersion";
    private static final String appStatus = "appStatus";
    public static final String reportEmail = "email";
    public static final String savedUsername = "username";
    public static final String savedPassword = "password";
    public static final String savedAccessToken = "accessToken";
    @SuppressWarnings("unused")
    public static final String localTitle = "title";
    @SuppressWarnings("unused")
    public static final String localArticle = "article";

    //The padding values will get initiated before any segments are created
    public static int padding15;
    public static int padding50;
    public static int padding72;
    public static int paddingFullWidth;

    private static ProgressDialog loadingDialog;

    public static AppMode appMode = AppMode.OFFLINE;
    public static List<Integer> menuOrderList = new ArrayList<>();

    public static void initialize(Context context) {
        mainContext = context;
        DisplayMetrics window = mainContext.getResources().getDisplayMetrics();
        padding15 = (int)(window.density * 15);
        padding50 = (int)(window.density * 50);
        padding72 = (int)(window.density * 72);
        paddingFullWidth = window.widthPixels;
    }


    public static void checkVerisonInfoAndLoginUser(final Context context, final boolean showAlert){
        if (showAlert) {
            showLoadingIndicator(context);
        }

        String versionToCheck = baseVersion;
        String version = getParam(appVersion);
        if (version != null) {
            versionToCheck = version;
            String[] versionArr = version.split("\\.");
            if (versionArr.length != 3) {                                    //Corrupted data
                versionToCheck = baseVersion;
                initializeApp();
            } else if (!versionArr[1].equals(baseVersion.split("\\.")[1])) { //Software version mismatch
                versionToCheck = baseVersion;
                initializeApp();// TODO: merge top if nothing special
            }
        } else {//First time install
            initializeApp();
        }

        String status = getParam(appStatus);
        if (status != null && !status.equals("OK")) {
            return; //TODO: Any friendly message?
        }

        WCService.checkSoftwareVersion(context, versionToCheck, new WCService.OnCheckSoftwareVersionListener() {
            @SuppressWarnings("IfCanBeSwitch")
            @Override
            public void OnCheckSoftwareVersionDone(String status, String title, String msg, String updates, final String version) {
                appMode = AppMode.LOGIN;
                if (status.equals("OK")) {
                    dismissIndicatorAndTryLogin(context, showAlert);
                } else if (status.equals("CU")) {
                    setParam(appVersion, version);
                    Database.run(context, updates);
                    dismissIndicatorAndTryLogin(context, showAlert);
                } else if (status.equals("BM")) {
                    if (!updates.equals("")) {
                        Database.run(context, updates);
                    }

                    hideLoadingIndicator();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title)
                            .setCancelable(false)
                            .setMessage(msg)
                            .setPositiveButton(context.getString(R.string.remind_later), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String currVersion = version.length() > 3 ? version.substring(version.length() - 3) : version;
                                    try {
                                        int prevVersion = Integer.parseInt(currVersion) - 1;
                                        String prevVersionStr = version.substring(0, version.length() - 3) +
                                                String.format(Locale.getDefault(), "%03d", prevVersion);
                                        setParam(appVersion, prevVersionStr);
                                    } catch (NumberFormatException e) {
                                        setParam(appVersion, version);//TODO: Do something here
                                    }
                                }
                            })
                            .setNegativeButton(context.getString(R.string.never_show_again), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    setParam(appVersion, version);
                                }
                            })
                            .show();
                    dismissIndicatorAndTryLogin(context, showAlert);
                } else if (status.equals("AU")) {
                    hideLoadingIndicator();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title)
                            .setCancelable(false)
                            .setMessage(msg)
                            .setPositiveButton(context.getString(R.string.remind_later), null)
                            .setNegativeButton(context.getString(R.string.never_show_again), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    setParam(appStatus, "");
                                }
                            })
                            .show();
                } else {
                    hideLoadingIndicator();
                    processErrorMessage(context, status, showAlert);
                }

            }
        });

    }

    private static void initializeApp(){
        setParam(appStatus, "OK");
        setParam(appVersion, baseVersion);
        CacheManager.localDirInitiateSetup(mainContext);
    }

    private static void dismissIndicatorAndTryLogin(final Context context, final boolean showAlert){
        String username = getParam(savedUsername);
        String password = getParam(savedPassword);
        final String accessToken = getParam(savedAccessToken);
        if(username != null && !username.equals("") && password != null && !password.equals("")){
            //Utils.logMsg("Migration started!");
            //migration starts
            Utils.deleteParam(savedUsername);
            Utils.deleteParam(savedPassword);
            WCUserManager.loginMigration(context, username, password, new WCUserManager.OnLoginMigrationListener() {
                @Override
                public void OnMigrationDone(String error, final String migratedAccessToken) {
                    if (error.equals("")) {
                        WCUserManager.loginUser(context, migratedAccessToken, new WCUserManager.OnLoginUserListener() {
                            @Override
                            public void OnLoginUserDone(String error, WCUser user) {
                                if (error.equals("")){
                                    appMode = AppMode.LOGGED_ON;
                                    hideLoadingIndicator();
                                    Utils.logMsg("Migration done!!" + migratedAccessToken);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));
                                } else {
                                    appMode = AppMode.LOGIN;
                                    hideLoadingIndicator();
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));
                                }
                            }
                        });
                    } else {
                        appMode = AppMode.LOGIN;
                        hideLoadingIndicator();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));
                    }
                }
            });

        } else if (accessToken != null && !accessToken.equals("")) {
            WCUserManager.loginUser(context, accessToken, new WCUserManager.OnLoginUserListener() {
                @Override
                public void OnLoginUserDone(String error, WCUser user) {
                    if (error.equals("")){
                        appMode = AppMode.LOGGED_ON;
                        hideLoadingIndicator();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));
                    } else {
                        hideLoadingIndicator();
                        processErrorMessage(context, error, showAlert);
                    }
                }
            });
        } else {
            hideLoadingIndicator();
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));
        }
    }

    public static void processErrorMessage(Context context, String errMsg, boolean showAlert){
        if (errMsg.equals(context.getString(R.string.server_down_error))){
            WCService.currentUser = null;
            appMode = AppMode.OFFLINE;
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("reloadUserCell"));

            if (showAlert) {
                showAlertMessage(context, errMsg);
            } else {
                //Utils.logMsg("server down but not shown");
            }
        } else {
            showAlertMessage(context, errMsg);
        }
    }

    public static void showAlertMessage(Context context, String alert){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false).setTitle(null).setMessage(alert)
                .setPositiveButton(context.getString(R.string.ok), null).show();
    }

    @SuppressWarnings("ConstantConditions")
    public static void showLoadingIndicator(Context context){
        if (loadingDialog != null){
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
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

    public static boolean isEmailAddress(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String checkPasswordStrength(String password){
        if (password.length() < 6) {
            return mainContext.getString(R.string.password_too_short);
        } else {
            if (password.matches(".*[a-zA-Z].*")) {
                if (password.matches(".*[0-9].*")) {
                    return "";
                } else {
                    return mainContext.getString(R.string.password_no_number);
                }
            } else {
                return mainContext.getString(R.string.password_no_letter);
            }
        }
    }

    //Since android preference is activity specific, all preferences are stored under MainTabActivity
    public static String getParam(String key){
        return ((MainTabActivity) mainContext).getPreferences(MODE_PRIVATE).getString(key, null);
    }

    public static void setParam(String key, String value){
        ((MainTabActivity) mainContext).getPreferences(MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public static void deleteParam(String key){
        ((MainTabActivity) mainContext).getPreferences(MODE_PRIVATE).edit().remove(key).apply();
    }

    public static Map<String, String> getHtmlAttributes (String string) {
        Map<String, String> dic = new HashMap<>();
        Matcher m = Pattern.compile("\\w+\\s?=\\s?\".*?\"").matcher(string);

        while (m.find()) {
            String match = m.group(0);
            String key = match.split("=")[0].trim();
            String value = match.replace(key, "")
                    .replace("=", "").replaceAll("\"", "").trim();

            dic.put(key, value);
        }
        return dic;
    }

    /**
     UTC Time formatter that converts iso-8601 with millisecond.
     Mainly used to convert full iso-8601 time string from server to date in UTC timezone
     Examples:
     iso8601DateUTC("2017-01-09T17:34:12.215Z") returns UTC Date 2017-01-09 17:34:12
     iso8601FullUTC.format(AboveDate) returns 2017-01-09T12:34:12.215-05:00
     */
    private static final SimpleDateFormat iso8601FullUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault());

    /**
     UTC Time formatter that converts iso-8601 without millisecond.
     Mainly used to convert abbreviated iso-8601 time string from server to date in UTC timezone
     Examples:
     iso8601DateUTC("2017-01-09T17:34:12Z") returns UTC Date 2017-01-09 17:34:12
     iso8601AbbrUTC.format(AboveDate) returns 2017-01-09T17:34:12Z
     */
    private static final SimpleDateFormat iso8601AbbrUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.getDefault());

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
    private static final SimpleDateFormat abbrLocalZone = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
            Locale.getDefault());

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

    public static String dateToString(Date date){
        return abbrLocalZone.format(date);
    }

    public static String convertToWCImageId(int id) {
        return "WCImage_" + Integer.toString(id);
    }

    public static Bitmap createImage(int colorId) {
        Bitmap image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        image.eraseColor(colorId);
        return image;
    }

    public static int compressRateForSize(Bitmap bitmap, int target) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        int fullSize = stream.toByteArray().length;
        int rate = fullSize / target;

        if (rate == 0) {
            return 100;
        } else if (rate <= 2) {
            return 90;
        } else if (rate <= 4) {
            return 60;
        } else if (rate <= 8) {
            return 20;
        } else {
            return 0;
        }
    }

    public static void logMsg(String msg){
        Log.wtf("csa.debug", msg);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void logLong(String content) {
        if (content.length() > 4000) {
            Log.wtf("csa.debug", content.substring(0, 4000));
            logLong(content.substring(4000));
        } else {
            Log.wtf("csa.debug", content);
        }
    }
}


