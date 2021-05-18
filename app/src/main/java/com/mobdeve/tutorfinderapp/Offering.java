package com.mobdeve.tutorfinderapp;

import java.util.ArrayList;

public class Offering {
    private String email;
    private String firstname;
    private String lastname;
    private String category;
    private String fee;
    private ArrayList<String> specializations;

    public Offering(String email, String firstname, String lastname, String category, String fee, ArrayList<String> specializations){
        this.email=email;
        this.firstname=firstname;
        this.lastname=lastname;
        this.category=category;
        this.fee=fee;
        this.specializations=specializations;
    }

    public ArrayList<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<String> specializations) {
        this.specializations = specializations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
