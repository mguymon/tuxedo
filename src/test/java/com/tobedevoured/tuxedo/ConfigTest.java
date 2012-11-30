package com.tobedevoured.tuxedo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	private TypeSafeConfig config;
	
	@Before
	public void setup() {
		config = new TypeSafeConfig();
	}
	
	@Test
	public void defaults() {
		assertEquals( 8001, config.getProxyPort() );
		
		assertEquals( "localhost", config.getWebHost() );
		assertEquals( 3001, config.getWebPort() );
		assertEquals( "localhost:3001", config.getWebHostAndPort() );
		
		assertEquals( "localhost", config.getCassandraHost() );
		assertEquals( 9160, config.getCassandraPort() );
		assertEquals( "localhost:9160", config.getCassandraHostAndPort() );
		
	}
}
