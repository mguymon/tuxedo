package com.tobedevoured.tuxedo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	private Config config;
	
	@Before
	public void setup() {
		config = new Config();
	}
	
	@Test
	public void defaults() {
		assertEquals( 8000, config.getProxyPort() );
		assertEquals( "localhost", config.getWebHost() );
		assertEquals( 3000, config.getWebPort() );
	}
}
