package com.example.nirmal.attendancetracker;

import android.Manifest;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nirmal on 23/4/17.
 */

//This is a singleton class that will be holding all the
//variables that are needed by the app and also the volley
//libraries that are going to be used will be stored here
public class DataModel {
    //Shared preferences name and the key names are declared
    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final String SharedPrefsName = "UserDetails";
    public static final String SharedPrefsUserName = "UserName";
    public static final String SharedPrefsSTP = "STP";
    public static final String SharedPrefsDefault = "Default";

    //Permission related Constants
    public static final String[] PERMS= {Manifest.permission.ACCESS_FINE_LOCATION};

    //Map related Constants
    public static final String GoogleMapsAPIKey = "AIzaSyCYnuRbF9ZCpFkCyeOA6MmhXL2HCyHQtH0";
    public static final double CampusLongitude = 80.141702;
    public static final double CampusLatitude = 12.947622;
    public static final Integer zoomlevel = 15;
    public static final String mapFillColor = "#c5cae9";
    public static final String mapStrokeColor = "#ef9a9a";
    public static final long DISTANCE_CALC = 10; // 10 meters
    // The minimum time between updates in milliseconds
    public static final long TIME_UPDATE = 1000 * 60 * 1; // 1 minute
    public static final String STRING_TEST = "Halo";

    //Declare parameters for volley and initialise it
    public static RequestQueue VolleyRequestQueue;
    public static void initialiseVolley(Context context){
        VolleyRequestQueue = Volley.newRequestQueue(context);
    }

    //URL where data can be fetched from
    public static final String URLTest = "https://rokensho.herokuapp.com/test";
    //A static class that will convert the difference between two lat and long into metre
    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}
