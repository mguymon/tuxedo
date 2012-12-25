package com.tobedevoured.tuxedo.api;

import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.Parameters;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import com.strategicgains.restexpress.plugin.cache.CacheControlPlugin;
import com.strategicgains.restexpress.plugin.route.RoutesMetadataPlugin;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IService;
import com.tobedevoured.tuxedo.ServiceException;
import org.jboss.netty.handler.codec.http.HttpMethod;

@ByYourCommand
public class RestService implements IService {

    CacheController controller;

    public RestService() {
        controller = new CacheController();
    }

    @Command
    public void start() {
        RestExpress server = new RestExpress()
                .setName("API")
                .setDefaultFormat("json")
                .putResponseProcessor(Format.JSON, ResponseProcessors.json())
                .putResponseProcessor(Format.WRAPPED_JSON, ResponseProcessors.wrappedJson())
        //        .addPostprocessor(new LastModifiedHeaderPostprocessor())
                .addMessageObserver(new SimpleConsoleLogMessageObserver());

        server.uri("/api/1/cache.{format}", controller)
                .method(HttpMethod.POST);

        // Maps /kickstart uri with required orderId and optional format identifier
        // to the KickStartService.  Accepts only GET, PUT, DELETE HTTP methods.
        // Names this route to allow returning links from read resources in
        // KickStartService methods via call to LinkUtils.asLinks().
        server.uri("/api/1/cache/{id}.{format}", controller)
                .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
                .name("apiOrderUri")
                .parameter(Parameters.Cache.MAX_AGE, 3600);		// Cache for 3600 seconds (1 hour).
//			.flag(Flags.Cache.DONT_CACHE);					// Expressly deny cache-ability.

        new RoutesMetadataPlugin()							// Support discoverability.
                .register(server)
                .parameter(Parameters.Cache.MAX_AGE, 86400);	// Cache for 1 day (24 hours).

        new CacheControlPlugin()							// Support caching headers.
                .register(server);

        server.bind(9922);
        server.awaitShutdown();
    }

    public void stop() throws ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public static void main(String[] args) throws RunException {
        Runner.run(args);
    }
}
