package ru.mail.tp.perfecture.map.presenter;

import android.Manifest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import ru.mail.tp.perfecture.map.view.IMapView;
import ru.mail.tp.perfecture.mvp.Presenter;

/**
 * Created by maksimus on 01.04.17.
 */

public class MapPresenter extends Presenter implements OnMapReadyCallback {
    private IMapView mapView;
    private GoogleMap map;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;

    public  MapPresenter(IMapView mapView) {
        this.mapView = mapView;
    }

    public void setMapType(int type) {
        this.mapType = type;
        map.setMapType(mapType);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setMapType(mapType);
        mapView.checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onDestroy() {
        mapView = null;
        super.onDestroy();
    }
}
