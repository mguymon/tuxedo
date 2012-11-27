package com.tobedevoured.tuxedo;

import java.io.IOException;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.thrift.transport.TTransportException;

public class CassandraService {
	EmbeddedCassandra embeddedCassandra;
	Thread cassandraThread;
	
	public CassandraService() throws TTransportException, IOException {
		embeddedCassandra = new EmbeddedCassandra();
		embeddedCassandra.init();
	}
	
	public void start() {
		cassandraThread = new Thread(embeddedCassandra);
		cassandraThread.setDaemon(true);
		cassandraThread.start();
	}
	
	public void stop() {
		Cluster myCluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		HFactory.shutdownCluster( myCluster );
	}
}
