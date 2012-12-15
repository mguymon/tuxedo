package com.tobedevoured.tuxedo.db;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.command.DependencyManager;

public class Db4oServiceTest {

    static Db4oService service = (Db4oService)DependencyManager.instance.getInstance(IDbService.class);
    
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
        service.save( cache );
        
        Cache queried = service.findCacheById( cache.id );
        assertEquals(cache.id, queried.id);
    }
    
    @Test
    public void findCacheByPath() {
        Cache cache = new Cache();
        cache.path = "/a/path";
        service.save( cache );
        
        Cache queried = service.findCacheByPath( cache.path );
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
    }
    
    public void update() {
        Cache cache = new Cache();
        cache.path = "/a/path";
        service.save( cache );
        
        Cache queried = service.findCacheByPath( cache.path );
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
        
        cache.path = "/a/path2";
        service.save( cache );
        
        queried = service.findCacheByPath( cache.path );
        assertEquals(cache.id, queried.id);
        assertEquals(cache.path, queried.path);
    }
}
