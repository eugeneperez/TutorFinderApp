package com.mobdeve.tutorfinderapp;

import java.util.ArrayList;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private String contact;
    private ArrayList<String> categories;
    private String fee;
    private String profpic;
    private float aveRating;
    private int totalTutees;
    private ArrayList<TutorList> tutorList = new ArrayList<>();
    private ArrayList<TuteeList> tuteeList = new ArrayList<>();

    public User(String email, String firstname, String lastname, String contact) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
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

    public float getAveRating() {
        return aveRating;
    }

    public void setAveRating(float aveRating) {
        this.aveRating = aveRating;
    }

    public int getTotalTutees() {
        return totalTutees;
    }

    public void setTotalTutees(int totalTutees) {
        this.totalTutees = totalTutees;
    }

    public ArrayList<TutorList> getTutorList() {
        return tutorList;
    }

    public void setTutorList(ArrayList<TutorList> tutorList) {
        this.tutorList = tutorList;
    }

    public ArrayList<TuteeList> getTuteeList() {
        return tuteeList;
    }

    public void setTuteeList(ArrayList<TuteeList> tuteeList) {
        this.tuteeList = tuteeList;
    }
}
