package com.fmning.wpi_csa.objects;

import com.fmning.wpi_csa.cache.CacheType;

/**
 * Created by fangmingning
 * On 11/9/17.
 */

@SuppressWarnings("WeakerAccess")
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