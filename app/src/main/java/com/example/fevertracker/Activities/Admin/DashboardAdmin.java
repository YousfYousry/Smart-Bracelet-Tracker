package com.example.fevertracker.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fevertracker.Dialogs.QrDialogForAdmin;
import com.example.fevertracker.OldClasses.LocationActivity;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.MainActivity_RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import maes.tech.intentanim.CustomIntent;

public class DashboardAdmin extends AppCompatActivity {
    File localFile = null;
    ImageView selectedImage;
    TextView name, id;
    String NotInfectedPassword = "", SuspectedPassword = "", InfectedPassword = "";
    boolean doubleBackToExitPressedOnce = false;
    public static final String SHARED_PREFS = "sharedPrefs";

    public void realTimeMap(View view) {
        startActivity(new Intent(getApplicationContext(), RealTimeTracker.class));
        CustomIntent.customType(this, "left-to-right");
    }

    public void searchByPlaceMap(View view) {
        startActivity(new Intent(getApplicationContext(), searchByPlace.class));
        CustomIntent.customType(this, "left-to-right");
        finish();
    }

    public void findUsers(View view) {
        Intent intent = new Intent(getApplicationContext(), findUserAdmin.class);
        startActivityForResult(intent, 2);
        CustomIntent.customType(this, "left-to-right");
    }

    public void announcement(View view) {
        startActivity(new Intent(getApplicationContext(), announcement.class));
        CustomIntent.customType(this, "left-to-right");
    }

    public void qrtoscan(View view) {
        QrData();

        QrDialogForAdmin cdd=new QrDialogForAdmin(DashboardAdmin.this);
        cdd.show();

//        notInfected exampleDialog = new notInfected();
//        exampleDialog.setPassWord(NotInfectedPassword);
//        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void logout(View view) {
        saveData("", "log");
        startActivity(new Intent(getApplicationContext(), MainActivity_RegisterActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);
        selectedImage = findViewById(R.id.profilePicture);
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        saveData("in", "log");
//        if (!loadData("Id").isEmpty()) {
//            setUserInfo();
//        }
    }

    public void QrData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Infected").getValue() != null) {
                    InfectedPassword = dataSnapshot.child("Infected").getValue().toString();
                }
                if (dataSnapshot.child("Not infected").getValue() != null) {
                    NotInfectedPassword = dataSnapshot.child("Not infected").getValue().toString();
                }
                if (dataSnapshot.child("Suspected").getValue() != null) {
                    SuspectedPassword = dataSnapshot.child("Suspected").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public File getLocalFile() {
        return localFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
//            setUserInfo();
        }
    }

    public void setUserInfo() {
        id.setText(loadData("Id"));
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue() != null) {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if (loadData("pic").isEmpty()) {
            selectedImage.setImageResource(R.drawable.avatar);
        } else {
            selectedImage.setImageURI(Uri.parse(loadData("pic")));
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

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(LocationActivity.SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
