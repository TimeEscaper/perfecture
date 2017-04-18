package ru.mail.tp.perfecture.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ru.mail.tp.perfecture.R;
import ru.mail.tp.perfecture.api.ApiService;
import ru.mail.tp.perfecture.api.Place;
import ru.mail.tp.perfecture.api.PlaceList;
import ru.mail.tp.perfecture.places.PlaceInfoActivity;

@SuppressWarnings("FieldCanBeLocal")
public class MapActivity extends Activity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener {

    public GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    @SuppressWarnings("unused")
    private Location location;
    private LocationRequest locationRequest = new LocationRequest();

    private int mapType = GoogleMap.MAP_TYPE_NORMAL;

    private RadioGroup mapTypeGroup;

    private boolean isPending;

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 1;

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

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_maps);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapTypeGroup = (RadioGroup) findViewById(R.id.group_maps);
        mapTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onMapTypeChanged(group, checkedId);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSIONS_MULTIPLE_REQUEST);


        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            alertMessage("Perfecture", "No permissions to access geolocations!");
            finish();
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            Log.d("MapActivity", "Location: " + String.valueOf(location.getLatitude()) + ";" +
                    String.valueOf(location.getLongitude()));
        }
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @SuppressWarnings("unused")
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //TODO: add resolve or ignore
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        startLocationUpdates();
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null) {
            Log.d("MapActivity", "Location: " + String.valueOf(location.getLatitude()) + ";" +
                    String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            long markedPlace = (Long) marker.getTag();
            Intent intent = new Intent(MapActivity.this, PlaceInfoActivity.class);
            intent.putExtra(PlaceInfoActivity.EXTRA_PLACE_TAG, String.valueOf(markedPlace));
            startActivity(intent);
        }
        return false;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            alertMessage("Perfecture", "No permissions to access geolocations!");
            finish();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @SuppressWarnings("UnusedParameters")
    private void onMapTypeChanged(RadioGroup group, int checkedId) {
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
        mMap.setMapType(mapType);
    }

    private void alertMessage(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage(text).setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPlaces(List<Place> places) {
        for (Place place : places) {
            LatLng placeCoord = new LatLng(place.getLatitude(), place.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .title(place.getTitle())
                    .position(placeCoord)).setTag(place.getId());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean fineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (fineLocation && coarseLocation) {
                        mMap.setMapType(mapType);

                        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                if (isPending) {
                                    return false;
                                }
                                if (location != null) {
                                    isPending = true;
                                    ApiService.getInstance().getNearestPlaces(location.getLatitude(),
                                            location.getLongitude(),
                                            new ApiService.ApiCallback<PlaceList>() {
                                                @Override
                                                public void onSuccess(PlaceList result) {
                                                    isPending = false;
                                                    showPlaces(result.getPlaces());
                                                }

                                                @Override
                                                public void onError(Throwable t) {
                                                    Log.d("MapActivity", "Error!");
                                                }
                                            });
                                }
                                return false;
                            }
                        });

                        mMap.setOnMarkerClickListener(this);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                    } else {
                        alertMessage("Perfecture", "No permissions to access geolocations!");
                        finish();
                    }
                    break;
                }
        }

    }
}
