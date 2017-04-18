package ru.mail.tp.perfecture.storage;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mail.tp.perfecture.api.Place;

/**
 * Created by sibirsky on 14.04.17.
 */

public class DbManager {
    private static final DbManager ourInstance = new DbManager();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    public void getPlace(long id, final queryCallback<Place> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PlaceModel placeModel = SQLite.select()
                        .from(PlaceModel.class)
                        .querySingle();
                if (placeModel == null) {
                    callback.onError("No such place!");
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
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(place);
                    }
                });
            }
        });
    }

    public void addPlace(final Place place) {
        DatabaseDefinition database = FlowManager.getDatabase(Configuration.activeDb);
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

                for (String photo : place.getPhotos()) {
                    PhotoLinkModel newPhoto = new PhotoLinkModel(photo, place.getId());
                    newPhoto.save(databaseWrapper);
                }
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("dfs", "Success!");
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(Transaction transaction, Throwable throwable) {
                Log.d("fsdf", "Error!");
            }
        }).build();

        transaction.execute();
    }

    public void addPhoto(final long placeId, final String url) {
        DatabaseDefinition database = FlowManager.getDatabase(Configuration.activeDb);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                PhotoLinkModel photoLinkModel = new PhotoLinkModel(url, placeId);
                photoLinkModel.save(databaseWrapper);
            }
        }).build();
        transaction.execute();
        transaction.cancel();
    }

    public interface queryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
