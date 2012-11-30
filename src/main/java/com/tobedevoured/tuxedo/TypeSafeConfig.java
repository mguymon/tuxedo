package com.tobedevoured.tuxedo;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

public class TypeSafeConfig implements IConfig {

    private static final Logger logger = LoggerFactory.getLogger( TypeSafeConfig.class );
    
	public static final String PROXY_KEY = "tuxedo.proxy.";
	public static final String WEB_KEY = "tuxedo.web.";
	public static final String CASSANDRA_KEY = "tuxedo.cassandra.";
	
	com.typesafe.config.Config config;
	
	public TypeSafeConfig() {
		config = ConfigFactory.load();
		
		logger.debug( "config: {}", config );
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getProxyPort()
     */
	@Override
    public int getProxyPort() {
		return config.getInt( StringUtils.join(PROXY_KEY, "port") );
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getWebHost()
     */
	@Override
    public String getWebHost() {
		return config.getString( StringUtils.join(WEB_KEY, "host") );
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getWebPort()
     */
	@Override
    public int getWebPort() {
		return config.getInt( StringUtils.join(WEB_KEY , "port") );
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getWebHostAndPort()
     */
	@Override
    public String getWebHostAndPort() {
		return StringUtils.join(getWebHost(), ":", getWebPort());
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getCassandraHost()
     */
	@Override
    public String getCassandraHost() {
		return config.getString(StringUtils.join(CASSANDRA_KEY, "host"));
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getCassandraPort()
     */
	@Override
    public int getCassandraPort() {
		return config.getInt(StringUtils.join(CASSANDRA_KEY, "port"));
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getCassandraHostAndPort()
     */
	@Override
    public String getCassandraHostAndPort() {
		return StringUtils.join(getCassandraHost(), ":", getCassandraPort());
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getCassandraCluster()
     */
	@Override
    public String getCassandraCluster() {
		return config.getString(StringUtils.join(CASSANDRA_KEY, "cluster"));
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IConfig#getCassandraKeyspace()
     */
	@Override
    public String getCassandraKeyspace() {
	    return config.getString(StringUtils.join(CASSANDRA_KEY, "keyspace"));
	}

}
