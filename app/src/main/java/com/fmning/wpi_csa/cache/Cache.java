package com.fmning.wpi_csa.cache;

/**
 * Created by fangmingning
 * On 11/9/17.
 */

public class Cache {
    int id;
    String name;
    CacheType type;
    int mappingId;
    String value;

    public Cache(int id, String name, CacheType type, int mappingId, String value){
        this.id = id;
        this.name = name;
        this.type = type;
        this.mappingId = mappingId;
        this.value = value;
    }

}