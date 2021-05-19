package com.mobdeve.tutorfinderapp;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private String contact;
    private ArrayList<String> categories;
    private String fee;
    private String profpic;

    public User(String email, String firstname, String lastname, String contact, ArrayList<String> categories, String fee) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        this.categories= categories;
        this.fee=fee;
    }

    public String getProfpic() {
        return profpic;
    }

    public void setProfpic(String profpic) {
        this.profpic = profpic;
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

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getContact() {
        return contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
