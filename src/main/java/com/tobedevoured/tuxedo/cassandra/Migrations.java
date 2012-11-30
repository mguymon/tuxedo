package com.tobedevoured.tuxedo.cassandra;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.TypeSafeConfig;
import com.tobedevoured.tuxedo.IMigrations;
import com.tobedevoured.tuxedo.IConfig;

@ByYourCommand
public class Migrations implements IMigrations {
    AstyanaxContext<Keyspace> context;
    IConfig config = new TypeSafeConfig();
    
    @Inject
    public Migrations(AstyanaxContext<Keyspace> context) {
        this.context = context;
    }
    
    /* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IMigrations#createKeyspace()
     */
    @Override
    @Command
    public void createKeyspace() throws ConnectionException {
       Keyspace keyspace = context.getEntity();
       keyspace.createKeyspace(
            ImmutableMap.<String, Object>builder()
            .put("strategy_options", ImmutableMap.<String, Object>builder()
                .put("replication_factor", "1")
                .build())
            .put("strategy_class",     "SimpleStrategy")
                .build()
             );
        
    }
    
    /* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IMigrations#deleteKeyspace()
     */
    @Override
    @Command
    public void deleteKeyspace() throws ConnectionException {
       Keyspace keyspace = context.getEntity();
       keyspace.dropKeyspace();
    }
    
    public static void main(String[] args) throws RunException {
        Runner.run( args );
    }
}
