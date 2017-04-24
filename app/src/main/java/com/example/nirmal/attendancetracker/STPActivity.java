package com.example.nirmal.attendancetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by nirmal on 23/4/17.
 */

public class STPActivity extends AppCompatActivity implements View.OnClickListener{
    EditText STPEditText;
    Button STPButton;
    boolean flag;//If the flag is true then the user already has logged in before
    String STP;//This is the password that user needs to enter
    int count = 0;//This variable will keep the count of how many times the user has entered the wrong STP
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stp);
//        getSupportActionBar().hide();
        //Check if the user already has a STP
        flag = checkPreviousUserData();
        //Initialise the views
        initializeViews();
    }

    //This function will initialise all the views in the layout and map it into the object in java file
    //Also set the onClickListener for the buttons
    public void initializeViews(){
        STPButton = (Button) findViewById(R.id.loginButton);
        STPEditText = (EditText) findViewById(R.id.userRegisterNo);
        STPButton.setOnClickListener(this);
    }

    //Handle click events
    @Override
    public void onClick(View view) {
        String title,body;
        if(view == STPButton) {
            try {
                String userSTP = STPEditText.getText().toString();
                //Check if the user has given a valid input
                if (!(userSTP.isEmpty()) && (userSTP.length() == 4)) {
                    if (flag) {
                        //The user has already logged in so check if the user entered STP is correct
                        //The data is already present in STP string. This was taken care of by checkPreviousUserData()
                        if (userSTP.equals(STP)) {
                            changeActivity();
                        } else {
                            if(count<3) {
                                title = "Wrong STP";
                                body = "You have " + String.valueOf(3 - count++) + " chances remaining";
                                AlertBuilder(title,body);
                            }else {
                                title = "Wrong STP";
                                body = "You have exceeded your limit. Try again later";
                                AlertBuilder(title, body,true);
                            }
                        }
                    } else {
                        //The user has not logged in so store the STP in sharedprefs
                        writeDatainSharedPrefs(userSTP);
                        changeActivity();
                    }
                } else {//This executes if the user has given invalid input
                    title = "Invalid Details";
                    body = "The STP should be a 4 digit number";
                    AlertBuilder(title, body);
                }
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This function enters the STP in sharedprefs for the first time
    public void writeDatainSharedPrefs(String userData){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DataModel.SharedPrefsName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataModel.SharedPrefsSTP, userData);
        editor.commit();
    }

    //This function redirects the user to the main page
    public void changeActivity(){
        Intent i = new Intent(STPActivity.this,MainActivity.class);
        startActivity(i);
    }

    //If user makes a mistake alert them by calling this function with a message and title
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(STPActivity.this);
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                STPEditText.setText("");
            }
        });
        alert.show();
    }
    //If user makes a mistake alert them by calling this function with a message and title
    public void AlertBuilder(String AlertTitle, String AlertBody,boolean flag){
        final AlertDialog.Builder alert = new AlertDialog.Builder(STPActivity.this);
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Enter the code again.
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alert.show();
    }
    //This function will check if the user has already logged in the app before
    //if so then the STP is taken and stored
    public boolean checkPreviousUserData(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DataModel.SharedPrefsName,MODE_PRIVATE);
        try {
            STP = sharedPreferences.getString(DataModel.SharedPrefsSTP, DataModel.SharedPrefsDefault);
            if (STP.equals(DataModel.SharedPrefsDefault)){
                //No user data was found
                //This is the first time the user is logging in to the app
                return false;
            }else{
                //The user has already logged into the app
                //So ask for the STP again
                return true;
            }
        }catch (NullPointerException e){
            //Some error occured
            //Most probably because user has not logged in before
            return false;
        }
    }
}
