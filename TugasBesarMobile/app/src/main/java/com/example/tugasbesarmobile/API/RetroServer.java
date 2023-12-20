package com.example.tugasbesarmobile.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetroServer {
    private static final String baseURL = "http://172.20.10.3/taskmanager/";
    private static Retrofit retro;
    public static Retrofit konekRetrofit(){

        if (retro == null) {
            retro = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retro;
    }
}