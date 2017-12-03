package com.fmning.wpi_csa.http.objects;

/**
 * Created by fangmingning
 * On 11/15/17.
 */

public class WCUser {

    public int id;
    public String username;
    public String accessToken;
    public int avatarId;
    public boolean emailConfirmed;
    public String name;
    public String birthday;
    public String classOf;
    public String major;

    public WCUser(int id){
        this(id, null, null);
    }

    public WCUser(int id, String username, String accessToken){
        this.id = id;
        this.username = username;
        this.accessToken = accessToken;
        this.name = "";
        this.birthday = "";
        this.classOf = "";
        this.major = "";
    }
}
