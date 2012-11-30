package com.tobedevoured.tuxedo;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ProxyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IService.class).to(ProxyService.class).in(Singleton.class);
    }

}
