package com.myvuapp.socialapp.Model;

public class reportpostmodel {
    public reportpostmodel(String task, String date, String time, String postID, String reportbyID) {
        this.task = task;
        this.date = date;
        this.time = time;
        this.postID = postID;
        this.reportbyID = reportbyID;
    }
    public reportpostmodel(){

    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getReportbyID() {
        return reportbyID;
    }

    public void setReportbyID(String reportbyID) {
        this.reportbyID = reportbyID;
    }

    private String task;
    private String date;
    private String time;
    private String postID;
    private String reportbyID;
}
