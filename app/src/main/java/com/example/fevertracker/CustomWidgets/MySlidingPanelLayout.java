package com.example.fevertracker.CustomWidgets;

import android.content.Context;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MySlidingPanelLayout extends SlidingUpPanelLayout {
    public MySlidingPanelLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}