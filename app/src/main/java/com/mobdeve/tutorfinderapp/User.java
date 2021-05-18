package com.mobdeve.tutorfinderapp;

import java.util.ArrayList;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private String contact;
    private ArrayList<String> categories;
    private float fee;

    public User(String email, String firstname, String lastname, String contact, ArrayList<String> categories, float fee) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        this.categories= categories;
        this.fee=fee;
    }


    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(float fee) {
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
