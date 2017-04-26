package com.example.nirmal.attendancetracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nirmal.attendancetracker.DatabaseHelper.SingletonDataClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 24/4/17.
 */

public class RecordsFragment extends Fragment {
    Button callButton;
    TextView resultView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_records,container,false);
        callButton = (Button) subView.findViewById(R.id.TestButton);
        resultView = (TextView)subView.findViewById(R.id.dummyText);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callVolley();
            }
        });
        return subView;
    }
    public void callVolley(){
        StringRequest sr = new StringRequest(Request.Method.POST, SingletonDataClass.URLSelect, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
            resultView.setText(response.toString());
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            resultView.setText(error.toString());
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
}
