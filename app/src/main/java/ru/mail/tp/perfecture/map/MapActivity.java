package ru.mail.tp.perfecture.map;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.LinkedList;
import java.util.List;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.map.presenter.MapPresenter;
import ru.mail.tp.perfecture.map.view.IMapView;
import ru.mail.tp.perfecture.mvp.PresenterActivity;

/**
 * Created by maksimus on 01.04.17.
 */

public class MapActivity extends PresenterActivity<MapPresenter> implements IMapView {

    private RadioGroup mapTypeGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MapPresenter(this);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(presenter);

        mapTypeGroup = (RadioGroup) findViewById(R.id.group_maps);
        mapTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onMapTypeChanged(group, checkedId);
            }
        });
    }

    @Override
    public void checkPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> notGrantedList = new LinkedList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    notGrantedList.add(permission);
                }
            }
            if (notGrantedList.isEmpty()) {
                presenter.onPermissionGranted();
            } else {
                presenter.onPermissionNotGranted(notGrantedList);
            }
        }
    }

    private void onMapTypeChanged(RadioGroup group, int checkedId) {
        int mapType;
        switch (checkedId) {
            case R.id.radio_map:
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
            case R.id.radio_hybrid:
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                break;
            case R.id.radio_terrain:
                mapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            default:
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
        }
        presenter.setMapType(mapType);
    }
}
