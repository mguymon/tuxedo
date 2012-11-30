package com.tobedevoured.tuxedo.cassandra;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.tuxedo.IConfig;
import com.tobedevoured.tuxedo.IService;
import com.tobedevoured.tuxedo.ServiceException;

@ByYourCommand
public class CassandraService implements IService {
	
    private CassandraDaemon cassandraDaemon;
    private final ExecutorService service = Executors.newSingleThreadExecutor(
                        new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("CassandraService-%d")
                            .build());
    
    @Inject
	public CassandraService(IConfig config) throws ServiceException {
	    cassandraDaemon = new CassandraDaemon();
        try {
            cassandraDaemon.init(null);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IService#start()
     */
	@Override
    @Command(exit=false)
	public void start() {
       service.submit(new Callable<Object>() { 
                @Override
                public Object call() throws Exception {
                    cassandraDaemon.start();
                    
                    return null;
                }
            }
        );
	}
	
	/* (non-Javadoc)
     * @see com.tobedevoured.tuxedo.IService#stop()
     */
	@Override
    public void stop() {
	    service.shutdownNow();
	    cassandraDaemon.deactivate();
	}
	
    public static void main(String[] args) throws RunException {
        Runner.run( args );
    }
}
