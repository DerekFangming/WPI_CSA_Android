package com.fmning.wpi_csa.webService.objects;

/**
 * Created by fangmingning
 * On 11/15/17.
 */

public class WCUser {

    public String username;
    public String accessToken;
    public int avatarId;
    public boolean emailConfirmed;
    public String name;
    public String birthday;
    public String classOf;
    public String major;


    public WCUser(String username, String accessToken){
        this.username = username;
        this.accessToken = accessToken;
        this.name = "";
        this.birthday = "";
        this.classOf = "";
        this.major = "";

        this.avatarId = -1;
    }
}
