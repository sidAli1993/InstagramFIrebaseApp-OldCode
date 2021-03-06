package com.myvuapp.socialapp.Model;

import com.google.firebase.database.Exclude;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postID = "";
    private String postPublisher = "";
    private String postTitle = ""; //book title
    private String postContent = "";
    private String postImage;
    private String postTime; //post publish time
    private Integer postBeingSaved;
    private String saleType;
    private String shelve;
    private String price;
    private String authorName;

    public String getPostCity() {
        return postCity;
    }

    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }

    private String postCity;




    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getShelve() {
        return shelve;
    }

    public void setShelve(String shelve) {
        this.shelve = shelve;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ArrayList<Comment> getCommentsUnderPost() {
        return commentsUnderPost;
    }

    public void setCommentsUnderPost(ArrayList<Comment> commentsUnderPost) {
        this.commentsUnderPost = commentsUnderPost;
    }


    private ArrayList<Comment> commentsUnderPost;

    public Post(String postID, String postPublisher, String postImage, String postTitle, String postContent, String postTime, Integer postBeingSaved,String authorname,String price,String saletype,String shelve,String postCity){
        this.postID = postID;
        this.postPublisher = postPublisher;
        this.postImage = postImage;
        this.postContent = postContent;
        this.postTitle = postTitle;
        this.postTime = postTime;
        this.postBeingSaved = postBeingSaved;
        this.authorName = authorname;
        this.price = price;
        this.saleType = saletype;
        this.shelve = shelve;
        this.postCity=postCity;
    }
    public Post(){

    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostPublisher() {
        return postPublisher;
    }

    public void setPostPublisher(String postPublisher) {
        this.postPublisher = postPublisher;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postID", postID);
        result.put("postPublisher", postPublisher);
        result.put("postTitle", postTitle);
        result.put("postContent", postContent);
        result.put("postImage", postImage);
        result.put("postTime", postTime);
        result.put("postBeingSaved", postBeingSaved);
        return result;
    }

    public Integer getPostBeingSaved() {
        return postBeingSaved;
    }

    public void setPostBeingSaved(Integer postBeingSaved) {
        this.postBeingSaved = postBeingSaved;
    }
}
