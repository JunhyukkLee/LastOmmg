package com.example.lastommg;

import android.net.Uri;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Item implements Serializable {
    int good=0;
    String id;
    String uri;
    String name;
    String phoneNumber;
    GeoPoint geoPoint;
    String address;
    double distance=0.0;
    Timestamp timestamp;
    public Item()  {

    }

    public Item(int good,String id,String name, String uri, String phoneNumber,GeoPoint geoPoint,String address,double distance,Timestamp timestamp)  {
       // this.resId = resId;
        this.good=good;
        this.id=id;
        this.name = name;
        this.uri=uri;
        this.phoneNumber = phoneNumber;
        this.geoPoint=geoPoint;
        this.address=address;
        this.distance=distance;
        this.timestamp=timestamp;
    }


    public int getGood() {
        return good;
    }
    public void setGood() {
        this.good=good;
    }
    public String getId() {
        return id;
    }
    public void setId() {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName() {
        this.name = name;
    }
    public String getUri(){
        return uri;
    }
    public void setUri(){
        this.uri=uri;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber() {
        this.phoneNumber = phoneNumber;
    }
    public GeoPoint getGeoPoint()
    {
        return geoPoint;
    }
    public void setGeoPoint()
    {
        this.geoPoint=geoPoint;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(){
        this.address=address;
    }
    public double getDistance(){
        return distance;
    }
    public void setDistance(){
        this.distance=distance;
    }
    public Timestamp getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(){
        this.timestamp=timestamp;
    }
    public Double getLat(){
        Double lat=geoPoint.getLatitude();
        return lat;
    }
    public Double getLon(){
        Double lon=geoPoint.getLongitude();
        return lon;
    }

}