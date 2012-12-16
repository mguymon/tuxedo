package com.tobedevoured.tuxedo.proxy;

import org.littleshoot.proxy.ProxyCacheManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ProxyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IProxyService.class).to(ProxyService.class).in(Singleton.class);
    }

}
