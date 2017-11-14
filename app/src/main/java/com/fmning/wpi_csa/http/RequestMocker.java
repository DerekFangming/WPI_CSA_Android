package com.fmning.wpi_csa.http;

import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fangmingning
 * On 11/2/17.
 */

public class RequestMocker {

    public static List<Object> getFakeResponse(String requestPath) {
        switch(requestPath){
            case WCUtils.pathGetVersionInfo:
                return new ArrayList<Object>(Arrays.asList("OK", "", "", "", ""));
            case WCUtils.pathGetRecentFeeds:
                WCFeed feed1 = new WCFeed(9, "Title 9", "Event", "Create red by user 11 i.e. Myself",
                        Utils.iso8601DateUTC("2017-02-21T02:12:21.534Z"));
                feed1.ownerId = 11;
                feed1.ownerName = "Fangming Ning";
                feed1.avatarId = 41;
                feed1.coverImgId = 20;

                WCFeed feed2 = new WCFeed(3, "Title 3", "Event",
                        "Moment test 3 with very long description like this hahahahahaha hahahahahahahhaha hahahahhaha wa haha very long line1line2\\nline3line4\\nline5",
                        Utils.iso8601DateUTC("2017-01-09T17:34:12.215Z"));
                feed2.ownerId = 4;
                feed2.ownerName = "Amy";
                feed2.avatarId = 13;
                feed2.coverImgId = 35;
                return new ArrayList<Object>(Arrays.asList("", new ArrayList<>(Arrays.asList(feed1, feed2)), ""));
            default:
                return null;
        }
    }
}
