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
	
	Config config;
	
	public TypeSafeConfig() {
		config = ConfigFactory.load();
		
		
		Config testConfig = ConfigFactory.load("test.conf");
		if ( testConfig != null) {
		    config = testConfig.withFallback(config);
		}
		logger.debug( "config: {}", config );
	}
	
	@Override
    public int getProxyPort() {
		return config.getInt( StringUtils.join(PROXY_KEY, "port") );
	}
	
	@Override
    public String getWebHost() {
		return config.getString( StringUtils.join(WEB_KEY, "host") );
	}
	
	@Override
    public int getWebPort() {
		return config.getInt( StringUtils.join(WEB_KEY , "port") );
	}
	
	@Override
    public String getWebHostAndPort() {
		return StringUtils.join(getWebHost(), ":", getWebPort());
	}
	
    @Override
    public String getDbPath() {
        return config.getString(StringUtils.join(DB_KEY, "path"));
    }
    
    public Boolean isDbDebug() {
        return config.getBoolean(StringUtils.join(DB_KEY, "debug"));
    }

}
