package com.fmning.wpi_csa.http;

import android.content.Context;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.objects.WCFeed;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                            wcFeed.id = feed.getInt("id");
                            wcFeed.title = feed.getString("title");
                            wcFeed.type = feed.getString("type");
                            wcFeed.createdAt = Utils.iso8601DateUTC(feed.getString("createdAt"));
                            wcFeed.ownerId = feed.getInt("ownerId");
                            wcFeed.ownerName = feed.getString("ownerName");
                            wcFeed.coverImgId = feed.getInt("coverImgId");
                            wcFeed.avatarId = feed.getInt("avatarId");

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
        });
    }

    public interface OnGetRecentFeedListener {
        void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint);
    }
}
