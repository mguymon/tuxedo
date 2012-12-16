package com.tobedevoured.tuxedo.cache;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.db.Db4oService;

@ByYourCommand
public class CacheListener implements IMessageListener {
    public static final String TOPIC = "CacheTopic";
    private static final Executor messageExecutor = Executors.newSingleThreadExecutor();
    private static Logger logger = LoggerFactory.getLogger( CacheListener.class );
    ITopic topic;
    IConfig config;
    HazelcastInstance hazelcast;
    Db4oService dbService;
    
    @Inject
    public CacheListener(IConfig config, Db4oService dbService) {
        this.config = config;
        this.dbService = dbService;
    }
    
    public void onMessage(final Message<CacheEvent> message) {
        logger.info("Message received: {}", message.toString());
        final CacheEvent cacheEvent = message.getMessageObject();
        messageExecutor.execute(new Runnable() {
            public void run() {
                Cache cache = cacheEvent.getMessageObject();
                Cache dbCache = dbService.findCacheById(cache.id);
                
                // manually merge existing Cache
                if ( dbCache != null ) {
                    if (cache.lazy != null) {
                        dbCache.lazy = cache.lazy;
                    }
                    
                    if ( cache.path != null ) {
                        dbCache.path = cache.path;
                    }
                    
                    // Dates have to be set, since null is valid
                    dbCache.publishedAt = cache.publishedAt;
                    dbCache.expiredAt = cache.expiredAt;
                    
                    if ( cache.response !=null ) {
                        dbCache.response = cache.response;
                    }
                    cache = dbCache;
                }
                
                dbService.store(cache);
            }
        });
    
    }
    
    public void publish(Cache cache) {
        topic.publish (new CacheEvent(TOPIC, cache ));
    }
   
    @Command
    public void start() throws ServiceException {
        com.hazelcast.config.Config cfg = new com.hazelcast.config.Config();
        
        hazelcast = Hazelcast.newHazelcastInstance(cfg);
        topic = hazelcast.getTopic(TOPIC);
        topic.addMessageListener(this);
    }

    @Command
    public void stop() throws ServiceException {
        topic.removeMessageListener(this);
    }

}
