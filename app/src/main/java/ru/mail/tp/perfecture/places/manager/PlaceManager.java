package ru.mail.tp.perfecture.places.manager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.perfecture.api.ApiInterface;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceError;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.api.RetrofitFactory;
import ru.mail.tp.perfecture.places.cache.Cacheable;
import ru.mail.tp.perfecture.places.cache.PlaceCache;
import ru.mail.tp.perfecture.storage.DbManager;

public class PlaceManager {
    private static final String TAG = PlaceManager.class.getName();
    private static final PlaceManager ourInstance = new PlaceManager();

    private final ApiInterface perfectureApi = RetrofitFactory.getApi().create(ApiInterface.class);
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("UseSparseArrays")
    private final Map<Integer, ManagerListener> listeners = new HashMap<>();
    private final PlaceCache requestCache = new PlaceCache();
    private int idCounter = 1;

    public static PlaceManager getInstance() {
        return ourInstance;
    }

    public int registerListener(ManagerListener listener) {
        listeners.put(idCounter, listener);
        return idCounter++;
    }

    public void unRegisterListener(int listenerId) {
        listeners.remove(listenerId);
        requestCache.remove(listenerId);
    }

    public void subscribeListener(int listenerId, ManagerListener listener) {
        if (listeners.containsKey(listenerId)) {
            return;
        }
        listeners.put(listenerId, listener);
        if (requestCache.contains(listenerId)) {
            Cacheable result = requestCache.get(listenerId);
            requestCache.remove(listenerId);
            if (result instanceof Place) {
                listener.onPlaceSuccess((Place)result);
                return;
            }
            if (result instanceof PlaceList) {
                listener.onPlaceListSuccess((PlaceList)result);
                return;
            }
            if (result instanceof PlaceError) {
                listener.onPlaceError((PlaceError)result);
            }
        }
    }

    public void unSubscribeListener(int listenerId) {
        if (listeners.containsKey(listenerId)) {
            listeners.put(listenerId, null);
        }
    }

    public void getPlace(final long placeId, final Integer listenerId) {
        final Call<Place> call = perfectureApi.getPlaceById(placeId);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, final Response<Place> response) {
                if (response.isSuccessful()) {
                    DbManager.getInstance().addPlace(response.body());
                    if (listeners.containsKey(listenerId)) {
                        final ManagerListener listener = listeners.get(listenerId);
                        if (listener != null) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPlaceSuccess(response.body());
                                }
                            });
                        } else {
                            requestCache.put(listenerId, response.body());
                        }
                    }
                } else {
                    retrievePlaceFromDB(placeId, listenerId);
                }
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Log.d(PlaceManager.TAG, "Retrofit callback error: " + t.getMessage());
                t.printStackTrace();
                retrievePlaceFromDB(placeId, listenerId);
            }
        });
    }

    public void getNearestPlaces(final double latitude, final double longitude,
                                 final Integer listenerId) {
        final  Call<PlaceList> call = perfectureApi.getNearestPlaces(latitude, longitude);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, final Response<PlaceList> response) {
                if (response.isSuccessful()) {
                    if (listeners.containsKey(listenerId)) {
                        final ManagerListener listener = listeners.get(listenerId);
                        if (listener != null) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPlaceListSuccess(response.body());
                                }
                            });
                        } else {
                            requestCache.put(listenerId, response.body());
                        }
                    }
                } else {
                    final ManagerListener listener = listeners.get(listenerId);
                    final PlaceError error = new PlaceError("Unable to get nearest places:" +
                            "unsuccessful response!");
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPlaceListError(error);
                            }
                        });
                    } else {
                        requestCache.put(listenerId, error);
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.d(PlaceManager.TAG, "Retrofit callback error: " + t.getMessage());
                t.printStackTrace();
                if (listeners.containsKey(listenerId)) {
                    final PlaceError error = new PlaceError("Unable to get nearest places:" +
                            "unsuccessful response!");
                    ManagerListener listener = listeners.get(listenerId);
                    if (listener != null) {
                        listener.onPlaceListError(error);
                    } else {
                        requestCache.put(listenerId, error);
                    }
                }
            }
        });
    }

    public void getAllPlacesList(final Integer listenerId) {
        DbManager.getInstance().getAllPlaces(new DbManager.QueryCallback<PlaceList>() {
            @Override
            public void onSuccess(final PlaceList result) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener listener = listeners.get(listenerId);
                    if (listener != null) {
                        if (result.getPlaces().isEmpty()) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPlaceListError(new PlaceError("Database is still empty!"));
                                }
                            });
                        } else {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPlaceListSuccess(result);
                                }
                            });
                        }
                    } else {
                        requestCache.put(listenerId, result);
                    }
                }
            }

            @Override
            public void onError(final String message) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener listener = listeners.get(listenerId);
                    final PlaceError error = new PlaceError("Unable to get places from database!");
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPlaceError(error);
                            }
                        });
                    } else {
                        requestCache.put(listenerId, error);
                    }
                }
            }
        });
    }

    private PlaceManager() {
    }

    private void retrievePlaceFromDB(final long placeId, final Integer listenerId) {
        DbManager.getInstance().getPlace(placeId, new DbManager.QueryCallback<Place>() {
            @Override
            public void onSuccess(final Place result) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener listener = listeners.get(listenerId);
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPlaceSuccess(result);
                            }
                        });
                    } else {
                        requestCache.put(listenerId, result);
                    }
                }
            }

            @Override
            public void onError(String message) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener listener = listeners.get(listenerId);
                    final PlaceError error = new PlaceError("Unable to retrieve place!");
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPlaceError(error);
                            }
                        });
                    } else {
                        requestCache.put(listenerId, error);
                    }
                }
            }
        });
    }


    public interface ManagerListener {
        void onPlaceSuccess(Place result);
        void onPlaceListSuccess(PlaceList result);
        void onPlaceError(PlaceError error);
        void onPlaceListError(PlaceError error);
    }
}
