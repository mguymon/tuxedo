package com.tobedevoured.tuxedo.proxy;

import java.net.URI;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import org.littleshoot.proxy.ChainProxyManager;
import org.littleshoot.proxy.DefaultCachedHttpResponse;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.littleshoot.proxy.HttpResponseFilters;
import org.littleshoot.proxy.LittleProxyConfig;
import org.littleshoot.proxy.ProxyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import com.tobedevoured.command.CommandException;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;

@ByYourCommand
public class ProxyService implements IProxyService {
	static final Logger logger = LoggerFactory.getLogger(ProxyService.class);
	IConfig config;
	HttpProxyServer server;
	String webHostAndPort;
	Db4oService db4oService;
	
	@Inject
	public ProxyService(IConfig config, Db4oService db4oService) {
		this.config = config;
		this.db4oService = db4oService;
		this.webHostAndPort = config.getWebHostAndPort();

		LittleProxyConfig.setProxyCacheManagerClass( CacheManager.class.getCanonicalName() );
		
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
	
	@Command(exit=false)
	public void start() {
		server.start();
        db4oService.start();
	}
	
	public void stop() {
		server.stop();
		db4oService.stop();
	}
	
    public static void main(String[] args) throws RunException {
        Runner.run( args );
    }
}
