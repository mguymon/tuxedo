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
	
	@Before
	public void start() throws TTransportException, IOException {
		CassandraDataCleaner cleaner = new CassandraDataCleaner();
		cleaner.prepare();
		
		service = new CassandraService();
		service.start();
	}
	
	@After
	public void stop() {
		service.stop();
	}
	
	@Test
	public void doStuff() {
		Cluster cluster = HFactory.getOrCreateCluster("test-cluster",
				"localhost:9160");
		
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace("MyKeyspace");
		if (keyspaceDef == null) {
		
			ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
					"MyKeyspace", "ColumnFamilyName", ComparatorType.BYTESTYPE);
	
			KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
					"MyKeyspace", ThriftKsDef.DEF_STRATEGY_CLASS,
					1, Arrays.asList(cfDef));
			// Add the schema to the cluster.
			// "true" as the second param means that Hector will block until all
			// nodes see the change.
			cluster.addKeyspace(newKeyspace, true);
		}
		
		Keyspace ksp = HFactory.createKeyspace("MyKeyspace", cluster);
		
		ColumnFamilyTemplate<String, String> template =
                new ThriftColumnFamilyTemplate<String, String>(ksp,
                                                               "ColumnFamilyName",
                                                               StringSerializer.get(),
                                                               StringSerializer.get());
		
		ColumnFamilyUpdater<String, String> updater = template.createUpdater("a key");
		updater.setString("domain", "www.datastax.com");
		updater.setLong("time", System.currentTimeMillis());

		try {
		    template.update(updater);
		} catch (HectorException e) {
		    // do something ...
		}
		
		try {
		    ColumnFamilyResult<String, String> res = template.queryColumns("a key");
		    assertEquals( "www.datastax.com", res.getString("domain") );
		    // value should be "www.datastax.com" as per our previous insertion.
		} catch (HectorException e) {
		    // do something ...
		}
	}
}
