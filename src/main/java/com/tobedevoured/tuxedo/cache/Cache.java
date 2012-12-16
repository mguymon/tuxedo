package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.db4o.config.annotations.Indexed;

public class Cache implements Serializable {

    @Indexed
    public UUID id;
    
    @Indexed
    public String path;
    public String response;
    public boolean lazy = false;

    @Indexed
    public Date expiredAt;

    @Indexed
    public Date publishedAt;
    public Date createdAt;
    public Date updatedAt;
    
    
    public Cache() {
        this(UUID.randomUUID());
    }

    public Cache(UUID messageId) {
        this.id = messageId;
    }
}
