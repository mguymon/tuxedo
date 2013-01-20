package com.tobedevoured.tuxedo;

import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class RestClient {

    IConfig config;

    @Inject
    public RestClient(IConfig config) {
       this.config = config;
    }

    public String get(String path) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet( "http://localhost:" + config.getApiPort() + path );
        HttpResponse response = client.execute(request);
        return  IOUtils.toString(response.getEntity().getContent());
    }
}
