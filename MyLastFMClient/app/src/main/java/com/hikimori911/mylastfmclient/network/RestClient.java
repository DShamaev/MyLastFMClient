package com.hikimori911.mylastfmclient.network;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

/**
 * Created by hikimori911 on 30.03.2015.
 */
public class RestClient {

    private static LastFMNetworkInterface REST_CLIENT;

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static LastFMNetworkInterface get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(LastFMNetworkInterface.SECURE_API_URL)
                .setConverter(new JacksonConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(LastFMNetworkInterface.class);
    }
}