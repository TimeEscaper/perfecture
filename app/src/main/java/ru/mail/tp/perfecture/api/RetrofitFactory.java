package ru.mail.tp.perfecture.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() { }

    private final static Retrofit RETROFIT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://aviaj-backend-travis.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit getApi() {
        return RETROFIT_INSTANCE;
    }
}
