package com.tobedevoured.tuxedo.api;

import com.db4o.config.annotations.Indexed;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class Api implements Serializable {

    private static final long serialVersionUID = 2053954134407439043L;

    @Indexed
    public String id;
    public Date createdAt;
    public Date startedAt;
    public Integer version = 1;

}
