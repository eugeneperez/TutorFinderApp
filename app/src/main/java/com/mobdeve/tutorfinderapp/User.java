package com.mobdeve.tutorfinderapp;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private String contact;


    public User(String email, String firstname, String lastname, String contact) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
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
