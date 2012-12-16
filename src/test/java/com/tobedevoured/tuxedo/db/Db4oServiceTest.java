package com.tobedevoured.tuxedo.db;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.command.DependencyManager;

public class Db4oServiceTest {

    static Db4oService service = (Db4oService)DependencyManager.instance.getInstance(IDbService.class);
    long DAY_IN_MS = 1000 * 60 * 60 * 24;
    Date sevenDaysAgo = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
    Date fourDaysAgo = new Date(System.currentTimeMillis() - (4 * DAY_IN_MS));
    Date twoDaysAgo = new Date(System.currentTimeMillis() - (2 * DAY_IN_MS));
    Date threeDaysFromNow = new Date(System.currentTimeMillis() + (3 * DAY_IN_MS));
    
    @Before
    public void startup() {
        File db = new File(service.config.getDbPath());
        if ( db.exists() ) {
            db.delete();
        }
        service.start();
    }
    
    @After
    public void stop() {
        service.stop();
    }
    
    @Test
    public void findCacheById() {
        Cache cache = new Cache();
        service.store( cache );
        
        Cache queried = service.findCacheById( cache.id );
        assertEquals(cache.id, queried.id);
    }
    
    @Test
    public void findCacheByPath() {
        Cache cache = new Cache();
        cache.path = "/a/path";
        service.store( cache );
        
        Cache queried = service.findCacheByPath( cache.path ).get(0);
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
    }
    
    @Test
    public void update() {
        Cache cache = new Cache();
        cache.path = "/a/path" ;
        service.store( cache );
        
        Cache queried = service.findCacheById( cache.id );
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        
        queried.path = "/a/path2";
        service.store( queried );
        
        queried = service.findCacheById( cache.id );
        assertEquals(cache.id, queried.id);
        assertEquals("/a/path2", queried.path);
    }
    
    @Test
    public void storedDefaultsPublishedAt() {
        Cache cache = new Cache();
        cache.path = "/default/publishedAt";
        cache.response = "no dates";
        service.store(cache);
        
        Cache dbCache = service.findActiveCache("/default/publishedAt");
        assertEquals(cache.id, dbCache.id);
        assertNotNull("A null PublishedAt should be set when stored",dbCache.publishedAt);
    }
    
    @Test 
    public void findActiveCacheWithValidPublishedAt() {
        
        Cache cache = new Cache();
        cache.path = "/active/cache";
        cache.expiredAt = sevenDaysAgo;
        cache.response = "expired 7 days ago";
        service.store(cache);
        
        cache = new Cache();
        cache.path = "/active/cache";
        cache.publishedAt = twoDaysAgo;
        cache.expiredAt = new Date();
        cache.response = "published 2 days ago but expired now";
        service.store(cache);
        
        cache = new Cache();
        cache.path = "/active/cache";
        cache.publishedAt = sevenDaysAgo;
        cache.expiredAt = new Date();
        cache.response = "published 7 days ago but expired now";
        service.store(cache);
        
        Cache recentCache = new Cache();
        recentCache.path = "/active/cache";
        recentCache.publishedAt = twoDaysAgo;
        recentCache.expiredAt = threeDaysFromNow;
        recentCache.response = "published 2 days ago and expires 3 days from now";
        service.store(recentCache);

        cache = new Cache();
        cache.path = "/active/cache";
        cache.publishedAt = fourDaysAgo;
        cache.response = "published 4 days ago";
        service.store(cache);
        
        
        Cache dbCache = service.findActiveCache("/active/cache");
        
        assertEquals( recentCache.response, dbCache.response);
        assertEquals( recentCache.id, dbCache.id);
    }
    

    @Test 
    public void findActiveCacheWithExpired() {
        Cache cache = new Cache();
        cache.path = "/active/cache";
        cache.expiredAt = sevenDaysAgo;
        cache.response = "expired 7 days ago";
        service.store(cache);
        
        cache = new Cache();
        cache.path = "/active/cache";
        cache.publishedAt = twoDaysAgo;
        cache.expiredAt = new Date();
        cache.response = "published 2 days ago but expired now";
        service.store(cache);
        
        cache = new Cache();
        cache.path = "/active/cache";
        cache.publishedAt = sevenDaysAgo;
        cache.expiredAt = new Date();
        cache.response = "published 7 days ago but expired now";
        service.store(cache);
        
        Cache dbCache = service.findActiveCache("/active/cache");
        assertNull( "All cache should be expired", dbCache );
    }
}
