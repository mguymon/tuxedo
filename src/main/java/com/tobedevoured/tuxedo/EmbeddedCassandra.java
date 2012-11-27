package com.tobedevoured.tuxedo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.ChainProxyManager;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.littleshoot.proxy.HttpResponseFilters;
import org.littleshoot.proxy.SelfSignedKeyStoreManager;

/**
 * Hello world!
 * 
 */
public class EmbeddedCassandra implements Runnable {
	CassandraDaemon cassandraDaemon;

	public void init() throws TTransportException, IOException {
		cassandraDaemon = new CassandraDaemon();
		cassandraDaemon.init(null);
	}

	public void run() {
		cassandraDaemon.start();
	}

	public static void main(String[] args) throws TTransportException,
			IOException, InterruptedException {
//		App app = new App();
//		app.init();
//		Thread t = new Thread(app);
//		t.setDaemon(true);
//		t.start();

	}
}
