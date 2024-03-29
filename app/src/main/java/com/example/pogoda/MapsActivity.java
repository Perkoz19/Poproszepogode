package com.example.pogoda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener{

    private static final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    Marker gpsMarker = null;
    Marker mapmarker = null;

    LatLng markerPos = null;

    //List<Marker> markerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //markerList = new ArrayList<>();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLoadedCallback(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    private void createLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult != null){
                    if(gpsMarker != null)
                        gpsMarker.remove();
                    Location location = locationResult.getLastLocation();
                    gpsMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).alpha(0.8f).title("Current Location"));
                }
            }
        };
    }

    @Override
    public void onMapLoaded() {
        Log.i(MapsActivity.class.getSimpleName(), "MapLoaded");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        Task<Location> lastLocation = fusedLocationClient.getLastLocation();
        lastLocation.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null && mMap != null){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(getString(R.string.last_known_loc_msg)));
                }
            }
        });
        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();

    }

    @Override
    protected void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        if(locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

/*    @Override
    public void onMapLongClick(LatLng latLng) {
        float distance = 0f;
        if(markerList.size() > 0){
            Marker lastMarker = markerList.get(markerList.size()-1);
            float [] tmpDis = new float[3];
            Location.distanceBetween(lastMarker.getPosition().latitude, lastMarker.getPosition().longitude, latLng.latitude, latLng.longitude, tmpDis);
            distance = tmpDis[0];

            PolylineOptions rectOptions = new PolylineOptions().add(lastMarker.getPosition()).add(latLng).width(10).color(Color.BLUE);
            mMap.addPolyline(rectOptions);
        }
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).alpha(0.8f).title(String.format("Position; (%.2f, %.2f), Distance: %.2f",latLng.latitude, latLng.longitude, distance)));
        markerList.add(marker);
    } */

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(mapmarker != null) {
            mapmarker.remove();
            markerPos = null;
        }
        mapmarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).alpha(0.8f).title(String.format("Selected location: (%.2f, %.2f)", latLng.latitude, latLng.longitude)));
        markerPos = mapmarker.getPosition();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //CameraPosition cameraPos = mMap.getCameraPosition();
        //if(cameraPos.zoom < 14f)
        //    mMap.moveCamera(CameraUpdateFactory.zoomTo(14f));
        //new AlertDialog.Builder(this).setTitle("abc");
        //String poptitle = getResources().getString(R.string.title);
        Intent pop = new Intent(MapsActivity.this, MarkerInfo.class);
        //LatLng temp = mapmarker.getPosition();
        //String templat = Double.toString(temp.latitude);
        pop.putExtra("ltt", Integer.toString((int)mapmarker.getPosition().latitude));
        pop.putExtra("lgt", Integer.toString((int)mapmarker.getPosition().longitude));
        startActivity(pop);
        return false;
    }

}
