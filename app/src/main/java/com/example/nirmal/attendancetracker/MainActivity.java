package com.example.nirmal.attendancetracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.nirmal.attendancetracker.DataModel.PERMS;


public class MainActivity extends AppCompatActivity {
    Button next;
    private int icons[] = {R.drawable.mapmarker,R.drawable.calendarclock};
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check and get all permission
        if (Build.VERSION.SDK_INT >= 23)
            checkPermission();
        //Initialise all the views
        initViews();
        //Initialise the volley singleton item so that network calls can be made
        //from anywhere in the app
        initVolley();

    }

    public void initVolley(){
        DataModel.initialiseVolley(getApplicationContext());
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission();
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, DataModel.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DataModel.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    String title = "Permission Denied";
                    String body = "Please allow this permissin to use the app";
                    AlertBuilder(title,body);
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    //Initialise all the views
    //Setup the viewpager and inflate them with the necessary data
    public void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewPager = (ViewPager) findViewById(R.id.viewpagers);
        tabLayout = (TabLayout) findViewById(R.id.tablayers);
        //Set up the adapter
        ViewPagerCustomAdapter adapter = new ViewPagerCustomAdapter(getSupportFragmentManager());
        adapter.addFragmentToTabs(new MapFragment(),"    Location");
        adapter.addFragmentToTabs(new RecordsFragment(),"    Records");
        //Assign the adapter to the viewpager
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //Set the tablayout with icons and text
        tabLayout.getTabAt(0).setIcon(icons[0]);
        tabLayout.getTabAt(1).setIcon(icons[1]);
    }

    public void AlertBuilder(String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Whatever");
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
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
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

    public class ViewPagerCustomAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragmentList = new ArrayList<>();
        private final ArrayList<String> fragmentListTitle = new ArrayList<>();

        public ViewPagerCustomAdapter(FragmentManager manager){
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentListTitle.get(position);
        }

        public void addFragmentToTabs(Fragment fragment, String Title){
            fragmentList.add(fragment);
            fragmentListTitle.add(Title);
        }
    }
}
