package com.tobedevoured.tuxedo.db;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.UuidSupport;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.cache.Cache;

@ByYourCommand
public class Db4oService implements IDbService {
    ObjectContainer rootContainer;
    IConfig config;
    
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
        List<Cache> caches = rootContainer.query(new Predicate<Cache>() {
            public boolean match(Cache cache) {
                return cache.path.equals(path);
            }
        });
        
        return caches;
    }
    
    public Cache findActiveCache(final String path) {
        Date now = new Date();
        Query query = rootContainer.query();
        query.constrain(Cache.class);
        query.descend("path").constrain(path).equal();
        query.descend("expiredAt").constrain(now).greater()
            .or( query.descend("expiredAt").constrain(null).identity());
        query.descend("publishedAt").constrain(now).smaller()
            .or( query.descend("publishedAt").constrain(null).identity());
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
        configuration.common().messageLevel(4);
        configuration.common().diagnostic().addListener(new DiagnosticToConsole());
        configuration.file().generateCommitTimestamps(true);
        rootContainer = Db4oEmbedded.openFile( configuration, config.getDbPath() );
    }

    @Command
    public void stop() {
        rootContainer.close();
        rootContainer = null;
    }
}
