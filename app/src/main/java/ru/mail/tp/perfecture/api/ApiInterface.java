package ru.mail.tp.perfecture.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sibirsky on 17.04.17.
 */

public interface ApiInterface {
    @GET("/api/places/id/{id}")
    Call<Place> getPlaceById(@Path("id") long id);

    @GET("/api/places/")
    Call<PlaceList> getNearestPlaces(@Query("latitude") double latitude,
                                     @Query("longitude") double longitude);
}
