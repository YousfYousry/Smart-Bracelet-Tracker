package com.example.fevertracker.Classes;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.storage.FirebaseStorage;

public class userSearch {
    private String name, passport, id, mImageUrl = "";
    private int status;

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
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

    public userSearch(String name, String passport, String id, int status) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        if (passport.trim().equals("")) {
            passport = "No Passport";
        }
        this.name = name;
        this.passport = passport;
        this.id = id;
        this.status = status;
    }

    public userSearch(String name, String passport, String id, String mImageUrl, int status) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        if (passport.trim().equals("")) {
            passport = "No Passport";
        }
        this.name = name;
        this.passport = passport;
        this.id = id;
        this.status = status;
        this.mImageUrl = mImageUrl;
    }
}
