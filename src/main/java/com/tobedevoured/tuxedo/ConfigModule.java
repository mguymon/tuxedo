package com.tobedevoured.tuxedo;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
       bind(IConfig.class).to(TypeSafeConfig.class).in(Singleton.class);
    }

}
