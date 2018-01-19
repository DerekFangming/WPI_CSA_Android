package com.fmning.wpi_csa.webService;

import android.content.Context;

import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.objects.WCUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fangmingning
 * On 11/1/17.
 */

public class WCUtils {

    //If set to false, all request will go to test server
    private static final Boolean prodMode = false;
    //If enabled, most of the HTTP request will return faked local value, instead of making network calls
    static final Boolean localMode = false;

    static final String serviceBase = prodMode ? "https://wcservice.fmning.com/" : "http://wc.fmning.com/";

    public static final String clientToken = "sandbox_bk8pdqf3_wnbj3bx4nwmtyz77";

    /*
        Web request URL standard:
        Create: create a new object and save to db
        Update: update an existing object
        Get: retrieve an existing object
        Save: create a new object if does not exist. update if it exists
    */
    static final String pathGetVersionInfo = "get_version_info";
    static final String pathLogin = "login";
    static final String pathRegister = "register";
    static final String pathSaveUserDetails = "save_user_detail";
    static final String pathSendVerificationEmail = "send_verification_email";
    static final String pathChangePassword = "update_password";
    static final String pathCreateReport = "create_sg_report";
    @SuppressWarnings("unused")
    static final String pathCreateArticle = "create_sg_article";
    static final String pathGetImage = "get_image";
    static final String pathSaveTUImage = "save_type_unique_image";
    static final String pathGetRecentFeeds = "get_recent_feeds";
    @SuppressWarnings("unused")
    static final String pathGetEvent = "get_event";
    static final String pathGetFeed = "get_feed";
    static final String pathGetTicket = "get_ticket";
    static final String pathCheckPaymentStatus = "check_payment_status";
    static final String pathMakePayment = "make_payment";

    public Context context;

    static void checkAndSaveAccessToken(JSONObject response) {
        try {
            String accessToken = response.getString("accessToken");
            WCService.currentUser.accessToken = accessToken;
            Utils.setParam(Utils.savedAccessToken, accessToken);// Is it really good to involve Utils here?
        } catch(Exception ignored){}
    }
}
