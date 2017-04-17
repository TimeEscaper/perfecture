package ru.mail.tp.perfecture.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sibirsky on 17.04.17.
 */

public class RetrofitFactory {

    private RetrofitFactory() { };

    private final static Retrofit RETROFIT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://aviaj-backend-travis.herokuapp.com/api/places/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static Retrofit getApi() {
        return RETROFIT_INSTANCE;
    }
}
