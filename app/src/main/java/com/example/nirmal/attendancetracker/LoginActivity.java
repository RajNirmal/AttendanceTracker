package com.example.nirmal.attendancetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Button loginButton;
    EditText registerNo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //No need to show the action bar in login page
//        getSupportActionBar().hide();
        //Check if the user has already logged in
        checkPreviousUserData();
        //Initialise the views
        initializeViews();

    }

    //This function will initialise all the views in the layout and map it into the object in java file
    //Also set the onClickListener for the buttons
    public void initializeViews(){
        loginButton = (Button) findViewById(R.id.loginButton);
        registerNo = (EditText) findViewById(R.id.userRegisterNo);
        loginButton.setOnClickListener(this);
    }

    //This function will check if the user has already logged in the app before
    //if so then he should be redirected to the STP page
    public void checkPreviousUserData(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DataModel.SharedPrefsName,MODE_PRIVATE);
        try {
            String userName = sharedPreferences.getString(DataModel.SharedPrefsUserName, DataModel.SharedPrefsDefault);
            if (userName.equals(DataModel.SharedPrefsDefault)){
                //No user data was found
                //This is the first time the user is logging in to the app
            }else{
                //The user has already logged into the app
                //So no need to ask for the user ID again
                //Redirect the user the STP screen
                changeActivity();
            }
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),"Some error occurred with the previous users data\nPlease enter the data again",Toast.LENGTH_SHORT).show();
        }
    }

    //This function will handle all the onclick events
    @Override
    public void onClick(View view) {
        String title,body;
        if(view == loginButton){
            String userID = registerNo.getText().toString();
            //Runs if user clicks enter without entering any data
            if(userID.isEmpty()){
                title = "Invalid Details";
                body = "Enter all fields";
                AlertBuilder(title,body);
            }else if(userID.length()!=6){//Runs if user enters invalid data
                title = "Invalid Details";
                body = "The ID should be a 6 digit number";
                AlertBuilder(title,body);
            }else{//Runs if user enters valid data
                writeUserNameinSharedPrefs(userID);
                changeActivity();
            }
        }
    }

    //Change the activity to the STP page
    public void changeActivity(){
        Intent i = new Intent(LoginActivity.this,STPActivity.class);
        startActivity(i);
    }

    //Write the user name in shared prefs for future usage
    private void writeUserNameinSharedPrefs(String UserName){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DataModel.SharedPrefsName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataModel.SharedPrefsUserName,UserName);
        editor.commit();
    }

    //If user makes a mistake alert them by calling this function with a message and title
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Enter the code again.
            }
        });
        alert.show();
    }
}
