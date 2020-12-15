package com.example.fevertracker.Classes;

import android.text.Spanned;

public class Announce {
    Spanned announce;
    long id;

    public long getId() {
        return id;
    }

    public Spanned getAnnounce() {
        return announce;
    }

    public Announce(Spanned announce, Long id) {
        this.announce = announce;
        this.id = id;
    }
}
