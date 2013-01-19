package com.tobedevoured.tuxedo.api;

import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.TypeSafeConfig;
import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;
import com.tobedevoured.tuxedo.db.IDbService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 */
public class RestServiceTest {
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

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:9922/api/1/caches.json");
        HttpResponse response = client.execute(request);

        String content = IOUtils.toString( response.getEntity().getContent() );


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

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:9922/api/1/caches/"+ cache1.id.toString() + ".json");
        HttpResponse response = client.execute(request);

        String content = IOUtils.toString( response.getEntity().getContent() );


        assertThat( content, containsString("\"status\":\"success\""));
        assertThat( content, containsString("\"status_code\":\"001\""));
        assertThat( content, containsString(cache1.id.toString()));
        assertThat( content, containsString("\"cache\":{\""));
        assertThat( content, containsString(cache1.path));
    }

}
