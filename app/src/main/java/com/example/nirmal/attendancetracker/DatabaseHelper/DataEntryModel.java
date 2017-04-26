package com.example.nirmal.attendancetracker.DatabaseHelper;

/**
 * Created by nirmal on 26/4/17.
 */

//This POJO class will define the structure of data that is going to inserted
//    into the local database
//    THis will allow the data model to be accessed from anywhere in the app
public class DataEntryModel {

    int UniqueId,Flag,Duration;
    String TimeStamp;

    public DataEntryModel(){

    }
    public DataEntryModel(int ID,String time,int duration,int flag){
        UniqueId = ID;
        TimeStamp = time;
        Duration = duration;
        Flag = flag;
    }
    public int getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

}
