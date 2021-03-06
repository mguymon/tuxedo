package com.tobedevoured.tuxedo.db;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class DbModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(IDbService.class).toProvider(DbProvider.class);
        bind(Db4oService.class).toProvider(DbProvider.class);
    }

}
