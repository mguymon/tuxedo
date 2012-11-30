package com.tobedevoured.tuxedo.cassandra;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.tobedevoured.tuxedo.IService;

public class CassandraModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IService.class).to(CassandraService.class).in(Singleton.class);
        
        bind(AstyanaxContext.class).toProvider(ContextProvider.class).in(Singleton.class);
    }
    

}
