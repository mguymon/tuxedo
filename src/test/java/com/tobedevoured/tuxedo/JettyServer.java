package com.tobedevoured.tuxedo;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

public class JettyServer implements IService {

	Server server;
	int port;
	
	public JettyServer(int port) {
		this.port = port;
	}
	
	public void start() throws ServiceException {
		server = new Server(port);
 
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/test/resources");
 
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
 
    	try {
            server.start();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
	}
	
	public void stop() throws ServiceException {
		try {
            server.stop();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
	}
	
	public static void main(String[] args) throws Exception {
		JettyServer server = new JettyServer(8080);
		server.start();
	}
}
