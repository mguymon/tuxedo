package com.tobedevoured.tuxedo;

public interface IConfig {

    int getProxyPort();

    String getWebHost();

    int getWebPort();

    String getWebHostAndPort();

    String getCassandraHost();

    int getCassandraPort();

    String getCassandraHostAndPort();

    String getCassandraCluster();

    String getCassandraKeyspace();

}
