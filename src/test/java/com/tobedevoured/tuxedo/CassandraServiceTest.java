package com.tobedevoured.tuxedo;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class CassandraServiceTest {

	CassandraService service;
	Config config = new Config();
	Keyspace keyspace;
	
	@Before
	public void start() throws Exception {
		CassandraDataCleaner cleaner = new CassandraDataCleaner();
		cleaner.prepare();
		
		service = new CassandraService();
		service.start();
		
		Cluster cluster = HFactory.getOrCreateCluster(config.getCassandraCluster(), config.getCassandraHostAndPort());
		
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace("TestKeyspace");
		if (keyspaceDef == null) {
		
			ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
					"TestKeyspace", "ColumnFamilyName", ComparatorType.BYTESTYPE);
	
			KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
					"TestKeyspace", ThriftKsDef.DEF_STRATEGY_CLASS,
					1, Arrays.asList(cfDef));
			// Add the schema to the cluster.
			// "true" as the second param means that Hector will block until all
			// nodes see the change.
			cluster.addKeyspace(newKeyspace, true);
		}
		
		keyspace = HFactory.createKeyspace("TestKeyspace", cluster);
	}
	
	@After
	public void stop() {
		service.stop();
	}
	
	@Test
	public void doStuff() {
		
		ColumnFamilyTemplate<String, String> template =
                new ThriftColumnFamilyTemplate<String, String>(keyspace,
                                                               "ColumnFamilyName",
                                                               StringSerializer.get(),
                                                               StringSerializer.get());
		
		ColumnFamilyUpdater<String, String> updater = template.createUpdater("a key");
		updater.setString("test", "tuxedo");
		updater.setLong("time", System.currentTimeMillis());

		template.update(updater);
		
		
	    ColumnFamilyResult<String, String> res = template.queryColumns("a key");
	    assertEquals( "tuxedo", res.getString("test") );
		
	}
}
