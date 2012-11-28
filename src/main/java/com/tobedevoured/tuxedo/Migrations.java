package com.tobedevoured.tuxedo;

import java.util.Arrays;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

public class Migrations {
    Config config;
    
    public Migrations(Config config) {
        this.config = config;
    }
    
    public void migrate() throws MigrationException {
        // Tuxedo keyspace
        String keyspaceName = "Tuxedo";
        Cluster cluster = HFactory.getOrCreateCluster(config.getCassandraCluster(), config.getCassandraHostAndPort());
        
        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);
        
        ColumnFamilyDefinition migrationsDef = HFactory.createColumnFamilyDefinition(
                keyspaceName, "Migrations", ComparatorType.BYTESTYPE);
        
        if (keyspaceDef == null) {
                    KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
                    keyspaceName, ThriftKsDef.DEF_STRATEGY_CLASS,
                    1,  // XXX: replication factor of 1
                    Arrays.asList(migrationsDef));
            
            cluster.addKeyspace(newKeyspace, true);
        }
        
        Keyspace keyspace = HFactory.createKeyspace(keyspaceName, cluster);
       
        ColumnFamilyTemplate<String, String> template = 
                new ThriftColumnFamilyTemplate<String, String>(
                       keyspace,
                       "Migrations", 
                       StringSerializer.get(),        
                       StringSerializer.get() );
        
        //if (!versionLoaded(template, "000-response_cache")) {
        //
        //}
    }
    
    private boolean versionLoaded(ColumnFamilyTemplate<String, String> template, String version) throws MigrationException {
        boolean loaded = false;
        try {
            ColumnFamilyResult<String, String> res = template.queryColumns(version);
            if ( res != null ) {
                loaded = true;
            }
            
        } catch (HectorException e) {
            throw new MigrationException(e);
        }
        
        return loaded;
    }
}
