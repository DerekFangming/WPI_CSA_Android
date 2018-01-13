package com.fmning.wpi_csa.webService.objects;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fangmingning
 * On 11/3/17.
 */

public class WCFeed {

    public int id;
    public String title;
    public String type;
    public String body;
    public Date createdAt;
    public int ownerId;
    public String ownerName;
    public int coverImgId;
    public int avatarId;

    public WCEvent event;

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
