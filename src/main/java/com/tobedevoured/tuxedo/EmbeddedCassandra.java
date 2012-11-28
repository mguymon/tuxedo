package com.tobedevoured.tuxedo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.config.DatabaseDescriptor;
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
	Config config;
	
	public EmbeddedCassandra(Config config) {
		this.config = config;
	}

	public void init() throws Exception {
		cassandraDaemon = new CassandraDaemon();
		cassandraDaemon.init(null);
	}

	public void run() {
		cassandraDaemon.start();
	}
}
