package ru.mail.tp.perfecture.api;

import android.util.Log;

import com.google.android.gms.location.LocationListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sibirsky on 17.04.17.
 */

public class ApiService {
    private static final ApiService ourInstance = new ApiService();

    public static ApiService getInstance() {
        return ourInstance;
    }

    private ApiService() {
    }

    public void getPlace(final long id, final ApiCallback<Place> callback) {
        final ApiInterface apiInterface = RetrofitFactory.getApi().create(ApiInterface.class);
        final Call<Place> call = apiInterface.getPlaceById(id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                //TODO: check in storage
                callback.onError(t);
            }
        });
    }

    public void getNearestPlaces(final double latitude, final double longitude,
                                 final ApiCallback<PlaceList> callback) {
        final ApiInterface apiInterface = RetrofitFactory.getApi().create(ApiInterface.class);
        final Call<PlaceList> call = apiInterface.getNearestPlaces(latitude, longitude);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                //TODO: check in storage
                callback.onError(t);
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Throwable t);
    }
}
