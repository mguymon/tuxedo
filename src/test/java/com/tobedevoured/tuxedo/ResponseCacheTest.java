package com.tobedevoured.tuxedo;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.tobedevoured.tuxedo.cassandra.CassandraModule;
import com.tobedevoured.tuxedo.cassandra.CassandraService;
import com.tobedevoured.tuxedo.cassandra.ResponseCache;

public class ResponseCacheTest {
    
    static Injector injector = Guice.createInjector(new ConfigModule()).createChildInjector(new CassandraModule());
    static IConfig config = injector.getInstance(IConfig.class);
    static IService service = injector.getInstance(CassandraService.class);
    static AstyanaxContext<Keyspace> context = injector.getInstance(AstyanaxContext.class);
    ResponseCache responseCache = injector.getInstance(ResponseCache.class);
    
    
    @BeforeClass
    public static void start() throws Exception {
        CassandraDataCleaner cleaner = new CassandraDataCleaner();
        cleaner.prepare();
        
        service.start();
        
        Keyspace keyspace = context.getEntity();
        
        Thread.sleep(500);
        
        KeyspaceDefinition keyspaceDef = keyspace.describeKeyspace();
        if ( keyspace.describeKeyspace() == null ) {
        
            keyspace.createKeyspace(
                ImmutableMap.<String, Object>builder()
                .put("strategy_options", ImmutableMap.<String, Object>builder()
                    .put("replication_factor", "1")
                    .build())
                .put("strategy_class",     "SimpleStrategy")
                    .build()
                 );
        }
        
        ColumnFamilyDefinition cfDef = keyspaceDef.getColumnFamily( ResponseCache.CF_RESPONSE_CACHE.getName() );
        if (cfDef == null ) {
            keyspace.createColumnFamily(ResponseCache.CF_RESPONSE_CACHE, null);
        }
    }
    
    @AfterClass
    public static void stop() throws ServiceException {
        service.stop();
    }
    
    @Test
    public void cacheAndGetResponse() throws ConnectionException {
        responseCache.cacheResponse( "/a/test/path", "blah blah blah" );
        
        String response = responseCache.getResponse("/a/test/path");
        
        assertEquals( "blah blah blah", response );
    }
    
    @Test
    public void ttlShouldMatter() throws ConnectionException, InterruptedException {
        responseCache.cacheResponse( "/an/another/path", "waa waa waa", 2 );
        assertEquals( "waa waa waa", responseCache.getResponse("/an/another/path") );
        
        Thread.sleep(2000);
        
        assertNull("Response should be expired", responseCache.getResponse("/an/another/path") );
    }
    
    @Test 
    public void clearCache() throws ConnectionException, InterruptedException {
        responseCache.cacheResponse( "/a/test/path", "blah blah blah" );
        assertEquals( "blah blah blah", responseCache.getResponse("/a/test/path") );
        
        responseCache.clearCache("/a/test/path");
        assertNull("Response should be cleared", responseCache.getResponse("/a/test/path") );
    }
}
