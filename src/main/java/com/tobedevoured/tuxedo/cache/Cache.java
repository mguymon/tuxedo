package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.db4o.config.annotations.Indexed;

public class Cache implements Serializable {

    private static final long serialVersionUID = 9083954804407539043L;

    @Indexed
    public UUID id;
    
    @Indexed
    public String path;

    @Indexed
    public Date expiredAt;

    @Indexed
    public Date publishedAt;
    
    public String response;
    public Boolean lazy;
    public Date createdAt;
    public Date updatedAt;
    
    
    public Cache() {
        this(UUID.randomUUID());
    }

    public Cache(UUID messageId) {
        this.id = messageId;
        this.publishedAt = new Date();
        this.lazy = Boolean.FALSE;
    }
}
