package com.example.nirmal.attendancetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirmal.attendancetracker.DatabaseHelper.SingletonDataClass;

import static android.R.attr.x;

/**
 * Created by nirmal on 28/4/17.
 */

public class Settings extends AppCompatActivity implements View.OnClickListener{
    TextView userName;
    Button showSTP, changeSTP;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userName = (TextView) findViewById(R.id.usernametextsettings);
        showSTP = (Button) findViewById(R.id.showSTP);
        changeSTP = (Button)findViewById(R.id.changeSTP);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showSTP.setOnClickListener(this);
        changeSTP.setOnClickListener(this);
        String name = getUserName();
        userName.setText(name);
    }

    private String getUserName(){
        SharedPreferences sharedPreferences = getSharedPreferences(SingletonDataClass.SharedPrefsName,MODE_PRIVATE);
        try {
            String x = sharedPreferences.getString(SingletonDataClass.SharedPrefsUserName, "Hello");
            if (x.equals("Hello")) {
                return SingletonDataClass.SharedPrefsUserNameForSession;
            } else {
                return x;
            }
        }catch (NullPointerException e){
            return SingletonDataClass.SharedPrefsUserNameForSession;
        }
    };

    private String getSTP(){
        SharedPreferences sharedPreferences = getSharedPreferences(SingletonDataClass.SharedPrefsName,MODE_PRIVATE);
        try {
            String x = sharedPreferences.getString(SingletonDataClass.SharedPrefsSTP, "Hello");
            if (x.equals("Hello")) {
                return SingletonDataClass.SharedPrefsUserNameForSession;
            } else {
                return x;
            }
        }catch (NullPointerException e){
            return SingletonDataClass.SharedPrefsUserNameForSession;
        }
    };
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.showSTP){
            String x = "The STP is "+getSTP();
            String y = "Please do not share this with anyone";
            AlertBuilder(x,y);
        }
        if(view.getId() == R.id.changeSTP){
            getnewSTP();
        }
    }

    private void setNewSTP(String y){
        SharedPreferences preferences = getSharedPreferences(SingletonDataClass.SharedPrefsName,MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(SingletonDataClass.SharedPrefsSTP,y);
        edit.commit();
    }
    private void getnewSTP(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new STP");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = input.getText().toString();
                if(x.length()==4)
                    setNewSTP(x);
                else
                    Toast.makeText(Settings.this, "The STP must be 4 digits long", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setCancelable(false);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing
                            }
        });
        alert.show();
    }
}
