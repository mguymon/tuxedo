package com.tobedevoured.tuxedo.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tobedevoured.command.CommandException;
import com.tobedevoured.command.DependencyManagable;
import com.tobedevoured.tuxedo.ConfigModule;
import com.tobedevoured.tuxedo.cassandra.CassandraModule;
import com.tobedevoured.tuxedo.proxy.ProxyModule;

public class DependencyManager implements DependencyManagable {
    
    public static DependencyManager instance;
    static {
        instance = new DependencyManager();
        instance.injector = 
           Guice.createInjector(new ConfigModule())
                .createChildInjector(new CassandraModule(), new ProxyModule());
    }
    
    Injector injector;
    
    public void init() {
        
    }    

    public <T> T getTarget(Class<T> clazz) throws CommandException {
        return instance.injector.getInstance(clazz);
    }

}
