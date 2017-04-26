package com.example.nirmal.attendancetracker;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by nirmal on 24/4/17.
 */

public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener{
    MapView mapView;
    GoogleMap googleMap;
    GoogleApiClient myGoogleClient;
    LocationManager locationManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_map,container,false);
        mapView = (MapView)subView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        if(myGoogleClient == null) {
            myGoogleClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        myGoogleClient.connect();
        MapsInitializer.initialize(this.getActivity());
        return subView;
    }
    public void showUserInMap(Location location){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), DataModel.zoomlevel);
        googleMap.animateCamera(cameraUpdate);
        //Show the user the location of his campus

        CircleOptions campusCircle = new CircleOptions();
        campusCircle.center(new LatLng(DataModel.CampusLatitude,DataModel.CampusLongitude));
        campusCircle.radius(200);
        campusCircle.strokeColor(Color.parseColor(DataModel.mapStrokeColor));
        campusCircle.strokeWidth(4);
        campusCircle.fillColor(Color.parseColor(DataModel.mapFillColor));
        googleMap.addCircle(campusCircle);
    }

    public void checkIfTheUserIsInCampus(Location newLocation){
        float Distance = DataModel.distFrom(newLocation.getLatitude(),newLocation.getLongitude(),DataModel.CampusLatitude,DataModel.CampusLongitude);
        //If the user is in 300 metres of the campus then he can be logged in.
        if(Distance<300){

        }
        Toast.makeText(getActivity(),"The Distance is "+ Distance,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        ((MainActivity)getActivity()).AlertBuilder("Connection Failed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(myGoogleClient);
        if(location!=null) {
            showUserInMap(location);
        }else{
            ((MainActivity)getActivity()).AlertBuilder("Location Null");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        ((MainActivity)getActivity()).AlertBuilder("Connection Suspended");
    }

    @Override
    @SuppressWarnings({"MssingPermission"})
    public void onStart() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,DataModel.TIME_UPDATE,DataModel.DISTANCE_CALC,MapFragment.this);
        myGoogleClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        //This function will allow the map to zoom in on current user location
        //and Check if user has allowed location permission
        checkIfTheUserIsInCampus(location);
        Toast.makeText(getActivity(), "From onLocationChanged\n"+location.getLongitude(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }
}
