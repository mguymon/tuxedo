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
import com.hazelcast.core.MessageListener;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.IService;
import com.tobedevoured.tuxedo.ServiceException;

@ByYourCommand
public class CacheListener implements IMessageListener {
    public static final String TOPIC = "CacheTopic";
    private static final Executor messageExecutor = Executors.newSingleThreadExecutor();
    private static Logger logger = LoggerFactory.getLogger( CacheListener.class );
    ITopic topic;
    IConfig config;
    HazelcastInstance hazelcast;
    
    @Inject
    public CacheListener(IConfig config) {
        this.config = config;
    }
    
    public void onMessage(final Message<Cache> message) {
        logger.info("Message received: {}", message.toString());
       
        messageExecutor.execute(new Runnable() {
            public void run() {
                Cache cache = message.getMessageObject();
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
        hazelcast.shutdown();
    }

}
