package com.tobedevoured.tuxedo.db;

import java.util.List;
import java.util.UUID;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.UuidSupport;
import com.db4o.query.Predicate;
import com.google.inject.Inject;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.cache.Cache;

@ByYourCommand
public class Db4oService implements IDbService {
    ObjectContainer rootContainer;
    IConfig config;
    EmbeddedConfiguration configuration;
    
    @Inject
    public Db4oService(IConfig config) {
        this.config = config;
        
        configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new UuidSupport());
        configuration.file().generateCommitTimestamps(true);
        
    }
    
    public void save(Object object) {
        ObjectContainer container = rootContainer.ext().openSession();
        try {
            container.store(object);
        } finally {
            container.close();
        }
    }
    
    public Cache findCacheByMessageId(final UUID messageId) {
        List<Cache> caches = rootContainer.query(new Predicate<Cache>() {
            public boolean match(Cache cache) {
                return cache.messageId.equals(messageId);
            }
        });
        
        if ( caches.size() > 0 ) {
            return caches.get(0);
        } else {
            return null;
        }
    }
    
    @Command
    public void start() {
        rootContainer = Db4oEmbedded.openFile( configuration, config.getDbPath() );
    }

    @Command
    public void stop() {
        rootContainer.close();
    }
}
