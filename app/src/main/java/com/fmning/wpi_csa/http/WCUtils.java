package com.fmning.wpi_csa.http;

/**
 * Created by fangmingning on 11/1/17.
 */

public class WCUtils {

    //let serviceBase = "https://wcservice.fmning.com/" //*****************PROD
    public static final String serviceBase = "http://wc.fmning.com/"; //********************TEST
    //If enabled, most of the HTTP request will return faked local value, instead of making network calls
    public static final Boolean localMode = true;
}
