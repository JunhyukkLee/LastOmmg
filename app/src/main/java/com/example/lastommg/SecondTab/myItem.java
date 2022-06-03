package com.example.lastommg.SecondTab;

import android.net.Uri;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class myItem implements Serializable {
    int good;
    int comment;
    int scrap;
    String nickname;
    String decripthion;
    String uri;
    String name;
    String phoneNumber;
    Double lat;
    Double lon;
    String address;
    double distance=0.0;
    String timestamp;
    public myItem()  {

    }

    public myItem(int good,int comment,int scrap,String nickname,String name,String decripthion, String uri, String phoneNumber,Double lat,Double lon,String address,double distance,String timestamp)  {
        // this.resId = resId;
        this.good=good;
        this.comment=comment;
        this.scrap=scrap;
        this.nickname=nickname;
        this.decripthion=decripthion;
        this.name = name;
        this.uri=uri;
        this.phoneNumber = phoneNumber;
        this.lat=lat;
        this.lon=lon;
        this.address=address;
        this.distance=distance;
        this.timestamp=timestamp;
    }
    public int getGood() {
        return good;
    }
    public void setGood(int good) {
        this.good=good;
    }
    public int getComment() {
        return comment;
    }
    public void setComment(int comment) {
        this.comment=comment;
    }
    public int getScrap() {
        return scrap;
    }
    public void setScrap(int scrap) {
        this.scrap=scrap;
    }
    public String getDecripthion() {
        return decripthion;
    }
    public void setDecripthion() {
        this.decripthion=decripthion;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname() {
        this.nickname = nickname;
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
    public Double getLat()
    {
        return lat;
    }
    public void setLat()
    {
        this.lat=lat;
    }
    public Double getLon()
    {
        return lon;
    }
    public void setLon()
    {
        this.lon=lon;
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
    public String getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(){
        this.timestamp=timestamp;
    }


}