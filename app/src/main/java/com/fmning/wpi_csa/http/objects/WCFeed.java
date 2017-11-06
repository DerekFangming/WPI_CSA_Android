package com.fmning.wpi_csa.http.objects;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fangmingning on 11/3/17.
 */

public class WCFeed {

    int id;
    String title;
    String type;
    String body;
    Date createdAt;
    int ownerId;
    String ownerName;
    int coverImgId;
    int avatarId;

    public WCFeed(int id, String title, String type, String body, Date createdAt){
        this.id = id;
        this.title = title;
        this.type = type;
        this.body = body;
        this.createdAt = createdAt;
        this.ownerId = -1;
        this.ownerName = "";
        this.coverImgId = -1;
        this.avatarId = -1;
    }

    public WCFeed(){
        this(-1, "", "", "", Calendar.getInstance().getTime());
    }
}
