package com.example.mychapapp.models;

public class UserDetails {

private  String uniqueID,name,phoneNumber,profileIMage,token;


    public UserDetails() {
    }

    public UserDetails(String uniqueID, String name, String phoneNumber, String profileIMage) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileIMage = profileIMage;
    }



    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileIMage() {
        return profileIMage;
    }

    public void setProfileIMage(String profileIMage) {
        this.profileIMage = profileIMage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
