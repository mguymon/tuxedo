package com.tobedevoured.tuxedo.api;

import com.tobedevoured.tuxedo.RestClient;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.TypeSafeConfig;
import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;
import com.tobedevoured.tuxedo.db.IDbService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 */
public class CacheControllerTest {

    RestClient restClient = DependencyManager.instance.getInstance(RestClient.class);
    TypeSafeConfig config = DependencyManager.instance.getInstance(TypeSafeConfig.class);
    Db4oService dbService = (Db4oService)DependencyManager.instance.getInstance(IDbService.class);
    RestService restService = DependencyManager.instance.getInstance(RestService.class);

    @Before
    public void start() {
        File db = new File(config.getDbPath());
        if ( db.exists() ) {
            db.delete();
        }
        restService.start();
    }

    @After
    public void stop() throws ServiceException {
        restService.stop();
    }


    @Test
    public void index() throws IOException {
        Cache cache1 = new Cache();
        cache1.path = "/cache1";
        dbService.store(cache1);

        Cache cache2 = new Cache();
        cache2.path = "/cache2";
        dbService.store(cache2);

        Cache cache3 = new Cache();
        cache3.path = "/cache3";
        dbService.store(cache3);

        String content = restClient.get("/api/1/caches.json");

        assertThat( content, containsString("\"status\":\"success\""));
        assertThat( content, containsString("\"status_code\":\"001\""));
        assertThat( content, containsString("\"caches\":[{\""));
        assertThat( content, containsString(cache1.id.toString()));
        assertThat( content, containsString(cache1.path));
        assertThat( content, containsString(cache2.id.toString()));
        assertThat( content, containsString(cache1.path));
        assertThat( content, containsString(cache3.id.toString()));
        assertThat( content, containsString(cache1.path));
    }

    @Test
    public void show() throws IOException {
        Cache cache1 = new Cache();
        cache1.path = "/cache1";
        dbService.store(cache1);

        String content = restClient.get("/api/1/caches/"+ cache1.id.toString() + ".json");

        assertThat( content, containsString("\"status\":\"success\""));
        assertThat( content, containsString("\"status_code\":\"001\""));
        assertThat( content, containsString(cache1.id.toString()));
        assertThat( content, containsString("\"cache\":{\""));
        assertThat( content, containsString(cache1.path));
    }

}
