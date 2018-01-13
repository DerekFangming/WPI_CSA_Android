package com.fmning.wpi_csa.webService;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.objects.WCEvent;
import com.fmning.wpi_csa.webService.objects.WCFeed;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fangmingning
 * On 11/13/17.
 */

public class WCFeedManager {


    private static AsyncHttpClient client = new AsyncHttpClient();

    //This method needs to be error proof
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static void getRecentFeeds(final Context context, int limit, String checkPoint,
                                      final OnGetRecentFeedListener listener){
        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathGetRecentFeeds);
            listener.OnGetRecentFeedDone((String)mock.get(0), (List<WCFeed>)mock.get(1), (String)mock.get(2));
            return;
        }

        RequestParams params = new RequestParams();
        params.put("limit", Integer.toString(limit));
        if (checkPoint != null){
            params.put("checkPoint", checkPoint);
        }

        client.get(WCUtils.serviceBase + WCUtils.pathGetRecentFeeds, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<WCFeed> feedList = new ArrayList<>();
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnGetRecentFeedDone(error, feedList, null);
                    } else {
                        JSONArray rawList = response.getJSONArray("feedList");
                        for (int i = 0; i < rawList.length(); i++){
                            JSONObject feed = rawList.getJSONObject(i);

                            WCFeed wcFeed = new WCFeed();
                            try {
                                wcFeed.id = feed.getInt("id");
                            } catch (JSONException ignored){}

                            try {
                                wcFeed.title = feed.getString("title");
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.type = feed.getString("type");
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.createdAt = Utils.iso8601DateUTC(feed.getString("createdAt"));
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.ownerId = feed.getInt("ownerId");
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.ownerName = feed.getString("ownerName");
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.coverImgId = feed.getInt("coverImgId");
                            } catch (JSONException ignored){}
                            try {
                                wcFeed.avatarId = feed.getInt("avatarId");
                            } catch (JSONException ignored){}

                            feedList.add(wcFeed);
                        }
                        listener.OnGetRecentFeedDone("", feedList, response.getString("checkPoint"));
                    }
                } catch(JSONException e){
                    listener.OnGetRecentFeedDone(context.getString(R.string.respond_format_error),
                            feedList, null);
                } catch(Exception e){
                    listener.OnGetRecentFeedDone(context.getString(R.string.unknown_error),
                            feedList, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.logMsg(res);
                listener.OnGetRecentFeedDone(context.getString(R.string.server_down_error),
                        new ArrayList<WCFeed>(), null);
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

    public static void getFeed(final Context context, int id, final OnGetFeedListener listener) {
        if (WCUtils.localMode) {
            List<Object> mock = RequestMocker.getFakeResponse(WCUtils.pathGetFeed);
            listener.OnGetFeedDone((String)mock.get(0), (WCFeed)mock.get(1));
            return;
        }

        RequestParams params = new RequestParams();
        params.put("id", id);


        client.get(WCUtils.serviceBase + WCUtils.pathGetFeed, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.getString("error");
                    if (!error.equals("")){
                        listener.OnGetFeedDone(error, null);
                    } else {
                        int id = response.getInt("id");
                        String title = response.getString("title");
                        String type = response.getString("type");
                        String body = response.getString("body");
                        Date createdAt = Utils.iso8601DateUTC(response.getString("createdAt"));
                        WCFeed feed = new WCFeed(id, title, type, body, createdAt);

                        try {
                            JSONObject eventDic = response.getJSONObject("event");
                            int eventId = eventDic.getInt("id");
                            String eventTitle = eventDic.getString("title");
                            Date startTime = Utils.iso8601DateUTC(eventDic.getString("startTime"));
                            Date endTime = Utils.iso8601DateUTC(eventDic.getString("endTime"));
                            String location = eventDic.getString("location");

                            WCEvent event = new WCEvent(eventId, eventTitle, startTime, endTime, location);
                            event.ownerId = eventDic.getInt("ownerId");
                            event.createdAt = Utils.iso8601DateUTC(eventDic.getString("createdAt"));
                            event.description = eventDic.getString("description");
                            try {
                                event.fee = eventDic.getDouble("fee");
                            } catch (JSONException ignored){}

                            feed.event = event;

                        } catch (JSONException ignored){}

                        listener.OnGetFeedDone("", feed);
                    }
                } catch(JSONException e){
                    listener.OnGetFeedDone(context.getString(R.string.respond_format_error), null);
                } catch(Exception e){
                    listener.OnGetFeedDone(context.getString(R.string.unknown_error), null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Utils.logMsg(res);
                listener.OnGetFeedDone(context.getString(R.string.server_down_error), null);
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

    public interface OnGetRecentFeedListener {
        void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint);
    }

    public interface  OnGetFeedListener {
        void OnGetFeedDone(String error, WCFeed feed);
    }
}
