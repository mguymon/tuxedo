package com.tobedevoured.tuxedo;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.tobedevoured.tuxedo.cassandra.CassandraModule;
import com.tobedevoured.tuxedo.cassandra.CassandraService;

public class CassandraServiceTest {

    public static ColumnFamily<String, String> CF_TEST = 
            ColumnFamily.newColumnFamily("CassandraServiceTest", 
                    StringSerializer.get(),
                    StringSerializer.get());

    Injector injector = Guice.createInjector(new ConfigModule())
            .createChildInjector(new CassandraModule());
    IConfig config = injector.getInstance(IConfig.class);
    IService service = injector.getInstance(CassandraService.class);
    AstyanaxContext<Keyspace> context = injector.getInstance(AstyanaxContext.class);

    @Before
    public void start() throws Exception {
        CassandraDataCleaner cleaner = new CassandraDataCleaner();
        cleaner.prepare();

        service.start();

        Thread.sleep(500);

        Keyspace keyspace = context.getEntity();

        KeyspaceDefinition keyspaceDef = null;
        try {
            keyspaceDef = keyspace.describeKeyspace();
        } catch (BadRequestException e) {

            keyspace.createKeyspace(ImmutableMap
                    .<String, Object> builder()
                    .put("strategy_options",
                            ImmutableMap.<String, Object> builder()
                                    .put("replication_factor", "1").build())
                    .put("strategy_class", "SimpleStrategy").build());
            keyspaceDef = keyspace.describeKeyspace();
        }

        ColumnFamilyDefinition cfDef = keyspaceDef.getColumnFamily(CF_TEST
                .getName());
        if (cfDef == null) {
            keyspace.createColumnFamily(CF_TEST, null);
        }

    }

    @After
    public void stop() throws ServiceException {
        service.stop();
    }

    @Test
    public void doStuff() throws ConnectionException {
        Keyspace keyspace = context.getEntity();
        MutationBatch mutation = keyspace.prepareMutationBatch();

        mutation.withRow(CF_TEST, "test").putColumn("test column", "test val", null);

        mutation.execute();

        OperationResult<ColumnList<String>> result = 
           keyspace.prepareQuery(CF_TEST).getKey("test").execute();
        ColumnList<String> columns = result.getResult();

        assertEquals("test val", columns.getColumnByName("test column").getStringValue());

    }
}
