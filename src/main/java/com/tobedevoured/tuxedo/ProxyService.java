package com.tobedevoured.tuxedo;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.ChainProxyManager;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.littleshoot.proxy.HttpResponseFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyService {
	static final Logger logger = LoggerFactory.getLogger(ProxyService.class);
	Config config;
	HttpProxyServer server;
	String webHostAndPort;
	
	public ProxyService(Config config) {
		this.config = config;
		
		this.webHostAndPort = new StringBuilder( config.getWebHost() )
			.append( ":" )
			.append( config.getWebPort() )
			.toString();

		final HttpRequestFilter requestFilter = new HttpRequestFilter() {

			public void filter(HttpRequest httpRequest) {
				
			}

        };
		
		final HttpResponseFilters responseFilters = new HttpResponseFilters() {

			public HttpFilter getFilter(String hostAndPort) {
				return new HttpFilter() {

					public boolean filterResponses(HttpRequest httpRequest) {
						
						return true;
					}

					public HttpResponse filterResponse(HttpRequest request, HttpResponse response) {						
						
						return response;
					}

					public int getMaxResponseSize() {
						return 1024 * 200000;
					}

				};
			}
			
		};

		this.server = new DefaultHttpProxyServer(config.getProxyPort(), responseFilters, 
				new ChainProxyManager() {
		      		public String getChainProxy(HttpRequest httpRequest )  {
		      			return webHostAndPort;
		      		}

					public void onCommunicationError(String hostAndPort) {
						logger.debug( "communication error from {}", hostAndPort );
					}
				}, null, requestFilter
		);
	}
	
	public void start() {
		server.start();	
	}
	
	public void stop() {
		server.stop();
	}
	
}
