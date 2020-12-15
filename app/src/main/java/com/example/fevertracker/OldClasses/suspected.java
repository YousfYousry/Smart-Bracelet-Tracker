package com.example.fevertracker.OldClasses;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.fevertracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class suspected extends AppCompatDialogFragment {
    ImageView imageView;
    String passWord = "";
    Window window;

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.qr, null);

        imageView = view.findViewById(R.id.qr);
        if (!passWord.isEmpty()) {
            generateQR();
        }
        window = getActivity().getWindow();

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Suspected");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    passWord = dataSnapshot.getValue().toString();
                    generateQR();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Dialog d = new Dialog(getActivity());
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(view);

        return d;
    }
    public void generateQR() {
        if (window != null) {
            Rect displayRectangle = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            Bitmap bitmap;
            QRGEncoder qrgEncoder = new QRGEncoder(passWord, null, QRGContents.Type.TEXT, (int) (displayRectangle.width() * 0.9f));
            qrgEncoder.setColorBlack(Color.parseColor("#B98B00"));
            qrgEncoder.setColorWhite(Color.parseColor("#E3E3E9"));
            bitmap = qrgEncoder.getBitmap();
            imageView.setImageBitmap(bitmap);
        }
    }
}
