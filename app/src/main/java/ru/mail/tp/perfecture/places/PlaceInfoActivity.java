package ru.mail.tp.perfecture.places;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceError;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.places.manager.PlaceManager;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PlaceInfoActivity extends AppCompatActivity
        implements PlaceManager.ManagerListener {

    private static final String TAG = PlaceInfoActivity.class.getName();
    private static final String STATE_LISTENER_ID = "listenerId";
    private static final String STATE_PENDING = "pending";

    public static final String EXTRA_PLACE_TAG = "EXTRA_PLACE_ID";

    private TextView txtPlaceTitle;
    private TextView txtPlaceDescription;
    private RecyclerView recyclerImages;

    private int listenerId = 0;
    private long placeId;
    private boolean isPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        txtPlaceTitle = (TextView) findViewById(R.id.place_title);
        txtPlaceDescription = (TextView) findViewById(R.id.place_description);
        placeId = Long.valueOf(getIntent().getStringExtra(EXTRA_PLACE_TAG));

        if (savedInstanceState != null) {
            listenerId = savedInstanceState.getInt(STATE_LISTENER_ID);
            isPending = savedInstanceState.getBoolean(STATE_PENDING);
        }

        if (listenerId == 0) {
            listenerId = PlaceManager.getInstance().registerListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlaceManager.getInstance().subscribeListener(listenerId, this);
        if (!isPending) {
            isPending = true;
            PlaceManager.getInstance().getPlace(placeId, listenerId);
        }
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
        savedInstanceState.putBoolean(STATE_PENDING, isPending);
    }

    @Override
    public void onPlaceSuccess(Place result) {
        if ((isPending) && (placeId == result.getId())) {
            displayPlace(result);
            isPending = false;
        }
    }

    @Override
    public void onPlaceListSuccess(PlaceList result) { }

    @Override
    public void onPlaceError(PlaceError error) {
        if (isPending) {
            isPending = true;
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage(error.getMessage())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onPlaceListError(PlaceError error) { }

    private void displayPlace(Place place) {
        txtPlaceTitle.setText(place.getTitle());
        txtPlaceDescription.setText(place.getDescription());
        if ((place.getPhotos() != null) && (place.getPhotos().size() != 0)) {
            recyclerImages = (RecyclerView) findViewById(R.id.images);
            recyclerImages.setLayoutManager(new LinearLayoutManager(this));
            recyclerImages.setAdapter(new ImageAdapter(this, place.getPhotos()));
        }
    }
}
