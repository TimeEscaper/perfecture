package ru.mail.tp.perfecture.places;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceError;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.places.adapters.PlacesAdapter;
import ru.mail.tp.perfecture.places.manager.PlaceManager;

@SuppressWarnings("unused")
public class PlacesListActivity extends AppCompatActivity
        implements PlaceManager.ManagerListener {

    private static final String TAG = PlacesListActivity.class.getName();
    private static final String STATE_LISTENER_ID = "listenerId";
    private static final String STATE_PENDING = "pending";

    private RecyclerView recyclerPlaces;

    private int listenerId = 0;
    private boolean isPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects_list);

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
            PlaceManager.getInstance().getAllPlacesList(listenerId);
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
    public void onPlaceSuccess(Place result) { }

    @Override
    public void onPlaceListSuccess(PlaceList result) {
        isPending = false;
        if (!result.getPlaces().isEmpty()) {
            recyclerPlaces = (RecyclerView) findViewById(R.id.places);
            recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
            recyclerPlaces.setAdapter(new PlacesAdapter(this, result.getPlaces()));
        }
    }

    @Override
    public void onPlaceError(PlaceError error) { }

    @Override
    public void onPlaceListError(PlaceError error) {
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
}
