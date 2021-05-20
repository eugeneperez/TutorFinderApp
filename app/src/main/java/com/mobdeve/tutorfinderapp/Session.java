package com.mobdeve.tutorfinderapp;

public class Session {
    private String partner;
    private String status;

    public Session(String partner, String tutee, String status) {
        this.partner = tutee;
        this.status = status;
    }

    public String getTutee() {
        return partner;
    }

    public void setTutee(String tutee) {
        this.partner = tutee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
