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

    public static void getSaltForUser(final Context context, String username, final OnGetUserSaltListener listener){

        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathGetSalt);
            listener.OnGetUserSaltDone((String)mock.get(0), (String)mock.get(1));
            return;
        }

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}

        client.post(context, WCUtils.serviceBase + WCUtils.pathGetSalt, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnGetUserSaltDone(error, "");
                    } else {
                        listener.OnGetUserSaltDone("", response.getString("salt"));
                    }
                } catch(JSONException e){
                    listener.OnGetUserSaltDone(context.getString(R.string.respond_format_error), "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                listener.OnGetUserSaltDone(context.getString(R.string.server_down_error), "");
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

    public static void loginUser(final Context context, String username, String password, final OnLoginUserListener listener){
        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathLogin);
            listener.OnLoginUserDone((String)mock.get(0), (WCUser)mock.get(1));
            return;
        }

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
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
                        WCUser user = new WCUser(response.getInt("userId"), response.getString("username"),
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

    public static void regesterSalt(final Context context, String username, final OnRegisterSaltListener listener){

        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("offset", -4); //TODO: Remove this offset
            entity = new StringEntity(params.toString());
        }catch (JSONException | UnsupportedEncodingException ignored){}


        client.post(context, WCUtils.serviceBase + WCUtils.pathRegisterSalt, entity,
                ContentType.APPLICATION_JSON.getMimeType(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String error = response.getString("error");
                            if (!error.equals("")){
                                listener.OnRegisterSaltDone(error, "");
                            } else {
                                listener.OnRegisterSaltDone("", response.getString("salt"));
                            }
                        } catch(JSONException e){
                            listener.OnRegisterSaltDone(context.getString(R.string.respond_format_error), "");
                        } catch(Exception e){
                            listener.OnRegisterSaltDone(context.getString(R.string.unknown_error), "");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnRegisterSaltDone(context.getString(R.string.server_down_error), "");
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

    public static void register(final Context context, String username, String password, final OnRegisterListener listener){
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
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
                                WCUser user = new WCUser(response.getInt("userId"), response.getString("username"),
                                        response.getString("accessToken"));
                                user.emailConfirmed = false;
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

    public static void saveCurrentUserDetails(final Context context, String name, String birthday, String classOf,
                                              String major, String avatar, final OnSaveUserDetailsListener listener){
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
                                }catch (JSONException ignored){}
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
                                listener.OnChangePasswordDone(error, "");
                            } else {
                                listener.OnChangePasswordDone("", response.getString("accessToken"));
                            }
                        } catch(JSONException e){
                            listener.OnChangePasswordDone(context.getString(R.string.respond_format_error), "");
                        } catch(Exception e){
                            listener.OnChangePasswordDone(context.getString(R.string.unknown_error), "");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Utils.logMsg(res);
                        listener.OnChangePasswordDone(context.getString(R.string.server_down_error), "");
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

    public interface OnGetUserSaltListener {
        void OnGetUserSaltDone(String error, String salt);
    }

    public interface OnLoginUserListener {
        void OnLoginUserDone(String error, WCUser user);
    }

    public interface OnRegisterSaltListener {
        void OnRegisterSaltDone(String error, String salt);
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
        void OnChangePasswordDone(String error, String accessToken);
    }
}
