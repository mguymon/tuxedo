package com.tobedevoured.tuxedo;

import com.typesafe.config.ConfigFactory;

public class Config {

	com.typesafe.config.Config config;
	
	public Config() {
		config = ConfigFactory.load();
	}
	
	public int getProxyPort() {
		return config.getInt( "tuxedo.proxy.port" );
	}
	
	public String getWebHost() {
		return config.getString( "tuxedo.web.host" );
	}
	
	public int getWebPort() {
		return config.getInt( "tuxedo.web.port" );
	}
}
