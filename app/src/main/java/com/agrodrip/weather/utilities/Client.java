package com.agrodrip.weather.utilities;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit client = null;

    public static Retrofit getClient() {
        if (client == null) {
            client = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return client;
    }
}
