package com.tobedevoured.tuxedo.cache;

import java.io.Serializable;

import com.hazelcast.core.Message;

public interface ICallback extends Serializable {

    void exec(Message<Cache> cacheEvent);
}
