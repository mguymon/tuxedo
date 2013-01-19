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
    public List<Map<String,String>> index(Request request, Response response) {

        List<Map<String,String>> data = new ArrayList();
        List<Cache> caches = dbService.all();
        for ( Cache cache: caches ) {
            Map<String,String> map = new LinkedHashMap<>();
            map.put("id", cache.id.toString() );
            map.put("path", cache.path );
            map.put("published_at", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( cache.publishedAt ) );
            map.put("expired_at", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( cache.expiredAt ) );
            data.add( map );
        }

        return data;
    }

    public Map<String,String> show(Request request, Response response) {
        String id = request.getUrlDecodedHeader("id");
        UUID uuid = UUID.fromString( id );
        Cache cache = dbService.findCacheById( uuid );
        Map<String,String> map = new LinkedHashMap<>();
        map.put("id", cache.id.toString() );
        map.put("path", cache.path );
        map.put("response", cache.response);
        map.put("lazy", String.valueOf( cache.lazy ) );
        map.put("published_at", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( cache.publishedAt ) );
        map.put("created_at", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( cache.createdAt ) );
        map.put("expired_at", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( cache.expiredAt ) );

        return map;
    }

    public void update(Request request, Response response) {
        // Use this ONLY if not using wrapped responses.  Wrapped responses WILL contain content.
//		response.setResponseNoContent();
    }

    public void delete(Request request, Response response) {
        // Use this ONLY if not using wrapped responses.  Wrapped responses WILL contain content.
//		response.setResponseNoContent();
    }
}
