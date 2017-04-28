package com.example.nirmal.attendancetracker;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirmal.attendancetracker.DatabaseHelper.DataEntryModel;
import com.example.nirmal.attendancetracker.DatabaseHelper.SingletonDataClass;

import java.util.ArrayList;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

/**
 * Created by nirmal on 26/4/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    ArrayList<DataEntryModel> dataSet;
    CardView cards;
    RecordsFragment frag;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView UserName, TimeStamp, Duration;
        public MyViewHolder(View itemView){
            super(itemView);
            this.UserName = (TextView)itemView.findViewById(R.id.userName);
            TimeStamp = (TextView)itemView.findViewById(R.id.timestamp_of_user);
            Duration = (TextView)itemView.findViewById(R.id.duration_of_user);
            cards = (CardView)itemView.findViewById(R.id.card_view);
            cards.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            String x = String.valueOf(dataSet.get(getAdapterPosition()).getDuration());
            frag.showToast(x);
        }
    }
    public CustomAdapter(ArrayList<DataEntryModel> data, RecordsFragment fragment){
        dataSet = data;
        frag = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_fragment,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextView userName = holder.UserName;
        TextView timeStamp = holder.TimeStamp;
        TextView durationSpent = holder.Duration;
        userName.setText(SingletonDataClass.SharedPrefsUserNameForSession);
        timeStamp.setText(dataSet.get(position).getTimeStamp());
        durationSpent.setText(String.valueOf(dataSet.get(position).getDuration()));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
