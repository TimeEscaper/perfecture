package ru.mail.tp.perfecture.places;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.ApiInterface;
import ru.mail.tp.perfecture.api.ApiService;
import ru.mail.tp.perfecture.api.Place;

public class PlaceInfoActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_TAG = "EXTRA_PLACE_ID";

    private TextView txtPlaceTitle;
    private TextView txtPlaceDescription;
    private TextView txtPlacePhotos;

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        txtPlaceTitle = (TextView) findViewById(R.id.place_title);
        txtPlaceDescription = (TextView) findViewById(R.id.place_description);
        txtPlacePhotos = (TextView) findViewById(R.id.place_photos);

        long placeId = Long.valueOf(getIntent().getStringExtra(EXTRA_PLACE_TAG));

        ApiService.getInstance().getPlace(placeId, new ApiService.ApiCallback<Place>() {
            @Override
            public void onSuccess(Place result) {
                displayPlace(result);
            }

            @Override
            public void onError(Throwable t) {
                showError("Error retrieving place");
            }
        });
    }

    private void displayPlace(Place place) {
        txtPlaceTitle.setText(place.getTitle());
        txtPlaceDescription.setText(place.getDescription());
        if (place.getPhotos() != null) {
            txtPlacePhotos.setText(String.valueOf(place.getPhotos().size()));
        }
    }

    private void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT);
        finish();
    }
}
