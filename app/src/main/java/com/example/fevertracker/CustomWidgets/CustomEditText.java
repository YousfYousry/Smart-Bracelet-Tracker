package com.example.fevertracker.CustomWidgets;

import android.content.Context;
import android.util.AttributeSet;

import com.example.fevertracker.Activities.Admin.announcement;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    announcement anouncement;

    public void setAnouncement(announcement anouncement) {
        this.anouncement = anouncement;
    }

    public CustomEditText(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);

    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomEditText(Context context) {
        super(context);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (anouncement != null) {
            anouncement.setButtons(selStart, selEnd);
        }
    }

}
