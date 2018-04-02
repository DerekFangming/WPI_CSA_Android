package com.fmning.wpi_csa.webService;

import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.objects.WCEvent;
import com.fmning.wpi_csa.webService.objects.WCFeed;
import com.fmning.wpi_csa.webService.objects.WCUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fangmingning
 * On 11/2/17.
 */

class RequestMocker {

    static List<Object> getFakeResponse(String requestPath) {
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
            case WCUtils.pathGetFeed:
                WCFeed feed = new WCFeed(9, "Title 9", "Event", "Create red by user 11 i.e. Myself",
                        Utils.iso8601DateUTC("2017-02-21T02:12:21.534Z"));
                feed.ownerId = 11;
                feed.ownerName = "Fangming Ning";
                feed.avatarId = 41;
                feed.coverImgId = 20;

                Date sTime = Utils.iso8601DateUTC("2018-04-10T22:00:00Z");
                Date eTime = Utils.iso8601DateUTC("2018-04-11T02:00:00Z");
                WCEvent event = new WCEvent(1, "Dragon night 2018", sTime, eTime, "Aldem Hall", true);
                event.ownerId = 11;
                event.createdAt = Utils.iso8601DateUTC("2017-10-15T23:01:31.732580Z");
                event.description = "This is dragon night event for 2018, hosted by CSA. There will be chinese shows and food.";
                event.fee = 0;

                feed.event = event;
                return new ArrayList<>(Arrays.asList("", event));
            case WCUtils.pathLogin:
                WCUser user = new WCUser("fning@wpi.edu", "token");
                user.emailConfirmed = true;
                user.name = "Fangming Ning";
                return new ArrayList<>(Arrays.asList("", user));
            case WCUtils.pathSaveUserDetails:
                return new ArrayList<Object>(Arrays.asList("", 1));
                //return new ArrayList<Object>(Collections.singleton(""));
            default:
                return new ArrayList<>();
        }
    }
}
