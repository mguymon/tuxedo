package com.tobedevoured.tuxedo.db;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.UuidSupport;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.diagnostic.NativeQueryNotOptimized;
import com.db4o.diagnostic.NativeQueryOptimizerNotLoaded;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.cache.Cache;

@ByYourCommand
public class Db4oService implements IDbService {
    private static Logger logger = LoggerFactory.getLogger(Db4oService.class);
    
    ObjectContainer rootContainer;
    IConfig config;
    boolean running = false;
    
    @Inject
    public Db4oService(IConfig config) {
        this.config = config;
        
    }
    
    public void store(Cache cache) {
        Date now = new Date();
        if ( cache.expiredAt == null && cache.publishedAt == null) {
            cache.publishedAt = now;
        }
        
        if ( cache.createdAt == null ) {
            cache.createdAt = now;
        } else {
            cache.updatedAt = now;
        }
        
        ObjectContainer container = rootContainer.ext().openSession();
        try {
            container.store(cache);
        } finally {
            container.close();
        }
    }

    public List<Cache> all() {
        Query query = rootContainer.query();
        query.constrain(Cache.class);

        return query.execute();
    }
    
    public Cache findCacheById(final UUID id) {
        Query query = rootContainer.query();
        query.constrain(Cache.class);
        query.descend("id").constrain(id).equal();

        ObjectSet<Cache> result = query.execute();
        if ( result.size() > 0 ) {
            return result.get(0);
        } else {
            return null;
        }
    }
    
    public List<Cache> findCacheByPath(final String path) {
        Query query = rootContainer.query();
        query.constrain(Cache.class);
        query.descend("path").constrain(path).equal();
        query.descend("publishedAt").orderDescending();
        return query.execute();
    }
    
    public Cache findActiveCache(final String path) {
        Date now = new Date();
        Query query = rootContainer.query();
        query.constrain(Cache.class);
        query.descend("path").constrain(path).equal();
        query.descend("expiredAt").constrain(now).greater()
            .or( query.descend("expiredAt").constrain(null).identity());
        query.descend("publishedAt").constrain(now).smaller();
        query.descend("publishedAt").orderDescending();
        
        
        ObjectSet<Cache> result = query.execute();
        
        if ( result.size() > 0 ) {
            return result.get(0);
        } else {
            return null;
        }
    }
    
    @Command
    public void start() {
        EmbeddedConfiguration configuration;
        configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new UuidSupport());
        configuration.file().generateCommitTimestamps(true);
        
        if (config.isDbDebug()) {
            configuration.common().messageLevel(4);
            configuration.common().diagnostic().addListener(new DiagnosticToConsole());
            configuration.common().diagnostic().addListener(new DiagnosticListener() {
                @Override
                public  void onDiagnostic(Diagnostic diagnostic) {
                    if(diagnostic instanceof NativeQueryNotOptimized){
                        logger.error("Query not optimized: {}", diagnostic);
                    } else  if(diagnostic instanceof NativeQueryOptimizerNotLoaded){
                        logger.error("Missing native query optimisation jars in classpath: {}", diagnostic);
                    }
                }
            });
        }
        
        rootContainer = Db4oEmbedded.openFile( configuration, config.getDbPath() );
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Command
    public void stop() {
        rootContainer.close();
        rootContainer = null;
        running = false;
    }
}
