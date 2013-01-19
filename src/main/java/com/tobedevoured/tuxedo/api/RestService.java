package com.tobedevoured.tuxedo.api;

import com.google.inject.Inject;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.db.Db4oService;
import org.jboss.netty.handler.codec.http.HttpMethod;

@ByYourCommand
public class RestService implements IApiService {

    CacheController controller;
    RestExpress server;
    Db4oService dbService;

    @Inject
    public RestService(Db4oService dbService) {
        this.dbService = dbService;
        controller = new CacheController(dbService);
    }

    @Command
    public void start() {

        if ( this.dbService.isRunning() ) {
            this.dbService.start();
        }

        server = new RestExpress()
                .setName("API")
                .setDefaultFormat("json")
                .putResponseProcessor(Format.JSON, ResponseProcessors.json())
                .putResponseProcessor(Format.WRAPPED_JSON, ResponseProcessors.wrappedJson())
        //        .addPostprocessor(new LastModifiedHeaderPostprocessor())
                .addMessageObserver(new SimpleConsoleLogMessageObserver());

        server.uri("/api/1/cache.{format}", controller)
                .action("index", HttpMethod.GET)
                .name("index");

        server.uri("/api/1/cache.{format}", controller)
                .method(HttpMethod.POST);


        server.uri("/api/1/cache/{id}.{format}", controller)
                .action("show", HttpMethod.GET)
                .name("show");


        server.uri("/api/1/cache/{id}.{format}", controller)
                .action("update", HttpMethod.PUT)
                .name("update");


        server.bind(9922);
        server.awaitShutdown();
    }

    public void stop() throws ServiceException {
       server.shutdown();
       dbService.stop();
    }


    public static void main(String[] args) throws RunException {
        Runner.run(args);
    }
}
