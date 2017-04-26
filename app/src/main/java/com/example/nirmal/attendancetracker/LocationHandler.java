package com.example.nirmal.attendancetracker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nirmal on 25/4/17.
 */

public class LocationHandler extends Service implements LocationListener {

    private final Context myContext;
    // True is GPS is enabled
    boolean isGPSEnabled = false;
    // True is Network is enabled
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    protected LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;
    Intent intent;
    int count = 0;
    // This distance when covered should call a function
    //that will check if the user is inside the campus or not
    private static final long DISTANCE_CALC = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long TIME_UPDATE = 1000 * 60 * 1; // 1 minute

    public LocationHandler(Context context){
        myContext = context;

    }



    public Location getUserLocation(){

        try {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText((MainActivity)myContext,"Returns here",Toast.LENGTH_SHORT).show();
                return null;
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,DISTANCE_CALC,TIME_UPDATE, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,DISTANCE_CALC,TIME_UPDATE, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    public void stopGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationHandler.this);
        }
    }

//      Function to get latitude
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

//    Function to get longitude
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

//      Function to check GPS/wifi enabled
//      @return boolean
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        intent = new Intent(DataModel.STRING_TEST);
//    }


    @Override
    public void onLocationChanged(Location location) {
        getUserLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
