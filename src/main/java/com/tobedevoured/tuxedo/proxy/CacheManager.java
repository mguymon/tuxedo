package com.tobedevoured.tuxedo.proxy;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.ProxyCacheManager;
import org.littleshoot.proxy.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.tobedevoured.command.CommandException;
import com.tobedevoured.tuxedo.ServiceException;
import com.tobedevoured.tuxedo.cassandra.ResponseCache;
import com.tobedevoured.tuxedo.command.DependencyManager;

public class CacheManager implements ProxyCacheManager {
    static final Logger logger = LoggerFactory.getLogger(ProxyService.class);
    
    static Pattern extensions = Pattern.compile(".+\\.(ttf|png|gif|jpg|woff|js|css)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    ResponseCache responseCache;
    
    public CacheManager() {
       responseCache = DependencyManager.instance.getInstance(ResponseCache.class);
    }
    
    @Override
    public boolean returnCacheHit(HttpRequest request, Channel channel) {
        URI uri = URI.create(request.getUri() );
        String path = uri.getPath();
        
        if ( !extensions.matcher(path).matches() ) {
            Optional<String> cache = Optional.absent();
            try {
                cache = Optional.fromNullable( responseCache.getResponse( path ) );
            } catch (ConnectionException e) {
                logger.error( "Failed to access ResponseCache", e);
                return false;
            }
            
            if (cache.isPresent()) {
                logger.info("Using cache for " + path );
                final String statusLine = "HTTP/1.1 200 OK\r\n";
                
                final String headers =
                        "Date: "+ProxyUtils.httpDate()+"\r\n"+
                        "Content-Length: "+ cache.get().length() + "\r\n"+
                        "Content-Type: text/html; charset=iso-8859-1\r\n" +
                        "\r\n";
                
                ProxyUtils.writeResponse(channel, statusLine, headers, cache.get());
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
                boolean isCached = false;
                try {
                    isCached = responseCache.isCached(path);
                } catch (ConnectionException e) {
                    logger.error("Failed to access ResponseCache", e);
                    return null;
                }
                
                if (isCached) {
                    String responseHtml = responseToCache.getContent().duplicate().toString(Charset.defaultCharset());
                    
                    try {
                        responseCache.cacheResponse(path, responseHtml );
                    } catch (ConnectionException e) {
                        logger.error("Failed to cache response to: " + uri.getPath(), e);
                    }
                }
                
            }            
        }
        return null;
    }

}
