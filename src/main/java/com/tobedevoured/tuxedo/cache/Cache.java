package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;
import java.util.UUID;

import com.db4o.config.annotations.Indexed;

public class Cache implements Serializable {

    @Indexed
    public UUID id;
    
    @Indexed
    public String path;
    public String response;
    
    
    public Cache() {
        this(UUID.randomUUID());
    }
    
    public Cache(UUID messageId) {
        this.id = messageId;
    }
}
