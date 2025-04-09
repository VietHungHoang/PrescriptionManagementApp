package com.mad.prescriptionmanagementapp.data.model;

public class User {
    private String googleAccountId;
    private String email;
    private String name;
    private String photoUrl;

    public User(String googleAccountId, String email, String name, String photoUrl) {
        this.googleAccountId = googleAccountId;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getGoogleAccountId() {
        return googleAccountId;
    }

    public void setGoogleAccountId(String googleAccountId) {
        this.googleAccountId = googleAccountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}


