package com.tobedevoured.tuxedo.api;

import com.strategicgains.restexpress.Request;
import com.tobedevoured.tuxedo.cache.Cache;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public abstract class BaseController {

    protected Map<String,Object> createResponseMap(Status status) {
        Map<String,Object> response = new LinkedHashMap<>();
        response.put( "status", status.message );
        response.put( "status_code", status.code );
        response.put( "status_detail", "" );

        return response;
    }

    protected String formatDate(Date date) {
        if ( date != null ) {
            return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format( date );
        } else {
            return null;
        }
    }


    protected String getParam(Request request, String key) {
        List<String> params = request.getBodyFromUrlFormEncoded().get("path");
        if ( params.size() > 0 ) {
            return params.get(0);
        } else {
            return null;
        }
    }
}
