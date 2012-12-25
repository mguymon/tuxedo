package com.tobedevoured.tuxedo.api;

import com.google.gson.GsonBuilder;
import com.strategicgains.restexpress.serialization.SerializationProcessor;
import com.strategicgains.restexpress.serialization.json.DefaultJsonProcessor;
import com.strategicgains.restexpress.serialization.json.GsonTimestampSerializer;
import com.strategicgains.util.date.DateAdapterConstants;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: zinger
 * Date: 12/24/12
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonSerializationProcessor extends DefaultJsonProcessor {

    public JsonSerializationProcessor() {
        super(
                new GsonBuilder()
                        .disableHtmlEscaping()
                        .registerTypeAdapter(Date.class, new GsonTimestampSerializer())
//			.registerTypeAdapter(ObjectId.class, new GsonObjectIdSerializer())
                        .setDateFormat(DateAdapterConstants.TIMESTAMP_OUTPUT_FORMAT)
                        .create()
        );
    }
}