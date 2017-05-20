package ru.mail.tp.perfecture.places;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.storage.DbManager;

public class PlaceInfoActivity extends AppCompatActivity {
    private static final String TAG = PlaceInfoActivity.class.getName();
    public static final String EXTRA_PLACE_TAG = "EXTRA_PLACE_ID";

    private TextView txtPlaceTitle;
    private TextView txtPlaceDescription;
    RecyclerView recyclerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        txtPlaceTitle = (TextView) findViewById(R.id.place_title);
        txtPlaceDescription = (TextView) findViewById(R.id.place_description);

        final long placeId = Long.valueOf(getIntent().getStringExtra(EXTRA_PLACE_TAG));

        PlaceManager.getInstance().getPlace(placeId, new PlaceManager.ManagerCallback<Place>() {
            @Override
            public void onSuccess(Place result) {
                displayPlace(result);
            }

            @Override
            public void onError(String message) {
                DbManager.getInstance().getPlace(placeId, new DbManager.queryCallback<Place>() {
                    @Override
                    public void onSuccess(Place result) {
                        displayPlace(result);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message);
                    }
                });
            }
        });
    }

    private void displayPlace(Place place) {
        txtPlaceTitle.setText(place.getTitle());
        txtPlaceDescription.setText(place.getDescription());
        if ((place.getPhotos() != null) && (place.getPhotos().size() != 0)) {
            recyclerImages = (RecyclerView) findViewById(R.id.images);
            recyclerImages.setLayoutManager(new LinearLayoutManager(this));
            recyclerImages.setAdapter(new ImageAdapter(this, place.getPhotos()));
        }
    }

    private void showError(String text) {
        Log.d(PlaceInfoActivity.TAG, text);
        finish();
    }
}
