package com.mobdeve.tutorfinderapp;

import java.util.ArrayList;

public class Offering {
    private String email;
    private String category;
    private float fee;
    private ArrayList<String> specializations;

    public Offering(String email, String category, float fee, ArrayList<String> specializations){
        this.email=email;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }
}
