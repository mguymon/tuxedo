package com.tobedevoured.tuxedo.db;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tobedevoured.tuxedo.IConfig;

public class DbProvider implements Provider<Db4oService> {
    private static Db4oService db4oService;
    
    @Inject
    public DbProvider(IConfig config) {
      if ( db4oService == null) {
          this.db4oService = new Db4oService(config);
      }
    }

    public Db4oService get() {
      return db4oService;
    }
  }