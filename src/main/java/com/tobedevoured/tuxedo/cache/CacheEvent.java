package com.tobedevoured.tuxedo.cache;

import com.hazelcast.core.Message;

public class CacheEvent extends Message<Cache> {

    public CacheEvent(String topicName, Cache cache) {
        super(topicName, cache);
    }

}
