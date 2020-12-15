package com.example.fevertracker.Fragments.Admin;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AnnouncementFragmentAdmin extends Fragment {

    TextView announcement, cancel;
    EditText announcementE;
    FloatingActionButton edit;
    ScrollView scrollView, scrollViewE;
    SpannableStringBuilder spannableStringBuilder;
    boolean editMode = false;
    StyleCallback styleCallback = new StyleCallback();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.announcement_fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edit = getView().findViewById(R.id.edit);

        announcement = getView().findViewById(R.id.announceText);
        announcementE = getView().findViewById(R.id.announceTextE);
        styleCallback.setBodyView(announcementE);
        announcementE.setCustomSelectionActionModeCallback(styleCallback);

        scrollView = getView().findViewById(R.id.showMode);
        scrollViewE = getView().findViewById(R.id.editMode);
        cancel = getView().findViewById(R.id.textView);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit(v);
            }
        });
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("announcement").getValue() != null) {
                    announcement.setText(Html.fromHtml(dataSnapshot.child("announcement").getValue().toString()));
                    announcementE.setText(Html.fromHtml(dataSnapshot.child("announcement").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void cancel(View view) {
        editMode = false;
        scrollView.setVisibility(View.VISIBLE);
        scrollViewE.setVisibility(View.GONE);
//        edit.setText("edit");
    }

    public void Edit(View view) {
        if (!editMode) {
            editMode = true;
            scrollView.setVisibility(View.GONE);
            scrollViewE.setVisibility(View.VISIBLE);
//            edit.setText("save");
        } else {
            DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("adminInfo");
            reff2.child("announcement").setValue(Html.toHtml(announcementE.getText()));

            editMode = false;
            scrollView.setVisibility(View.VISIBLE);
            scrollViewE.setVisibility(View.GONE);
//            edit.setText("edit");
        }
    }
}
