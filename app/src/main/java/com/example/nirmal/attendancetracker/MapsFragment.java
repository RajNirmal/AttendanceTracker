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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by nirmal on 25/4/17.
 */

public class MapsFragment extends Fragment implements LocationListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{
    MapView mapView;
    GoogleMap googleMap;
    LocationHandler locHandler;
    LocationManager locationManager;
    GoogleApiClient myGoogleClient;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_map,container,false);
        mapView = (MapView)subView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        locHandler = new LocationHandler(this.getActivity());
        if (myGoogleClient == null) {
            myGoogleClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        MapsInitializer.initialize(this.getActivity());

        return subView;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(myGoogleClient);
        if(location!=null) {
            Toast.makeText(getActivity(), location.toString(), Toast.LENGTH_SHORT).show();
            zoomIntoUser(location);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //This function will show a circle around the users campus
    //So the user will know where he has to be in order to register his attendance
    public void showCampusCircle(){
        CircleOptions campusCircle = new CircleOptions();
        campusCircle.center(new LatLng(DataModel.CampusLatitude,DataModel.CampusLongitude));
        campusCircle.radius(200);
        campusCircle.strokeColor(Color.parseColor(DataModel.mapStrokeColor));
        campusCircle.strokeWidth(4);
        campusCircle.fillColor(Color.parseColor(DataModel.mapFillColor));
        googleMap.addCircle(campusCircle);
    }

    public void zoomIntoUser(Location location){
        Toast.makeText(getActivity(),location.getLatitude()+"  "+location.getLongitude()+"",Toast.LENGTH_SHORT).show();
        String title = "Unable to get location";
        String body = "Please login to the app after turning on the GPS";
        showTheUserInMap(location);


        //Check if the OS is Marshmellow..If so get the user permission
        if (Build.VERSION.SDK_INT >= 23){
            if (checkPermission()) {
                if(location!=null)
                    showTheUserInMap(location);
                else
                    AlertBuilder(title,body);
            }else {
                requestPermission();
            }
        }else {
            //The build is less than marshmellow so no need for permission
            location = locHandler.getUserLocation();
            try {
//                double d = location.getLatitude();
                showTheUserInMap(location);
            } catch (NullPointerException e) {
//                Toast.makeText(getActivity(),"Being called from here"+e.toString(),Toast.LENGTH_SHORT).show();
//                AlertBuilder(title, body);
            }


        }
    }

    //If user makes a mistake alert them by calling this function with a message and title
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder((MainActivity)getActivity());
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setCancelable(false);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Close the app.
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alert.show();
    }

    public void showTheUserInMap(Location location){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), DataModel.zoomlevel);
        googleMap.animateCamera(cameraUpdate);
        //Show the user the location of his campus
        showCampusCircle();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, DataModel.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        myGoogleClient.connect();
        super.onStart();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStop() {
        myGoogleClient.disconnect();
        super.onStop();

    }

    @Override
    public void onLocationChanged(Location location) {
        //This function will allow the map to zoom in on current user location
        //and Check if user has allowed location permission
        Toast.makeText(getActivity(), "From onLocationChanged", Toast.LENGTH_SHORT).show();
        zoomIntoUser(location);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DataModel.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}
