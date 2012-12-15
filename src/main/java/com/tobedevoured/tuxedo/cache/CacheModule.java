package com.tobedevoured.tuxedo.cache;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CacheModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IMessageListener.class).to(CacheListener.class).in(Singleton.class);
    }

}
