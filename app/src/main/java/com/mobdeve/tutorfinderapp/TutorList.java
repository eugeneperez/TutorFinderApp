package com.mobdeve.tutorfinderapp;

import java.util.ArrayList;

public class TutorList {
    private String email;
    private String fullname;
    private ArrayList<String> categories;
    private String fee;
    private String contact;
    private String status;
    private String image_uri;

    public TutorList(String email, String fullname, ArrayList<String> categories, String fee, String contact, String status, String image_uri) {
        this.email = email;
        this.fullname = fullname;
        this.categories = categories;
        this.fee = fee;
        this.contact = contact;
        this.status = status;
        this.image_uri = image_uri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
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

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
}
