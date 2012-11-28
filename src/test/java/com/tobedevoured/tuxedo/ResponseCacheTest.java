package com.tobedevoured.tuxedo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ResponseCacheTest {
    
    ResponseCache responseCache;
    CassandraService service;
    Config config = new Config();
    
    @Before
    public void start() throws Exception {
        CassandraDataCleaner cleaner = new CassandraDataCleaner();
        cleaner.prepare();
        
        service = new CassandraService();
        service.start();
        
        Cluster cluster = HFactory.getOrCreateCluster(config.getCassandraCluster(), config.getCassandraHostAndPort());
        
        Thread.sleep(100);
        
        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(config.getCassandraKeyspace());
        if (keyspaceDef == null) {
            ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
                    config.getCassandraKeyspace(),
                    ResponseCache.COLUMN_FAMILY, 
                    ComparatorType.BYTESTYPE);
    
            KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
                    config.getCassandraKeyspace(), ThriftKsDef.DEF_STRATEGY_CLASS,
                    1, Arrays.asList(cfDef));
 
            cluster.addKeyspace(newKeyspace, true);
        }
        
        HFactory.createKeyspace(config.getCassandraKeyspace(), cluster);
       
        responseCache = new ResponseCache( config );
    }
    
    @After
    public void stop() {
        service.stop();
    }
    
    @Test
    public void cacheAndGetResponse() {
        responseCache.cacheResponse( "/a/test/path", "blah blah blah" );
        
        String response = responseCache.getResponse("/a/test/path");
        
        assertEquals( "blah blah blah", response );
    }
}
