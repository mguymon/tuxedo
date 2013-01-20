package com.tobedevoured.tuxedo.api;

import com.tobedevoured.tuxedo.RestClient;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 */
public class StatusControllerTest extends BaseController {
    Db4oService dbService = DependencyManager.instance.getInstance(Db4oService.class);
    RestService restService = DependencyManager.instance.getInstance(RestService.class);
    RestClient restClient = DependencyManager.instance.getInstance(RestClient.class);

    @Before
    public void start() {
        restService.start();
    }

    @After
    public void stop() throws ServiceException {
        restService.stop();
    }

    @Test
    public void show() throws IOException {
        assertEquals(1, dbService.all(Api.class).size());

        Api api = dbService.getApi();

        String response = restClient.get("/api/1/status.json");


        assertThat( response, containsString("\"status\":\"success\""));
        assertThat( response, containsString("\"status_code\":\"001\""));
        assertThat( response, containsString("\"id\":\"" + api.id + "\""));
        assertThat( response, containsString("\"version\":" + api.version));
        assertThat( response, containsString("\"started_at\":\"" + formatDate(api.startedAt) + "\""));
        assertThat( response, containsString("\"created_at\":\"" + formatDate(api.createdAt) + "\""));
    }
}
