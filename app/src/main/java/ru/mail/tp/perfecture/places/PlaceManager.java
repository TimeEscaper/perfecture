package ru.mail.tp.perfecture.places;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.perfecture.api.ApiInterface;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.api.RetrofitFactory;
import ru.mail.tp.perfecture.storage.DbManager;

public class PlaceManager {
    private static final String TAG = PlaceManager.class.getName();
    private static final PlaceManager ourInstance = new PlaceManager();

    private final ApiInterface perfectureApi = RetrofitFactory.getApi().create(ApiInterface.class);
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static PlaceManager getInstance() {
        return ourInstance;
    }

    public void getPlace(final long id, final ManagerCallback<Place> callback) {
        final Call<Place> call = perfectureApi.getPlaceById(id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                if (response.isSuccessful()) {
                    DbManager.getInstance().addPlace(response.body());
                    postUiSuccess(response.body(), callback);
                } else {
                    retrievePlaceFromDB(id, callback);
                }
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Log.d(PlaceManager.TAG, "Retrofit callback error: " + t.getMessage());
                t.printStackTrace();
                retrievePlaceFromDB(id, callback);
            }
        });
    }

    public void getNearestPlaces(final double latitude, final double longitude,
                                 final ManagerCallback<PlaceList> callback) {
        final  Call<PlaceList> call = perfectureApi.getNearestPlaces(latitude, longitude);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                if (response.isSuccessful()) {
                    postUiSuccess(response.body(), callback);
                } else {
                    postUiError("Unable to get nearest places", callback);
                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.d(PlaceManager.TAG, "Retrofit callback error: " + t.getMessage());
                t.printStackTrace();
                postUiError("Unable to get nearest places", callback);
            }
        });

    }

    private PlaceManager() {
    }

    private void retrievePlaceFromDB(final long id, final ManagerCallback callback) {
        DbManager.getInstance().getPlace(id, new DbManager.queryCallback<Place>() {
            @Override
            public void onSuccess(Place result) {
                postUiSuccess(result, callback);
            }

            @Override
            public void onError(String message) {
                postUiError("Unable to get object", callback);
            }
        });
    }

    private <T> void postUiSuccess(final T response, final ManagerCallback<T> callback) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response);
            }
        });
    }

    private void postUiError(final String message, final ManagerCallback callback) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(message);
            }
        });
    }

    public interface ManagerCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
