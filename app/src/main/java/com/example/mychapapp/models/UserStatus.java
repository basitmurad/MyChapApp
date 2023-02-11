package com.example.mychapapp.models;

import java.util.ArrayList;

public class UserStatus {
    private String userName,profileImage;
    private  long lastUpdate;
    private ArrayList<Status> statuses;


    public UserStatus() {
    }

    public UserStatus(String userName, String profileImage, long lastUpdate, ArrayList<Status> statuses) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.lastUpdate = lastUpdate;
        this.statuses = statuses;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
