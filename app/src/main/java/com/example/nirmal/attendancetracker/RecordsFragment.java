package com.example.nirmal.attendancetracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.nirmal.attendancetracker.DatabaseHelper.DataEntryModel;
import com.example.nirmal.attendancetracker.DatabaseHelper.SingletonDataClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.v7.recyclerview.R.attr.layoutManager;

/**
 * Created by nirmal on 24/4/17.
 */

public class RecordsFragment extends Fragment {
    Button callButton;
    TextView resultView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String TimeStamp[];
    ArrayList<DataEntryModel> dataset;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_records,container,false);
        callButton = (Button) subView.findViewById(R.id.TestButton);
        resultView = (TextView)subView.findViewById(R.id.dummyText);
        recyclerView = (RecyclerView)subView.findViewById(R.id.my_recycler_view);
        dataset = new ArrayList<>();
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this.getActivity());
        callVolley();
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callVolley();
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return subView;
    }
    public void callVolley(){

        StringRequest sr = new StringRequest(Request.Method.POST, SingletonDataClass.URLSelect, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
//            resultView.setText(response.toString());
            StringBuilder sb = new StringBuilder();
            try{
                JSONArray jsonArray = new JSONArray(response);
                if(jsonArray.length()!=0) {
                    TimeStamp = new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TimeStamp[i] = jsonObject.getString("TimeStamp1");
                        int ij = TimeStamp[i].indexOf('T');
                        String s = TimeStamp[i].substring(0,ij-1);
                        String s2 = TimeStamp[i].substring(ij+1,TimeStamp[i].length()-1);
                        sb.append(s+"\n"+s2+"\n");
                        String mod = s+" "+s2;
                        dataset.add(new DataEntryModel(1,s2,10,1));
                    }
                    CustomAdapter myAdapter = new CustomAdapter(dataset);
                    recyclerView.setAdapter(myAdapter);
//                    resultView.setText(sb.toString());
                }else{

                }
            }catch (Exception e){
                sb.append(e.toString());
                resultView.setText(sb.toString());
            }
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
