package ru.mail.tp.perfecture.storage;

import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceList;

public class DbManager {
    private static final String TAG = DbManager.class.getName();
    private static final DbManager ourInstance = new DbManager();

    private final Executor executor = Executors.newSingleThreadExecutor();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    public void getPlace(final long id, final QueryCallback<Place> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PlaceModel placeModel = SQLite.select()
                        .from(PlaceModel.class)
                        .where(PlaceModel_Table.id.eq(id))
                        .querySingle();
                if (placeModel == null) {
                    callback.onError("Place not found in local database");
                    return;
                }
                final Place place = new Place(placeModel.getId(), placeModel.getTitle(),
                        placeModel.getDescription(), placeModel.getLatitude(), placeModel.getLongitude());
                List<PhotoLinkModel> photoLinks = SQLite.select()
                        .from(PhotoLinkModel.class)
                        .where(PhotoLinkModel_Table.placeId_id.eq(place.getId()))
                        .queryList();
                if (!photoLinks.isEmpty()) {
                    List<String> photos = place.getPhotos();
                    for (PhotoLinkModel photoLink : photoLinks) {
                        photos.add(photoLink.getUrl());
                    }
                }
                callback.onSuccess(place);
            }
        });
    }

    public void getAllPlaces(final QueryCallback<PlaceList> callback) {
        SQLite.select()
                .from(PlaceModel.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<PlaceModel>() {
                    @Override
                    public void onListQueryResult(QueryTransaction queryTransaction, @NonNull List<PlaceModel> list) {
                        List<Place> places = new ArrayList<Place>();
                        if (!list.isEmpty()) {
                            for (PlaceModel model : list) {
                                places.add(new Place(model.getId(), model.getTitle(), null,
                                        model.getLatitude(), model.getLongitude()));
                            }
                        }
                        PlaceList placeList = new PlaceList();
                        placeList.setPlaces(places);
                        callback.onSuccess(placeList);
                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable throwable) {
                        Log.d(DbManager.TAG, "Transaction error: " + throwable.getMessage());
                        throwable.printStackTrace();
                        callback.onError("Unable to get places from database");
                    }
                })
                .execute();
    }


    public void addPlace(final Place place) {
        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                PlaceModel newPlace = new PlaceModel(
                        place.getId(),
                        place.getTitle(),
                        place.getDescription(),
                        place.getLatitude(),
                        place.getLongitude());
                newPlace.save(databaseWrapper);

                if ((place.getPhotos() != null) && (!place.getPhotos().isEmpty())) {
                    for (String photo : place.getPhotos()) {
                        PhotoLinkModel newPhoto = new PhotoLinkModel(photo, place.getId());
                        newPhoto.save(databaseWrapper);
                    }
                }
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(Transaction transaction, Throwable throwable) {
                Log.d(DbManager.TAG, "Transaction error: " +  throwable.getMessage());
                throwable.printStackTrace();
            }
        }).build();

        transaction.execute();
    }

    public interface QueryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
