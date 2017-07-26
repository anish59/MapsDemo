package com.example.anish.mapsdemo;

import android.app.Application;

import com.example.anish.mapsdemo.helper.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anish on 26-07-2017.
 */

public class AppApplication extends Application {
    private static Retrofit retrofit;
    private static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        initGson();
        initRetrofit();
    }

    private void initGson() {
        gson = new GsonBuilder()
                .setLenient()
                .create();
    }


    public static Gson getGson() {
        return gson;
    }

    private void initRetrofit() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASEURLSERVICEURL)
                .client(okHttpClient)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static Retrofit getRetrofit() {
        return retrofit;
    }


}
