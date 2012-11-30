package com.tobedevoured.tuxedo;

import static org.junit.Assert.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tobedevoured.tuxedo.cassandra.CassandraModule;
import com.tobedevoured.tuxedo.cassandra.CassandraService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProxyServiceTest {

    static Injector injector = Guice.createInjector(new ConfigModule()).createChildInjector(new ProxyModule());
    static IService service = injector.getInstance(CassandraService.class);
    static IConfig config = injector.getInstance(IConfig.class);
    static JettyServer jetty;
    
	ProxyService proxyService = injector.getInstance(ProxyService.class);
	
	@BeforeClass
	public static void setupJetty() throws Exception {
		jetty = new JettyServer(config.getWebPort());
		jetty.start();
	}
	
	@Before
	public void setupProxy() {
		proxyService.start();
	}
	
	@After
	public void shutdownProxy() {
		proxyService.stop();
	}
	
	@AfterClass
	public static void shutdownJetty() throws Exception {
		jetty.stop();
	}
	
	@Test
	public void proxy() throws Exception {
		final DefaultHttpClient http = new DefaultHttpClient();
        final HttpGet get = new HttpGet("http://localhost:" + config.getProxyPort() + "/test.html");
        final org.apache.http.HttpResponse hr = http.execute(get);
        final HttpEntity responseEntity = hr.getEntity();
        String response = EntityUtils.toString(responseEntity);
        http.getConnectionManager().shutdown();
        
        assertThat( response, containsString("<p>This is a test page</p>") );
	}
}
