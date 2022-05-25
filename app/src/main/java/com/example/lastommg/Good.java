package com.example.lastommg;


import android.net.Uri;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Good  {
    int count=0;
    ArrayList<String> id=new ArrayList<>();


    public Good(int count, ArrayList<String> id)  {
        // this.resId = resId;
       this.count=count;
       this.id=id;

    }



    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count=count;
    }
    public ArrayList<String> getId() {
        return id;
    }
    public void setId() {
        this.id = id;
    }


}