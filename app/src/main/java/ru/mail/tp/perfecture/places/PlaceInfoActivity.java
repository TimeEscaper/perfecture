package ru.mail.tp.perfecture.places;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.storage.DbManager;

public class PlaceInfoActivity extends AppCompatActivity
        implements PlaceManager.ManagerListener<Place> {
    private static final String TAG = PlaceInfoActivity.class.getName();
    private static final String STATE_LISTENER_ID = "listenerId";

    public static final String EXTRA_PLACE_TAG = "EXTRA_PLACE_ID";

    private TextView txtPlaceTitle;
    private TextView txtPlaceDescription;
    RecyclerView recyclerImages;

    private Integer listenerId;
    private long placeId;
    private boolean isDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        txtPlaceTitle = (TextView) findViewById(R.id.place_title);
        txtPlaceDescription = (TextView) findViewById(R.id.place_description);
        placeId = Long.valueOf(getIntent().getStringExtra(EXTRA_PLACE_TAG));

        if (savedInstanceState != null) {
            listenerId = savedInstanceState.getInt(STATE_LISTENER_ID);
        }
        Log.d(TAG, String.valueOf(listenerId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listenerId == null) {
            listenerId = PlaceManager.getInstance().registerListener(this);
        }
        PlaceManager.getInstance().subscribeListener(listenerId, this);
        PlaceManager.getInstance().getPlace(placeId, listenerId);
    }

    @Override
    protected void onStop(){
        PlaceManager.getInstance().unSubscribeListener(listenerId);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PlaceManager.getInstance().unRegisterListener(listenerId);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_LISTENER_ID, listenerId);
    }

    @Override
    public void onManagerSuccess(Place result) {
        if ((!isDisplayed) && (placeId == result.getId())) {
            displayPlace(result);
            isDisplayed = true;
        }
    }

    @Override
    public void onManagerError(String message) {
        if (!isDisplayed) {
            isDisplayed = true;
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
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
