package ru.mail.tp.perfecture.places;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.perfecture.api.ApiInterface;
import ru.mail.tp.perfecture.api.CachedObject;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.api.RetrofitFactory;
import ru.mail.tp.perfecture.storage.DbManager;

public class PlaceManager {
    private static final String TAG = PlaceManager.class.getName();
    private static final PlaceManager ourInstance = new PlaceManager();

    private final ApiInterface perfectureApi = RetrofitFactory.getApi().create(ApiInterface.class);
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Map<Integer, ManagerListener> listeners = new HashMap<>();
    private final Map<Integer, CachedObject> requestCache = new HashMap<>();
    private Integer idCounter = 1;

    public static PlaceManager getInstance() {
        return ourInstance;
    }

    public <T> Integer registerListener(ManagerListener<T> listener) {
        listeners.put(idCounter, listener);
        return idCounter++;
    }

    public void unRegisterListener(Integer listenerId) {
        listeners.remove(listenerId);
        requestCache.remove(listenerId);
    }


    public <T> void subscribeListener(Integer listenerId, ManagerListener<T> listener) {
        listeners.put(listenerId, listener);
        if (requestCache.containsKey(listenerId)) {
            CachedObject result = requestCache.remove(listenerId);
            if (result.getError() != null) {
                listener.onManagerSuccess((T)result.getObject());
            } else {
                listener.onManagerError(result.getError());
            }
        }
    }

    public void unSubscribeListener(Integer listenerId) {
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
                        final ManagerListener<Place> listener = listeners.get(listenerId);
                        if (listener != null) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onManagerSuccess(response.body());
                                }
                            });
                        } else {
                            requestCache.put(listenerId, new CachedObject(response.body()));
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
                        final ManagerListener<PlaceList> listener = listeners.get(listenerId);
                        if (listener != null) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onManagerSuccess(response.body());
                                }
                            });
                        } else {
                            requestCache.put(listenerId, new CachedObject(response.body()));
                        }
                    }
                } else {
                    final ManagerListener<PlaceList> listener = listeners.get(listenerId);
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onManagerSuccess(response.body());
                            }
                        });
                    } else {
                        requestCache.put(listenerId, new CachedObject(response.body()));
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.d(PlaceManager.TAG, "Retrofit callback error: " + t.getMessage());
                t.printStackTrace();
                if (listeners.containsKey(listenerId)) {
                    ManagerListener<Place> listener = listeners.get(listenerId);
                    if (listener != null) {
                        listener.onManagerError("Unable to get nearest places!");
                    } else {
                        requestCache.put(listenerId, new CachedObject("Unable to get nearest places!"));
                    }
                }
            }
        });
    }

    private PlaceManager() {
    }

    private void retrievePlaceFromDB(final long placeId, final Integer listenerId) {
        DbManager.getInstance().getPlace(placeId, new DbManager.queryCallback<Place>() {
            @Override
            public void onSuccess(final Place result) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener<Place> listener = listeners.get(listenerId);
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onManagerSuccess(result);
                            }
                        });;
                    } else {
                        requestCache.put(listenerId, new CachedObject(result));
                    }
                }
            }

            @Override
            public void onError(String message) {
                if (listeners.containsKey(listenerId)) {
                    final ManagerListener<Place> listener = listeners.get(listenerId);
                    if (listener != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onManagerError("Unable to retrieve place!");
                            }
                        });
                    } else {
                        requestCache.put(listenerId, new CachedObject("Unable to retrieve place!"));
                    }
                }
            }
        });
    }


    public interface ManagerListener<T> {
        void onManagerSuccess(T result);
        void onManagerError(String message);
    }
}
