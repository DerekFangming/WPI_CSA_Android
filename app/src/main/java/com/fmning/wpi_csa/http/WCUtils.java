package com.fmning.wpi_csa.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fangmingning
 * On 11/1/17.
 */

public class WCUtils {

    public static final String serviceBase = "https://wcservice.fmning.com/"; //*****************PROD
    //public static final String serviceBase = "http://wc.fmning.com/"; //********************TEST
    //If enabled, most of the HTTP request will return faked local value, instead of making network calls
    public static final Boolean localMode = false;

    /*
        Web request URL standard:
        Create: create a new object and save to db
        Update: update an existing object
        Get: retrieve an existing object
        Save: create a new object if does not exist. update if it exists
    */
    public static final String pathGetVersionInfo = "get_version_info";
    public static final String pathGetSalt = "login_for_salt";
    public static final String pathLogin = "login";
    public static final String pathRegisterSalt = "register_for_salt";
    public static final String pathRegister = "register";
    public static final String pathSaveUserDetails = "save_user_detail";
    public static final String pathSendVerificationEmail = "send_verification_email";
    public static final String pathChangePassword = "update_password";
    public static final String pathCreateReport = "create_sg_report";
    public static final String pathCreateArticle = "create_sg_article";
    public static final String pathGetImage = "get_image";
    public static final String pathSaveTUImage = "save_type_unique_image";
    public static final String pathGetRecentFeeds = "get_recent_feeds";
    public static final String pathGetEvent = "get_event";
    public static final String pathGetFeed = "get_feed";
    public static final String pathGetTicket = "get_ticket";
    public static final String pathMakePayment = "make_payment";

    public static Context context;
    public static JSONObject serverDownResponse;



    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
