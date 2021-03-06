package com.tobedevoured.tuxedo.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tobedevoured.tuxedo.api.ApiModule;
import com.tobedevoured.command.DependencyManagable;
import com.tobedevoured.tuxedo.ConfigModule;
import com.tobedevoured.tuxedo.cache.CacheModule;
import com.tobedevoured.tuxedo.db.DbModule;
import com.tobedevoured.tuxedo.proxy.ProxyModule;

public class DependencyManager implements DependencyManagable {
    
    public static DependencyManager instance;
    static {
        instance = new DependencyManager();
        instance.injector = 
           Guice.createInjector(new ConfigModule())
                .createChildInjector(new CacheModule(), new DbModule(),new ProxyModule(), new ApiModule());
    }
    
    Injector injector;
    
    public void init() {
        
    }    

    public <T> T getInstance(Class<T> clazz) {
        return instance.injector.getInstance(clazz);
    }

}
