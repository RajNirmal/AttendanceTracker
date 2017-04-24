package com.example.nirmal.attendancetracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nirmal on 24/4/17.
 */

public class RecordsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.fragment_records,container,false);
        return subView;
    }
}
