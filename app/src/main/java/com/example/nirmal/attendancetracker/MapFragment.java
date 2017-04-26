package com.example.nirmal.attendancetracker;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nirmal.attendancetracker.DatabaseHelper.DBHandler;
import com.example.nirmal.attendancetracker.DatabaseHelper.DataEntryModel;
import com.example.nirmal.attendancetracker.DatabaseHelper.SingletonDataClass;
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

import java.nio.channels.Pipe;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.LogRecord;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by nirmal on 24/4/17.
 */

public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener{
    String title = "What would you like to do?";
    String body = "Choose an appropriate action";
    MapView mapView;
    GoogleMap googleMap;
    Runnable runnable;
    Handler handler;
    GoogleApiClient myGoogleClient;
    Boolean myFlag = false;
    LocationManager locationManager;
    FloatingActionButton floatingActionButton;
    DBHandler dbHelperInstance;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_map,container,false);
        mapView = (MapView)subView.findViewById(R.id.map);
        floatingActionButton = (FloatingActionButton)subView.findViewById(R.id.fab);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        handler = new Handler();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        dbHelperInstance = new DBHandler(getContext());
        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        if(myGoogleClient == null) {
            myGoogleClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertBuilder(title,body);
            }
        });
        myGoogleClient.connect();
        MapsInitializer.initialize(this.getActivity());
        runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    boolean flag = isNetworkAvailable();
                    if(flag){
                        floatingActionButton.setEnabled(true);
                    }else{
                        floatingActionButton.setEnabled(false);
                        Toast.makeText(getActivity(), "Please switch in Wi-Fi, GPS and login again", Toast.LENGTH_SHORT).show();
                    }
                }finally {
                    handler.postDelayed(runnable,2000);
                }
            }
        };
        runnable.run();
        return subView;
    }

    public void showUserInMap(Location location){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), SingletonDataClass.zoomlevel);
        googleMap.animateCamera(cameraUpdate);
        //Show the user the location of his campus
        CircleOptions campusCircle = new CircleOptions();
        campusCircle.center(new LatLng(SingletonDataClass.CampusLatitude, SingletonDataClass.CampusLongitude));
        campusCircle.radius(300);
        campusCircle.strokeColor(Color.parseColor(SingletonDataClass.mapStrokeColor));
        campusCircle.strokeWidth(4);
        campusCircle.fillColor(Color.parseColor(SingletonDataClass.mapFillColor));
        googleMap.addCircle(campusCircle);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager  = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void AlertBuilder(String AlertTitle, String AlertBody,boolean flag){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setNegativeButton("Stop Attendance", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(SingletonDataClass.SharedPrefsFlag == 1){
                    SingletonDataClass.SharedPrefsFlag = 0;
                    updateDataInServer();
                }else {
                    Toast.makeText(getActivity(), "You must log in first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setPositiveButton("Start Attendance", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(SingletonDataClass.SharedPrefsFlag == 0){
                    if(myFlag){
                        insertDataInServer();
                        SingletonDataClass.SharedPrefsFlag = 1;
                    }else{
                        String title = "You are not in the location";
                        String body = "Please try to log in after you are inside the blue circle";
                        AlertBuilder(title,body,true);
                    }
                }else{
                    Toast.makeText(getActivity(), "You are already logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.show();
    }

    public boolean checkIfTheUserIsInCampus(Location newLocation){
        float Distance = SingletonDataClass.distFrom(newLocation.getLatitude(),newLocation.getLongitude(), SingletonDataClass.CampusLatitude, SingletonDataClass.CampusLongitude);
        //If the user is in 300 metres of the campus then he can be logged in.
        if(Distance<300){
            return true;
//            writeCurrentDataToLocalDB();
        }else{
            return false;
        }
    }

    public void updateDataInServer(){
        StringRequest sr = new StringRequest(Request.Method.POST, SingletonDataClass.URLUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Some error occured try again later",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps = new HashMap<>();
                maps.put(SingletonDataClass.KeyUserName,SingletonDataClass.SharedPrefsUserNameForSession);
                maps.put(SingletonDataClass.KeyUniqueID,SingletonDataClass.SharedPrefsUniqueId);
                return maps;
            }
        };
        SingletonDataClass.VolleyRequestQueue.add(sr);
    }
    public void insertDataInServer(){
        StringRequest sr = new StringRequest(Request.Method.POST, SingletonDataClass.URLInsert, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    int x = Integer.parseInt(response);
                    SingletonDataClass.SharedPrefsUniqueId = response;
                }catch (NumberFormatException e){
                    Toast.makeText(getActivity(),"Some error occured try again later",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps = new HashMap<>();
                maps.put(SingletonDataClass.KeyUserName,SingletonDataClass.SharedPrefsUserNameForSession);
                return maps;
            }
        };
        SingletonDataClass.VolleyRequestQueue.add(sr);
    }

    public void writeCurrentDataToLocalDB(){
        //Get The Unique ID
        ArrayList<DataEntryModel> allEntries = dbHelperInstance.getAllEntries();
        int UniqueId ;
        if(allEntries.isEmpty()){
            UniqueId = 1;
        }else{
            UniqueId = getMax(allEntries);
        }
//        DataEntryModel WriteIntoDB = new DataEntryModel(UniqueId,"Default",)
    }

    public int getMax(ArrayList<DataEntryModel> data){
        int max = 0;
        for(int i=0;i<data.size();i++){
            if(data.get(i).getUniqueId() > max)
                max = data.get(i).getUniqueId();
        }
        return max;
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
            myFlag = checkIfTheUserIsInCampus(location);
            if(myFlag){
                AlertBuilder(title,body);
            }
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SingletonDataClass.TIME_UPDATE, SingletonDataClass.DISTANCE_CALC,MapFragment.this);
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
        myFlag = checkIfTheUserIsInCampus(location);
        if(myFlag){
            AlertBuilder(title,body);
        }
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
