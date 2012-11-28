package com.tobedevoured.tuxedo;


import org.apache.commons.lang3.StringUtils;

import com.typesafe.config.ConfigFactory;

public class Config {

	public static final String PROXY_KEY = "tuxedo.proxy.";
	public static final String WEB_KEY = "tuxedo.web.";
	public static final String CASSANDRA_KEY = "tuxedo.cassandra.";
	
	com.typesafe.config.Config config;
	
	public Config() {
		config = ConfigFactory.load();
	}
	
	public int getProxyPort() {
		return config.getInt( StringUtils.join(PROXY_KEY, "port") );
	}
	
	public String getWebHost() {
		return config.getString( StringUtils.join(WEB_KEY, "host") );
	}
	
	public int getWebPort() {
		return config.getInt( StringUtils.join(WEB_KEY , "port") );
	}
	
	public String getWebHostAndPort() {
		return StringUtils.join(getWebHost(), ":", getWebPort());
	}
	
	public String getCassandraHost() {
		return config.getString(StringUtils.join(CASSANDRA_KEY, "host"));
	}
	
	public int getCassandraPort() {
		return config.getInt(StringUtils.join(CASSANDRA_KEY, "port"));
	}
	
	public String getCassandraHostAndPort() {
		return StringUtils.join(getCassandraHost(), ":", getCassandraPort());
	}
	
	public String getCassandraCluster() {
		return config.getString(StringUtils.join(CASSANDRA_KEY, "cluster"));
	}
	
	public String getCassandraKeyspace() {
	    return config.getString(StringUtils.join(CASSANDRA_KEY, "keyspace"));
	}

}
