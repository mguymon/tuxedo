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
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.TypeSafeConfig;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;
import com.tobedevoured.tuxedo.proxy.ProxyModule;

public class CacheListenerTest {

    TypeSafeConfig config1;
    TypeSafeConfig config2;
    TypeSafeConfig config3;
    
    CacheListener listener1;
    CacheListener listener2;
    CacheListener listener3;
    
    Db4oService dbService1;
    Db4oService dbService2;
    Db4oService dbService3;
   
    @Before
    public void start() throws ServiceException {
        config1 = new TypeSafeConfig();
        config1.dbPath = "target/tuxedo-listener-test1.db4o";
        dbService1 = new Db4oService( config1 );
        listener1 = new CacheListener(config1, dbService1);
        dbService1.start();
        listener1.start();
        
        config2 = new TypeSafeConfig();
        config2.dbPath = "target/tuxedo-listener-test2.db4o";
        dbService2 = new Db4oService( config2 );
        listener2 = new CacheListener(config2, dbService2);
        dbService2.start();
        listener2.start();
        
        config3 = new TypeSafeConfig();
        config3.dbPath = "target/tuxedo-listener-test3.db4o";
        dbService3 = new Db4oService( config3 );
        listener3 = new CacheListener(config3, dbService3);
        dbService3.start();
        listener3.start();
    }
    
    @After
    public void stop() throws ServiceException {
        listener1.stop();
        dbService1.stop();
        
        listener2.stop();
        dbService2.stop();
        
        listener3.stop();
        dbService3.stop();
    }
    
    @Test
    public void testMessage() throws InterruptedException {
        Cache cache = new Cache();
        cache.path = "test path" ;
        listener1.publish( cache );
        
        Thread.sleep(500);
        
        Cache queried = dbService1.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        
        queried = dbService2.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        
        queried = dbService3.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
    }
}
