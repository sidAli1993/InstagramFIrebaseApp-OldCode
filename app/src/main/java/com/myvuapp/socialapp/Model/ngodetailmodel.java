package com.myvuapp.socialapp.Model;

public class ngodetailmodel {
    public ngodetailmodel(String name, String accountNo, String address, String bankName, String ngoID, String phone) {
        Name = name;
        this.accountNo = accountNo;
        this.address = address;
        this.bankName = bankName;
        this.ngoID = ngoID;
        this.phone = phone;
    }
    public ngodetailmodel(){

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNgoID() {
        return ngoID;
    }

    public void setNgoID(String ngoID) {
        this.ngoID = ngoID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String Name;
    private String accountNo;
    private String address;
    private String bankName;
    private String ngoID;
    private String phone;
}
