package com.myvuapp.socialapp.Model;

public class messages {
    public messages(String senderImg, String sendername, String senderID, String status, String time, String inboxID, String recieverID, String recieverName, String recieverImg) {
        this.senderImg = senderImg;
        this.sendername = sendername;
        this.senderID = senderID;
        this.status = status;
        this.time = time;
        this.inboxID = inboxID;
        this.recieverID = recieverID;
        this.recieverName = recieverName;
        this.recieverImg = recieverImg;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInboxID() {
        return inboxID;
    }

    public void setInboxID(String inboxID) {
        this.inboxID = inboxID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getRecieverImg() {
        return recieverImg;
    }

    public void setRecieverImg(String recieverImg) {
        this.recieverImg = recieverImg;
    }

    public messages() {
    }

    private String senderImg;
    private String sendername;
    private String senderID;
    private String status;
    private String time;
    private String inboxID;
    private String recieverID;
    private String recieverName;
    private String recieverImg;



}
