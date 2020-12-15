package com.example.fevertracker.Fragments.Admin;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.Admin.announcement;

public class StyleCallback implements ActionMode.Callback {
    EditText bodyView;
    announcement anouncement;
    MenuInflater inflater;
    boolean styleOpened = false;
    ActionMode Mode;

    public void setStyleOpened(boolean styleOpened) {
        this.styleOpened = styleOpened;
    }

    public void setAnouncement(announcement anouncement) {
        this.anouncement = anouncement;
    }

    public void setBodyView(EditText bodyView) {
        this.bodyView = bodyView;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (!styleOpened) {
            inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.style, menu);
            menu.removeItem(android.R.id.selectAll);
            Mode = mode;
        }
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Mode = mode;
        return false;
    }

    public void closeAction() {
        int start = bodyView.getSelectionStart(), end = bodyView.getSelectionEnd();
        if (Mode != null) {
            Mode.finish();
        }
        bodyView.setSelection(start, end);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Mode = mode;
        switch (item.getItemId()) {

            case R.id.Format:
                anouncement.showView();
                closeAction();
                return true;
        }
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
        Mode = mode;
    }
}