package com.tobedevoured.tuxedo.cassandra;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.netflix.astyanax.AstyanaxContext;
import com.tobedevoured.tuxedo.IService;

public class CassandraModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ICassandraService.class).to(CassandraService.class).in(Singleton.class);
        
        bind(AstyanaxContext.class).toProvider(ContextProvider.class).in(Singleton.class);
    }
    

}
