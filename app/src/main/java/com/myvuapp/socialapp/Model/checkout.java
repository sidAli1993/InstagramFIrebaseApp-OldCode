package com.myvuapp.socialapp.Model;

public class checkout {
    public checkout(String checkoutID, String bookName, String authorName, String price, String postID, String saleType, String address, String city, String phone, String checkoutBY, String date, String time,String buyername) {
        this.checkoutID = checkoutID;
        this.bookName = bookName;
        this.authorName = authorName;
        this.price = price;
        this.postID = postID;
        this.saleType = saleType;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.checkoutBY = checkoutBY;
        this.date = date;
        this.time = time;
        this.buyername=buyername;
    }

    public String getCheckoutID() {
        return checkoutID;
    }

    public void setCheckoutID(String checkoutID) {
        this.checkoutID = checkoutID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
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

    public String getCheckoutBY() {
        return checkoutBY;
    }

    public void setCheckoutBY(String checkoutBY) {
        this.checkoutBY = checkoutBY;
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

    public checkout() {

    }

    private String checkoutID;
    private String bookName;
    private String authorName;
    private String price;
    private String postID;
    private String saleType;
    private String address;
    private String city;
    private String phone;
    private String checkoutBY;
    private String date;
    private String time;
    private String buyername;

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }


}
