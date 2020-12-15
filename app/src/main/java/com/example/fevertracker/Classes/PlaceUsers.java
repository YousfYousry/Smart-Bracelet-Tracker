package com.example.fevertracker.Classes;

import android.net.Uri;

public class PlaceUsers {
    private String UserName;
    private String UserID;
    private String UserTime;
    private Uri pic;

    public Uri getPic() {
        return pic;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserTime() {
        return UserTime;
    }

    public PlaceUsers(Uri pic, String UserName, String UserID,String UserTime) {
        this.UserName = UserName;
        this.UserID = UserID;
        this.UserTime = UserTime;
        this.pic = pic;
    }
}
