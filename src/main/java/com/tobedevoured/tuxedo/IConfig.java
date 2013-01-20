package com.tobedevoured.tuxedo;

public interface IConfig {

    int getProxyPort();

    String getWebHost();

    int getWebPort();

    String getWebHostAndPort();
    
    String getDbPath();

    Boolean isDbDebug();

    int getApiPort();
}
