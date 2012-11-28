package com.tobedevoured.tuxedo;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

public class ResponseCache {

    public static final String COLUMN_FAMILY = "ResponseCache";
    private Config config;
    
    public ResponseCache( Config config ) {
        this.config = config;
    }
    
    public String getResponse(String path) throws HectorException {
        Cluster cluster = HFactory.getOrCreateCluster(config.getCassandraCluster(), config.getCassandraHostAndPort());
        
        Keyspace keyspace = HFactory.createKeyspace(config.getCassandraKeyspace(), cluster);
        ColumnFamilyTemplate<String, String> template = 
                new ThriftColumnFamilyTemplate<String, String>(
                       keyspace,
                       COLUMN_FAMILY,
                       StringSerializer.get(),
                       StringSerializer.get() );
        
        ColumnFamilyResult<String, String> result = template.queryColumns(path);
        
        String response = null;
        if ( result != null ) {
            response = result.getString("response");
        }
        
        return response;
    }
    
    public void cacheResponse(String path, String response) throws HectorException {
        Cluster cluster = HFactory.getOrCreateCluster(config.getCassandraCluster(), config.getCassandraHostAndPort());
        
        Keyspace keyspace = HFactory.createKeyspace(config.getCassandraKeyspace(), cluster);
        ColumnFamilyTemplate<String, String> template = 
                new ThriftColumnFamilyTemplate<String, String>(
                       keyspace,
                       COLUMN_FAMILY, 
                       StringSerializer.get(),        
                       StringSerializer.get() );
        
        ColumnFamilyUpdater<String, String> updater = template.createUpdater(path);
        updater.setString("response", response);
        updater.setLong("createdAt", System.currentTimeMillis());

        template.update(updater);
        
    }
}
