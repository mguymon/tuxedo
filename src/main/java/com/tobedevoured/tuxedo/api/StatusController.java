package com.tobedevoured.tuxedo.api;

import com.google.inject.Inject;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.tobedevoured.tuxedo.db.Db4oService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class StatusController extends BaseController {

    Db4oService dbService;

    @Inject
    public StatusController(Db4oService dbService) {
        this.dbService = dbService;
    }

    public Map<String,Object> show(Request request, Response response) {
        Api api = dbService.getApi();

        Map<String,Object> responseData = createResponseMap(Status.SUCCESS);
        responseData.put("service", createApiMap(api));

        return responseData;
    }

    protected Map<String,Object> createApiMap(Api api) {
        Map<String,Object> apiData = new LinkedHashMap<>();
        apiData.put("id", api.id);
        apiData.put("version", api.version);
        apiData.put("started_at", formatDate(api.startedAt));
        apiData.put("created_at", formatDate(api.createdAt));

        return apiData;
    }
}
