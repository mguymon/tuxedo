package com.tobedevoured.tuxedo.api;

import com.google.inject.Inject;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.db.Db4oService;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@ByYourCommand
public class RestService implements IApiService {
    private static Logger logger = LoggerFactory.getLogger(RestService.class);

    IConfig config;
    CacheController cacheController;
    StatusController  statusController;
    RestExpress server;
    Db4oService dbService;

    @Inject
    public RestService(IConfig config, Db4oService dbService, CacheController cacheController, StatusController statusController) {
        this.config = config;
        this.dbService = dbService;
        this.cacheController = cacheController;
        this.statusController = statusController;
    }

    @Command
    public void start() {

        if ( !this.dbService.isRunning() ) {
            this.dbService.start();
        }

        Api api = this.dbService.getApi();
        api = this.dbService.getApi();
        api.version = 1;
        api.startedAt = new Date();
        this.dbService.store(api);

        logger.info("API id:{} version:{}", api.id, api.version);

        server = new RestExpress()
                .setName("API")
                .setDefaultFormat("json")
                .putResponseProcessor(Format.JSON, ResponseProcessors.json())
                .putResponseProcessor(Format.WRAPPED_JSON, ResponseProcessors.wrappedJson())
        //        .addPostprocessor(new LastModifiedHeaderPostprocessor())
                .addMessageObserver(new SimpleConsoleLogMessageObserver());


        // Status routes
        server.uri("/api/1/status.{format}", statusController)
                .action("show", HttpMethod.GET)
                .name("status_show");

        // Cache routes
        server.uri("/api/1/caches.{format}", cacheController)
                .action("index", HttpMethod.GET)
                .name("cache_index");

        server.uri("/api/1/caches.{format}", cacheController)
                .method(HttpMethod.POST);


        server.uri("/api/1/caches/{id}.{format}", cacheController)
                .action("show", HttpMethod.GET)
                .name("cache_show");


        server.uri("/api/1/caches/{id}.{format}", cacheController)
                .action("update", HttpMethod.PUT)
                .name("cache_update");


        server.bind(config.getApiPort());
    }

    public void stop() throws ServiceException {
       server.shutdown();
       dbService.stop();
    }


    public static void main(String[] args) throws RunException {
        Runner.run(args);
    }
}
