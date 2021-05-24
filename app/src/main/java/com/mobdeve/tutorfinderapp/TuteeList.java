package com.mobdeve.tutorfinderapp;

public class TuteeList {
    private String email;
    private String name;
    private String contact;
    private String image_uri;
    private String status;

    public TuteeList(String email, String name, String contact, String image_uri, String status) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.image_uri = image_uri;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
