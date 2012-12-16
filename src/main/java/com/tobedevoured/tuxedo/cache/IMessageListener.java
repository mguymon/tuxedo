package com.tobedevoured.tuxedo.cache;

import com.hazelcast.core.MessageListener;
import com.tobedevoured.tuxedo.IService;

public interface IMessageListener extends MessageListener<CacheEvent>, IService {

}
