package com.example.fevertracker.Dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.example.fevertracker.Activities.Admin.DashboardAdmin;
import com.example.fevertracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrDialogForAdmin extends Dialog implements
        android.view.View.OnClickListener {

    public DashboardAdmin c;
    public Dialog d;
    public ImageView image;
    public Button btn_Infected, btn_Suspicious, btn_Normal;
    public FrameLayout fram1, fram2, fram3;
    public int choice = 1;

    public QrDialogForAdmin(DashboardAdmin a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qr_dialog);
        btn_Infected = (Button) findViewById(R.id.btn_Infected);
        btn_Suspicious = (Button) findViewById(R.id.btn_Suspicious);
        btn_Normal = (Button) findViewById(R.id.btn_Normal);
        fram1 = findViewById(R.id.fram1);
        fram2 = findViewById(R.id.fram2);
        fram3 = findViewById(R.id.fram3);
        image = findViewById(R.id.image);
        btn_Infected.setOnClickListener(this);
        btn_Suspicious.setOnClickListener(this);
        btn_Normal.setOnClickListener(this);

        choice = 1;
        QrData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Infected:
                fram1.setVisibility(View.VISIBLE);
                fram2.setVisibility(View.INVISIBLE);
                fram3.setVisibility(View.INVISIBLE);
                btn_Infected.setBackgroundResource(R.drawable.qr_button_2);
                btn_Suspicious.setBackgroundResource(R.drawable.qr_button);
                btn_Normal.setBackgroundResource(R.drawable.qr_button);
                image.setBackgroundResource(R.drawable.qr_button_top_left);
                choice = 0;
                QrData();
                break;

            case R.id.btn_Suspicious:
                fram1.setVisibility(View.INVISIBLE);
                fram2.setVisibility(View.VISIBLE);
                fram3.setVisibility(View.INVISIBLE);
                btn_Infected.setBackgroundResource(R.drawable.qr_button);
                btn_Suspicious.setBackgroundResource(R.drawable.qr_button_2);
                btn_Normal.setBackgroundResource(R.drawable.qr_button);
                image.setBackgroundResource(R.drawable.qr_button);
                choice = 1;
                QrData();
                break;

            case R.id.btn_Normal:
                fram1.setVisibility(View.INVISIBLE);
                fram2.setVisibility(View.INVISIBLE);
                fram3.setVisibility(View.VISIBLE);
                btn_Infected.setBackgroundResource(R.drawable.qr_button);
                btn_Suspicious.setBackgroundResource(R.drawable.qr_button);
                btn_Normal.setBackgroundResource(R.drawable.qr_button_2);
                image.setBackgroundResource(R.drawable.qr_button_top_right);
                choice = 2;
                QrData();
                break;
            default:
                break;
        }
    }

    public void QrData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Infected").getValue() != null && choice == 0) {
                    generateQR(dataSnapshot.child("Infected").getValue().toString(), "#ffcc0000");
                }
                if (dataSnapshot.child("Not infected").getValue() != null && choice == 2) {
                    generateQR(dataSnapshot.child("Not infected").getValue().toString(), "#ff669900");
                }
                if (dataSnapshot.child("Suspected").getValue() != null && choice == 1) {
                    generateQR(dataSnapshot.child("Suspected").getValue().toString(), "#A67F0B");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void generateQR(String passWord, String color) {
        Bitmap bitmap;
        QRGEncoder qrgEncoder = new QRGEncoder(passWord, null, QRGContents.Type.TEXT, (int) c.dpToPx(310));
        qrgEncoder.setColorBlack(Color.parseColor(color));
        qrgEncoder.setColorWhite(Color.parseColor("#FFFFFF"));
        bitmap = qrgEncoder.getBitmap();
        image.setImageBitmap(bitmap);
    }
}

