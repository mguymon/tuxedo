package com.tobedevoured.tuxedo.cache;

import static org.junit.Assert.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.Message;
import com.tobedevoured.tuxedo.ConfigModule;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.proxy.ProxyModule;

public class CacheListenerTest {

    static CacheListener listener = (CacheListener)DependencyManager.instance.getInstance(IMessageListener.class);
    static AtomicInteger counter =  new AtomicInteger(0);
    static final Executor messageExecutor = Executors.newSingleThreadExecutor();
    
    @Before
    public void start() throws ServiceException {
        listener.start();
    }
    
    @After
    public void stop() throws ServiceException {
        listener.stop();
    }
    
    @Test
    public void testMessage() {
        Cache cache = new Cache();
        listener.publish( cache );
    }
}
