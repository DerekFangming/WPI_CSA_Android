package com.fmning.wpi_csa.webService.objects;

import java.util.Date;

/**
 * Created by fangmingning
 * On 11/13/17.
 */

public class WCEvent {
    public int id;
    public String title;
    public String description;
    public Date startTime;
    public Date endTime;
    public String location;
    public double fee;
    public int ownerId;
    public Date createdAt;
    public boolean active;

    public WCEvent(int id, String title, Date startTime, Date endTime, String location, boolean active){
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.active = active;
        this.description = "";
        this.fee = -1;
        this.ownerId = 0;
    }
}
