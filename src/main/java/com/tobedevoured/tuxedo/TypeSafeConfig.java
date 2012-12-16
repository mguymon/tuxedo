package com.tobedevoured.tuxedo;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TypeSafeConfig implements IConfig {

    private static final Logger logger = LoggerFactory.getLogger( TypeSafeConfig.class );
    
	public static final String PROXY_KEY = "tuxedo.proxy.";
	public static final String WEB_KEY = "tuxedo.web.";
	public static final String DB_KEY = "tuxedo.db.";
	
	public int proxyPort;
	public String webHost;
	public int webPort;
	public String dbPath;
	public Boolean isDbDebug;
	
	Config config;
	
	public TypeSafeConfig() {
		config = ConfigFactory.load();
		
		
		Config testConfig = ConfigFactory.load("test.conf");
		if ( testConfig != null) {
		    config = testConfig.withFallback(config);
		}
		logger.debug( "config: {}", config );
		
		proxyPort = config.getInt( StringUtils.join(PROXY_KEY, "port") );
		webHost = config.getString( StringUtils.join(WEB_KEY, "host") );
		webPort = config.getInt( StringUtils.join(WEB_KEY , "port") );
		dbPath = config.getString(StringUtils.join(DB_KEY, "path"));
		isDbDebug = config.getBoolean(StringUtils.join(DB_KEY, "debug"));
	}
	
    public int getProxyPort() {
		return proxyPort;
	}
	
    public String getWebHost() {
		return webHost;
	}
	
    public int getWebPort() {
		return webPort;
	}
	
    public String getWebHostAndPort() {
		return StringUtils.join(getWebHost(), ":", getWebPort());
	}
	
    public String getDbPath() {
        return dbPath;
    }
    
    public Boolean isDbDebug() {
        return isDbDebug;
    }

}
