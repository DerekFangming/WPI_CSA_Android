package com.fmning.wpi_csa.webService;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.objects.WCUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by fangmingning
 * On 11/20/17.
 */

public class WCUserManager {

    private static AsyncHttpClient client = new AsyncHttpClient();



    public static void loginUser(final Context context, String accessToken, final OnLoginUserListener listener){
        loginUser(context, accessToken, null, null, listener);
    }

    public static void loginUser(final Context context, String username, String password, final OnLoginUserListener listener){
        loginUser(context, null, username, password, listener);
    }

    private static void loginUser(final Context context, String accessToken, String username, String password, final OnLoginUserListener listener){
        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathLogin);
            listener.OnLoginUserDone((String)mock.get(0), (WCUser)mock.get(1));
            return;
        }

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            if (accessToken != null) {
                params.put("accessToken", accessToken);
            } else {
                params.put("username", username);
                params.put("password", password);
            }
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathLogin, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnLoginUserDone(error, null);
                    } else {
                        WCUser user = new WCUser(response.getString("username"),
                                response.getString("accessToken"));
                        user.emailConfirmed = response.getBoolean("emailConfirmed");

                        try{
                            user.name = response.getString("name");
                        }catch(JSONException ignored){}
                        try{
                            user.birthday = response.getString("birthday");
                        }catch(JSONException ignored){}
                        try{
                            user.classOf = response.getString("year");
                        }catch(JSONException ignored){}
                        try{
                            user.major = response.getString("major");
                        }catch(JSONException ignored){}
                        try{
                            user.avatarId = response.getInt("avatarId");
                        }catch(JSONException ignored){}

                        if (user.name.equals("")){user.name = "Unknown";}

                        WCService.currentUser = user;
                        WCUtils.checkAndSaveAccessToken(response);
                        listener.OnLoginUserDone("", user);
                    }
                } catch(JSONException e){
                    listener.OnLoginUserDone(context.getString(R.string.respond_format_error), null);
                } catch(Exception e){
                    listener.OnLoginUserDone(context.getString(R.string.unknown_error), null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.logMsg(res);
                listener.OnLoginUserDone(context.getString(R.string.server_down_error), null);
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

    public static void register(final Context context, final String username, String password, final String name, final String birthday,
                                final String classOf, final String major, String avatar, final OnRegisterListener listener){
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
            params.put("name", name);
            if (birthday != null){
                params.put("birthday", birthday);
            }
            if (classOf != null){
                params.put("year", classOf);
            }
            if (major != null){
                params.put("major", major);
            }
            if (avatar != null) {
                params.put("avatar", avatar);
            }
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathRegister, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnRegisterDone(error, null);
                            } else {
                                WCUser user = new WCUser(username, response.getString("accessToken"));
                                user.emailConfirmed = false;

                                user.name = name == null ? "" : name;
                                user.birthday = birthday == null ? "" : birthday;
                                user.classOf = classOf == null ? "" : classOf;
                                user.major = major == null ? "" : major;

                                int imageId = -1;
                                try {
                                    imageId = response.getInt("imageId");
                                    user.avatarId = imageId;
                                }catch (JSONException ignored){}

                                WCService.currentUser = user;
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnRegisterDone("", user);
                            }
                        } catch(JSONException e){
                            listener.OnRegisterDone(context.getString(R.string.respond_format_error), null);
                        } catch(Exception e){
                            listener.OnRegisterDone(context.getString(R.string.unknown_error), null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnRegisterDone(context.getString(R.string.server_down_error), null);
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

    //version 1.10 migration
    public static void loginMigration(final Context context, String username, String password, final OnLoginMigrationListener listener){
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + "login_migration", entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnMigrationDone(error, null);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnMigrationDone("", response.getString("accessToken"));
                            }
                        } catch(JSONException e){
                            listener.OnMigrationDone(context.getString(R.string.respond_format_error), null);
                        } catch(Exception e){
                            listener.OnMigrationDone(context.getString(R.string.unknown_error), null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnMigrationDone(context.getString(R.string.server_down_error), null);
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

    public static void saveCurrentUserDetails(final Context context, final String name, final String birthday, final String classOf,
                                              final String major, String avatar, final OnSaveUserDetailsListener listener){
        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathSaveUserDetails);
            listener.OnSaveUserDetailsDone((String)mock.get(0), (int)mock.get(1));
            return;
        }

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            if (name != null){
                params.put("name", name);
            }
            if (birthday != null){
                params.put("birthday", birthday);
            }
            if (classOf != null){
                params.put("year", classOf);
            }
            if (major != null){
                params.put("major", major);
            }
            if (avatar != null) {
                params.put("avatar", avatar);
            }

            if (params.length() == 1){
                listener.OnSaveUserDetailsDone(context.getString(R.string.user_detail_update_error), -1);
            }

            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathSaveUserDetails, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnSaveUserDetailsDone(error, -1);
                            } else {
                                int imageId = -1;
                                try {
                                    imageId = response.getInt("imageId");
                                    WCService.currentUser.avatarId = imageId;
                                }catch (JSONException ignored){}
                                WCUtils.checkAndSaveAccessToken(response);
                                WCService.currentUser.name = name == null ? "" : name;
                                WCService.currentUser.birthday = birthday == null ? "" : birthday;
                                WCService.currentUser.classOf = classOf == null ? "" : classOf;
                                WCService.currentUser.major = major == null ? "" : major;
                                listener.OnSaveUserDetailsDone("", imageId);
                            }
                        } catch(JSONException e){
                            listener.OnSaveUserDetailsDone(context.getString(R.string.respond_format_error), -1);
                        } catch(Exception e){
                            listener.OnSaveUserDetailsDone(context.getString(R.string.unknown_error), -1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnSaveUserDetailsDone(context.getString(R.string.server_down_error), -1);
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

    public static void sendEmailConfirmation(final Context context, final OnSendEmailConfirmationListener listener){

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathSendVerificationEmail, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnSendEmailConfirmationDone(error);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnSendEmailConfirmationDone("");
                            }
                        } catch(JSONException e){
                            listener.OnSendEmailConfirmationDone(context.getString(R.string.respond_format_error));
                        } catch(Exception e){
                            listener.OnSendEmailConfirmationDone(context.getString(R.string.unknown_error));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnSendEmailConfirmationDone(context.getString(R.string.server_down_error));
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

    public static void changePassword(final Context context, String oldPass, String newPass, final OnChangePasswordListener listener){
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("accessToken", WCService.currentUser.accessToken);
            params.put("oldPwd", oldPass);
            params.put("newPwd", newPass);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathChangePassword, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnChangePasswordDone(error);
                            } else {
                                WCUtils.checkAndSaveAccessToken(response);
                                listener.OnChangePasswordDone("");
                            }
                        } catch(JSONException e){
                            listener.OnChangePasswordDone(context.getString(R.string.respond_format_error));
                        } catch(Exception e){
                            listener.OnChangePasswordDone(context.getString(R.string.unknown_error));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnChangePasswordDone(context.getString(R.string.server_down_error));
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

    public interface OnLoginUserListener {
        void OnLoginUserDone(String error, WCUser user);
    }

    public interface OnRegisterListener {
        void OnRegisterDone(String error, WCUser user);
    }

    public interface OnSaveUserDetailsListener {
        void OnSaveUserDetailsDone(String error, int imageId);
    }

    public interface OnSendEmailConfirmationListener {
        void OnSendEmailConfirmationDone(String error);
    }

    public interface OnChangePasswordListener {
        void OnChangePasswordDone(String error);
    }

    public interface OnLoginMigrationListener {
        void OnMigrationDone(String error, String accessToken);
    }
}
