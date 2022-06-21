package com.myvuapp.socialapp.Model;

public class ngomodel {
    public ngomodel(String ngoid, String ngoname) {
        this.ngoid = ngoid;
        this.ngoname = ngoname;
    }

    public String getNgoid() {
        return ngoid;
    }

    public void setNgoid(String ngoid) {
        this.ngoid = ngoid;
    }

    public String getNgoname() {
        return ngoname;
    }

    public void setNgoname(String ngoname) {
        this.ngoname = ngoname;
    }

    private String ngoid;
    private String ngoname;
}
