package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;
import java.util.UUID;

import com.db4o.config.annotations.Indexed;

public class Cache implements Serializable {
    
    private static final long serialVersionUID = 6527934795017018018L;
    public String response;
    
    @Indexed
    public UUID messageId;
    
    public Cache() {
        this(UUID.randomUUID());
    }
    
    public Cache(UUID messageId) {
        this.messageId = messageId;
    }
}
