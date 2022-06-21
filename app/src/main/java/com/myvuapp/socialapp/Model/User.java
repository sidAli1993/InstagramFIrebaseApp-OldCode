package com.myvuapp.socialapp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    public User(String userID, String userName, String address, String city, String phone, String lat, String lng,String deviceID) {
        this.userID = userID;
        this.userName = userName;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.deviceID=deviceID;
    }

    private String userID;
    private String userName = "";
    private String profileImage;
    private int userBeingLiked;
    private String userSelfDescription = "";
    private String deviceID;
    private String address;
    private String city;
    private String phone;
    private String lat;
    private String lng;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }





    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }



    public User()
    {
    }

    public User(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public int getUserBeingLiked() {
        return userBeingLiked;
    }

    public void setUserBeingLiked(int userBeingLiked) {
        this.userBeingLiked = userBeingLiked;
    }

    public void setUserSelfDescription(String userSelfDescription) {
        this.userSelfDescription = userSelfDescription;
    }

    public String getUserSelfDescription() {
        return userSelfDescription;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userProfileImage", profileImage);
        result.put("userSelfDescription", userSelfDescription);
        return result;
    }
}
