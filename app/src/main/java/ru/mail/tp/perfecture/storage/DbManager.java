package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import ru.mail.tp.perfecture.api.Place;

/**
 * Created by sibirsky on 14.04.17.
 */

class DbManager {
    private static final DbManager ourInstance = new DbManager();

    static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    //TODO: implement
    public Place getPlace(long id) {
        return null;
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
        }).build();
        transaction.execute();
        transaction.cancel();
    }


}
