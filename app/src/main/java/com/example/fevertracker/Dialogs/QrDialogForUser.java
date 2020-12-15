package com.example.fevertracker.Dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.fevertracker.R;
import com.example.fevertracker.Activities.User.UserDashboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrDialogForUser extends Dialog implements
        View.OnClickListener {

    public UserDashboard c;
    public String Id;
    public Dialog d;
    public ImageView image;
    public Button qr_button;
    int colour = Color.parseColor("#ffcc0000");

    public QrDialogForUser(UserDashboard a, String Id) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.Id = Id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qr_fragment);
        qr_button = (Button) findViewById(R.id.QrScanner);
        image = findViewById(R.id.imageView);
        qr_button.setOnClickListener(this);
        getStatus();
    }

    @Override
    public void onClick(View v) {
        c.qrPressed(null);
    }

    public void getStatus() {
        if (Id != null && !Id.isEmpty()) {
            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("state").getValue() != null) {
                        if (dataSnapshot.child("state").getValue().toString().compareTo("1") == 0) {
                            colour = Color.parseColor("#ff669900");
                            generateQr();
                        } else if (dataSnapshot.child("state").getValue().toString().compareTo("2") == 0) {
                            colour = Color.parseColor("#A67F0B");
                            generateQr();
                        } else if (dataSnapshot.child("state").getValue().toString().compareTo("3") == 0) {
                            colour = Color.parseColor("#ffcc0000");
                            generateQr();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void generateQr() {
        Bitmap bitmap;
        QRGEncoder qrgEncoder = new QRGEncoder(Id, null, QRGContents.Type.TEXT, 1000);
        qrgEncoder.setColorBlack(colour);
        qrgEncoder.setColorWhite(Color.parseColor("#FFFFFF"));
        bitmap = qrgEncoder.getBitmap();
        image.setImageBitmap(bitmap);
    }
}

