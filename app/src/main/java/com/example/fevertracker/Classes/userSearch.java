package com.example.fevertracker.Classes;

import android.net.Uri;

public class userSearch {
    private String name, passport, id;
    private int status;
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getPassport() {
        return passport;
    }

    public String getId() {
        return id;
    }

    public userSearch(String name, String passport, String id,int status) {
        this.name = name;
        this.passport = passport;
        this.id = id;
        this.status = status;
    }
}
