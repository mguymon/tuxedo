package com.tobedevoured.tuxedo.api;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.db.Db4oService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.util.*;

/**
 *
 */
public class CacheController {

    Db4oService dbService;

    public CacheController(Db4oService dbService) {
        this.dbService = dbService;
    }

    public String create(Request request, Response response) {
        String newId = "42"; // Assume a new object created.
        response.setResponseCreated();
        // Include the Location header...
        String locationUrl = request.getNamedUrl(HttpMethod.GET, "apiOrderUri");
        //response.addLocationHeader(LinkUtils.formatUrl(locationUrl, "orderId", newId));
        // Return the newly-created ID...
        return newId;
    }

    /**
     * Reads a requested object from persistent storage.
     *
     * @param request
     * @param response
     */
    public Map<String,Object> index(Request request, Response response) {

        Map<String,Object> data = createResponseMap("success", "001");

        List<Map<String,String>> cacheData = new ArrayList();
        List<Cache> caches = dbService.all();
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
        String id = request.getUrlDecodedHeader("id");
        UUID uuid = UUID.fromString( id );
        Cache cache = dbService.findCacheById( uuid );
        Map<String,Object> responseData = createResponseMap("success", "001");

        Map<String,Object> cacheData = new LinkedHashMap<>();
        cacheData.put("id", cache.id.toString() );
        cacheData.put("path", cache.path );
        cacheData.put("response", cache.response);
        cacheData.put("lazy", String.valueOf( cache.lazy ) );
        cacheData.put("published_at", formatDate( cache.publishedAt ) );
        cacheData.put("created_at", formatDate( cache.createdAt ) );
        cacheData.put("expired_at", formatDate(cache.expiredAt));

        responseData.put("cache", cacheData );

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


    private Map<String,Object> createResponseMap(String status, String code) {
        Map<String,Object> response = new LinkedHashMap<>();
        response.put( "status", status );
        response.put( "status_code", code );

        return response;
    }

    private String formatDate(Date date) {
        if ( date != null ) {
            return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( date );
        } else {
            return null;
        }
    }
}
