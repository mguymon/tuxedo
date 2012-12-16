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
	public void testConfig() {
		assertEquals( 8001, config.getProxyPort() );
		
		assertEquals( "localhost", config.getWebHost() );
		assertEquals( 3000, config.getWebPort() );
		assertEquals( "localhost:3000", config.getWebHostAndPort() );
		
	}
}
