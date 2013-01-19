package com.tobedevoured.tuxedo.cache;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.TypeSafeConfig;
import com.tobedevoured.tuxedo.db.Db4oService;

public class CacheListenerTest {

    static TypeSafeConfig config1;
    static TypeSafeConfig config2;
    static TypeSafeConfig config3;
    
    static CacheListener listener1;
    static CacheListener listener2;
    static CacheListener listener3;
    
    static Db4oService dbService1;
    static Db4oService dbService2;
    static Db4oService dbService3;
   
    @BeforeClass
    public static void start() throws ServiceException {
        File file = new File("target/tuxedo-listener-test1.db4o");
        if ( file.exists() ) {
            file.delete();
        }
        config1 = new TypeSafeConfig();
        config1.dbPath = "target/tuxedo-listener-test1.db4o";
        dbService1 = new Db4oService( config1 );
        listener1 = new CacheListener(config1, dbService1);
        dbService1.start();
        listener1.start();
        
        file = new File("target/tuxedo-listener-test2.db4o");
        if ( file.exists() ) {
            file.delete();
        }
        config2 = new TypeSafeConfig();
        config2.dbPath = "target/tuxedo-listener-test2.db4o";
        dbService2 = new Db4oService( config2 );
        listener2 = new CacheListener(config2, dbService2);
        dbService2.start();
        listener2.start();
        
        file = new File("target/tuxedo-listener-test3.db4o");
        if ( file.exists() ) {
            file.delete();
        }
        config3 = new TypeSafeConfig();
        config3.dbPath = "target/tuxedo-listener-test3.db4o";
        dbService3 = new Db4oService( config3 );
        listener3 = new CacheListener(config3, dbService3);
        dbService3.start();
        listener3.start();
    }
    
    @AfterClass
    public static void stop() throws ServiceException {
        listener1.stop();
        dbService1.stop();
        
        listener2.stop();
        dbService2.stop();
        
        listener3.stop();
        dbService3.stop();
    }
    
    @Test
    public void testCacheMessage() throws InterruptedException {
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
    
    @Test
    public void testCacheMerge() throws InterruptedException {
        Cache cache = new Cache();
        cache.path = "test merge";
        listener3.publish( cache );
        
        cache.path = "test merge2";
        cache.lazy = true;
        listener2.publish( cache );
        

        cache.path = "test merge3";
        cache.publishedAt = new Date();
        listener1.publish( cache );
        
        Thread.sleep(1500);
        
        Cache queried = dbService1.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        assertEquals(cache.lazy, queried.lazy);
        assertEquals(cache.publishedAt, queried.publishedAt);
        
        queried = dbService2.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        assertEquals(cache.lazy, queried.lazy);
        assertEquals(cache.publishedAt, queried.publishedAt);
        
        queried = dbService3.findCacheByPath( cache.path ).get(0);
        assertNotNull("Cache should be found", queried);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        assertEquals(cache.lazy, queried.lazy);
        assertEquals(cache.publishedAt, queried.publishedAt);
    }
}
