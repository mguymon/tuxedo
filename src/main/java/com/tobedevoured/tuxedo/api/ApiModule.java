package com.tobedevoured.tuxedo.api;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 */
public class ApiModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(IApiService.class).to(RestService.class).in(Singleton.class);
    }
}
