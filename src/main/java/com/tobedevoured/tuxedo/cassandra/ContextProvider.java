package com.tobedevoured.tuxedo.cassandra;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.tobedevoured.tuxedo.IConfig;

public class ContextProvider implements Provider<AstyanaxContext> {

    private IConfig config;
    
    @Inject
    public ContextProvider(IConfig config) {
        this.config = config;
    }
    
    @Override
    public AstyanaxContext get() {
        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
            .forCluster(config.getCassandraCluster())
            .forKeyspace(config.getCassandraKeyspace())
            .withAstyanaxConfiguration(
                    new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.NONE)
            )
        .withConnectionPoolConfiguration(
            new ConnectionPoolConfigurationImpl(config.getCassandraKeyspace() + "Pool")
            .setPort(config.getCassandraPort())
            .setMaxConnsPerHost(1)
            .setSeeds(config.getCassandraHostAndPort())
        )
        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        .buildKeyspace(ThriftFamilyFactory.getInstance());
        context.start();
        
        return context;
    }

}
