package com.tobedevoured.tuxedo.api;

import com.google.inject.Inject;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.db.Db4oService;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

/**
 *
 */
public class CacheController extends BaseController {

    Db4oService dbService;

    @Inject
    public CacheController(Db4oService dbService) {
        this.dbService = dbService;
    }

    /**
     * Reads a requested object from persistent storage.
     *
     * @param request
     * @param response
     */
    public Map<String,Object> index(Request request, Response response) {

        Map<String,Object> data = createResponseMap(Status.SUCCESS);

        List<Map<String,String>> cacheData = new ArrayList();
        List<Cache> caches = dbService.all(Cache.class);
        for ( Cache cache: caches ) {
            Map<String,String> map = new LinkedHashMap<>();
            map.put("id", cache.id.toString() );
            map.put("path", cache.path );
            map.put("published_at", formatDate( cache.publishedAt ) );
            map.put("expired_at", formatDate(cache.expiredAt));
            cacheData.add( map );
        }

        data.put("caches", cacheData );

        return data;
    }

    public Map<String,Object> show(Request request, Response response) {
        Map<String,Object> responseData = null;
        String id = request.getUrlDecodedHeader("id");
        if ( id != null) {
            Cache cache = dbService.findCacheById( id );
            responseData = createResponseMap(Status.SUCCESS);

            Map<String, Object> cacheData = createCacheMap(cache);

            responseData.put("cache", cacheData );

        } else {
            responseData = createResponseMap(Status.INVALID_ID);
            responseData.put("status_detail", "ID was missing");
        }
        return responseData;
    }

    public Map<String, Object> create(Request request, Response response) {
        Cache cache = new Cache();

        // set cache vals from param
        // store cache
        // send to cluster
        cache.path = getParam(request,"path");
        cache.lazy = "true".equalsIgnoreCase( getParam(request,"lazy"));

        dbService.store(cache);

        Map<String,Object> responseData = createResponseMap(Status.SUCCESS);
        responseData.put( "cache", createCacheMap(cache) );

        return responseData;
    }

    public void update(Request request, Response response) {
        // Use this ONLY if not using wrapped responses.  Wrapped responses WILL contain content.
//		response.setResponseNoContent();
    }

    public void delete(Request request, Response response) {
        // Use this ONLY if not using wrapped responses.  Wrapped responses WILL contain content.
//		response.setResponseNoContent();
    }

    protected Map<String, Object> createCacheMap(Cache cache) {
        Map<String,Object> cacheData = new LinkedHashMap<>();
        cacheData.put("id", cache.id.toString() );
        cacheData.put("path", cache.path );
        cacheData.put("response", cache.response);
        cacheData.put("lazy", String.valueOf(cache.lazy) );
        cacheData.put("published_at", formatDate( cache.publishedAt ) );
        cacheData.put("expired_at", formatDate(cache.expiredAt));
        cacheData.put("created_at", formatDate( cache.createdAt ) );
        cacheData.put("update_at", formatDate( cache.updatedAt ) );
        return cacheData;
    }
}
