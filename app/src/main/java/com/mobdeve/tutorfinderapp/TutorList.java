package com.mobdeve.tutorfinderapp;

public class TutorList {
    private String fullname;
    private String categories;
    private String fee;
    private String contact;
    private String status;

    public TutorList(String fullname, String categories, String fee, String contact, String status) {
        this.fullname = fullname;
        this.categories = categories;
        this.fee = fee;
        this.contact = contact;
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
