package com.hikimori911.mylastfmclient.network;

import com.hikimori911.mylastfmclient.data.pojo.GetSessionObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Дмитрий on 28.03.2015.
 */
public interface LastFMNetworkInterface {

    String API_URL = "http://ws.audioscrobbler.com/2.0";
    String SECURE_API_URL = "https://ws.audioscrobbler.com/2.0";

    @FormUrlEncoded
    @POST("/?api_key=01deff8d92c306d597800c24952e8a57&method=auth.getMobileSession&format=json")
    void getUserToken(@Field("api_key") String api_key,
                      @Field("api_sig") String api_sig,
                      @Field("password") String password,
                      @Field("username") String username,
                      Callback<GetSessionObject> callback);
}
