package com.example.fevertracker.Activities.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fevertracker.Adapters.announceAdapter;
import com.example.fevertracker.Classes.Announce;
import com.example.fevertracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class announcementForUser extends AppCompatActivity {
    ListView announcementList;
    ArrayList<Announce> announce = new ArrayList<>();
    Context context = this;
    ProgressBar progressBar;

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_for_user);

        progressBar = findViewById(R.id.progressBar);

        announcementList = findViewById(R.id.announcementList);
        FirebaseDatabase.getInstance().getReference().child("adminInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (announce != null) {
                    announce.clear();
                }
                ArrayList<Long> posts = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.child("announcement").getChildren()) {
                    if (ds.getKey() != null) {
                        if (dataSnapshot.child("announcement").child(ds.getKey()).getValue() != null) {
                            posts.add(NumericOf(ds.getKey()));
                        }
                    }
                }
                Collections.sort(posts, Collections.reverseOrder());
                for (int i = 0; i < posts.size(); i++) {
                    announce.add(new Announce(Html.fromHtml(Objects.requireNonNull(dataSnapshot.child("announcement").child(Long.toString(posts.get(i))).getValue()).toString()), posts.get(i)));
                }

                announceAdapter arrayAdapter = new announceAdapter(context, R.layout.announce_user, announce);
                announcementList.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Connection Error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public long NumericOf(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }
}
