package com.tobedevoured.tuxedo.api;

import com.strategicgains.hyperexpress.util.LinkUtils;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zinger
 * Date: 12/24/12
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CacheController {

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
     * @return Best practice is to return the actual DTO or domain object here.  The Map returned from
     *         this particular implementation is for demo purposes only.
     */
    public Map<String, String> read(Request request, Response response) {
        String id = request.getUrlDecodedHeader("id", "No ID supplied");

        // Normally one would return an actual DTO or domain object instead of a Map.  While returning a
        // Map works, this particular implementation is for demonstration purposes... in leau of a domain
        // model.
        Map<String, String> result = new HashMap<String, String>();
        result.put("id", id);
        result.put("value", "something here");
        return result;
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
