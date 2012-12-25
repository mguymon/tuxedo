package com.tobedevoured.tuxedo.proxy;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.ProxyCacheManager;
import org.littleshoot.proxy.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.tobedevoured.tuxedo.cache.Cache;
import com.tobedevoured.tuxedo.command.DependencyManager;
import com.tobedevoured.tuxedo.db.Db4oService;
import com.tobedevoured.tuxedo.db.IDbService;

public class CacheManager implements ProxyCacheManager {
    static final Logger logger = LoggerFactory.getLogger(ProxyService.class);
    static Pattern extensions = Pattern.compile(".+\\.(ttf|png|gif|jpg|woff|js|css)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    Db4oService dbService;
    
    public CacheManager() {
        this.dbService = (Db4oService) DependencyManager.instance.getInstance(IDbService.class);
    }
    
    @Override
    public boolean returnCacheHit(HttpRequest request, Channel channel) {
        URI uri = URI.create(request.getUri() );
        String path = uri.getPath();
        
        if ( !extensions.matcher(path).matches() ) {
            Optional<Cache> cacheCheck = Optional.fromNullable( dbService.findActiveCache(path) );
            
            if (cacheCheck.isPresent()) {
                Cache cache = cacheCheck.get();
                logger.info("Using cache for " + path );
                final String statusLine = "HTTP/1.1 200 OK\r\n";
                
                final String headers =
                        "Date: "+ProxyUtils.httpDate()+"\r\n"+
                        "Content-Length: "+ cache.response.length() + "\r\n"+
                        "Content-Type: text/html; charset=iso-8859-1\r\n" +
                        "\r\n";
                
                ProxyUtils.writeResponse(channel, statusLine, headers, cache.response);
                ProxyUtils.closeOnFlush(channel);
                
                return true;
            }
        }
        
        return false;    
    }

    @Override
    public Future<String> cache(HttpRequest originalRequest,
            HttpResponse httpResponse, Object response, ChannelBuffer encoded) {
        if (response instanceof HttpResponse) {
            HttpResponse responseToCache =(HttpResponse) response; 
            if (!responseToCache.isChunked()) {
                URI uri = URI.create( originalRequest.getUri() );
                String path = uri.getPath();
                Optional<Cache> cacheCheck = Optional.fromNullable( dbService.findActiveCache(path) );
                
                if (cacheCheck.isPresent()) {
                    Cache cache = cacheCheck.get();
                    if ( cache.response == null && cache.lazy ) {
                        cache.response = responseToCache.getContent().duplicate().toString(Charset.defaultCharset());
                        dbService.store(cache);
                    }
                }
                
            }            
        }
        return null;
    }

}
