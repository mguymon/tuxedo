package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.db4o.config.annotations.Indexed;

public class Cache implements Serializable {

    private static final long serialVersionUID = 9083954804407539043L;

    @Indexed
    public String id;
    
    @Indexed
    public String path;

    @Indexed
    public Date expiredAt;

    @Indexed
    public Date publishedAt = new Date();
    
    public String response;
    public Boolean lazy = Boolean.FALSE;
    public Date createdAt;
    public Date updatedAt;

    public void generateId() {
        id = UUID.randomUUID().toString();
    }
}
