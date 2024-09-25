package com.bottom.footballtv.services;

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.util.Map;

public class CustomHttpDataSourceFactory implements HttpDataSource.Factory {

    private final String userAgent;
    private final String referer;
    private final String origin;

    public CustomHttpDataSourceFactory(String userAgent, String referer, String origin) {
        this.userAgent = userAgent;
        this.referer = referer;
        this.origin = origin;
    }

    @Override
    public HttpDataSource createDataSource() {
        DefaultHttpDataSource dataSource = new DefaultHttpDataSource(userAgent);
        dataSource.setRequestProperty("Referer", referer);
        dataSource.setRequestProperty("Origin", origin);
        return dataSource;
    }

    @Override
    public HttpDataSource.RequestProperties getDefaultRequestProperties() {
        return null;
    }

    @Override
    public HttpDataSource.Factory setDefaultRequestProperties(Map<String, String> defaultRequestProperties) {
        return null;
    }
}
