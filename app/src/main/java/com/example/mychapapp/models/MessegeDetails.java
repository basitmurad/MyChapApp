package com.example.mychapapp.models;

public class MessegeDetails {

    private String messege;
    private String messegeId;
    private String senderId;
    private String pushId;
    private String imageUrl;
    private long timeStamp;
    private int feeling=-1;

    public MessegeDetails() {
    }

//
//    public MessegeDetails(String messege ,String senderId, String pushId) {
//        this.messege = messege;
//
//        this.senderId = senderId;
//        this.pushId = pushId;
//    }

    public MessegeDetails(String messege, String senderId, long timeStamp, String pushID) {
        this.messege = messege;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.pushId= pushID;


    }




    public String getMessegeId() {
        return messegeId;
    }

    public void setMessegeId(String messegeId) {
        this.messegeId = messegeId;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
