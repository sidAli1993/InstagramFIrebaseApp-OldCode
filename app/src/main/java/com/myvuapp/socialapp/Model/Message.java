package com.myvuapp.socialapp.Model;

public class Message {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public Message() {

    }

    private String message;
    private String img;

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }


    public Message(String message, String img, String senderID, String msgTime, boolean iscurrentUser) {
        this.message = message;
        this.img = img;
        this.senderID = senderID;
        this.msgTime = msgTime;
        this.iscurrentUser = iscurrentUser;
    }

    private String senderID;
    private String msgTime;

    public boolean isIscurrentUser() {
        return iscurrentUser;
    }

    public void setIscurrentUser(boolean iscurrentUser) {
        this.iscurrentUser = iscurrentUser;
    }

    private boolean iscurrentUser;
}
