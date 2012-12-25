package com.tobedevoured.tuxedo.api;

import com.strategicgains.restexpress.response.DefaultResponseWrapper;
import com.strategicgains.restexpress.response.RawResponseWrapper;
import com.strategicgains.restexpress.response.ResponseProcessor;
import com.strategicgains.restexpress.response.ResponseWrapper;
import com.strategicgains.restexpress.serialization.SerializationProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: zinger
 * Date: 12/24/12
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseProcessors {
    private static final SerializationProcessor JSON_SERIALIZER = new JsonSerializationProcessor();
    private static final ResponseWrapper RAW_WRAPPER = new RawResponseWrapper();
    private static final ResponseWrapper WRAPPING_WRAPPER = new DefaultResponseWrapper();

    public static ResponseProcessor json() {
        return new ResponseProcessor(JSON_SERIALIZER, RAW_WRAPPER);
    }

    public static ResponseProcessor wrappedJson() {
        return new ResponseProcessor(JSON_SERIALIZER, WRAPPING_WRAPPER);
    }
}
