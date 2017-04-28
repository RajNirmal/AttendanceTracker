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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.support.v7.recyclerview.R.attr.layoutManager;

/**
 * Created by nirmal on 24/4/17.
 */

public class RecordsFragment extends Fragment {
    Button callButton;
    TextView resultView,noShow;
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
        noShow = (TextView)subView.findViewById(R.id.nothingtoshow);
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
        dataset = new ArrayList<>();
        StringRequest sr = new StringRequest(Request.Method.POST, SingletonDataClass.URLSelect, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
//            resultView.setText(response.toString());
//            StringBuilder sb = new StringBuilder();
            try{
                JSONArray jsonArray = new JSONArray(response);
                if(jsonArray.length()!=0) {
                    TimeStamp = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TimeStamp[i] = jsonObject.getString("TimeStamp1");
                        //Split the string and then join them in the format needed
                        int ij = TimeStamp[i].indexOf('T');
                        String s = TimeStamp[i].substring(0,ij);
                        String s2 = TimeStamp[i].substring(ij+1,TimeStamp[i].length()-8);
                        String mod = s+" "+s2;
                        String fString = changeDateIntoRequiredFormat(mod);
                        String finalTimestamp = jsonObject.getString("TimeStamp2");
                        int difference;
                        int ij1 = finalTimestamp.indexOf('T');
                        if(ij1 == -1)
                            difference = 7;
                        else {
                            String s1 = finalTimestamp.substring(0,ij1);
                            String s12 = finalTimestamp.substring(ij1+1,TimeStamp[i].length()-8);
                            String finalTimeFormatted = changeDateIntoRequiredFormat(s1+" "+s12);
                            difference = getDurationBWTimeStamps(fString,finalTimeFormatted);
                        }
//                        sb.append(finalTimestamp+"\n");
                        DataEntryModel m = new DataEntryModel(1,fString,difference,1);
                        dataset.add(m);
                    }
                    noShow.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    CustomAdapter myAdapter = new CustomAdapter(dataset,RecordsFragment.this);
                    recyclerView.setAdapter(myAdapter);
//                    resultView.setText(sb.toString());
                }else{
                    recyclerView.setVisibility(View.GONE);
                    noShow.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
//                sb.append(e.toString());
//                resultView.setText(sb.toString());
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

    public int getDurationBWTimeStamps(String Time1, String Time2){
        int diff;
//        if(Time2.equals(""))
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm a");
            Date dateStart = formatter.parse(Time1);
            Date dateEnd = formatter.parse(Time2);
            diff = getDifferenceInHours(dateStart,dateEnd);
        }catch (ParseException e){
            diff = 10;
        }
        return diff;
    }

    public int getDifferenceInHours(Date startDate, Date endDate){
        long secs = (endDate.getTime() - startDate.getTime()) / 1000;
        int hours = (int)secs / 3600;
        if(hours>7)
            return 7;
        else
            return hours;
    }

    public void showToast(String x){
        Toast.makeText(getActivity(), x, Toast.LENGTH_SHORT).show();
    }

    public String changeDateIntoRequiredFormat(String x){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date GMTDate = format.parse(x);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            formatter.setTimeZone(TimeZone.getDefault());
            String LocalDate = formatter.format(GMTDate);
            return LocalDate;
        }catch (ParseException e){
            return "Server down..Login after some time";
        }
    }
}
