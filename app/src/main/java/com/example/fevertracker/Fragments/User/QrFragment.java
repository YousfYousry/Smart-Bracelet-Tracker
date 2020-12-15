package com.example.fevertracker.Fragments.User;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.OldClasses.LocationActivity;
import com.example.fevertracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QrFragment extends Fragment {
    int color;
    String Id;
    ImageView imageView;
    LocationActivity locationActivity;
    ZBarScannerView mScannerView;

    public void setLocationActivity(LocationActivity locationActivity) {
        this.locationActivity = locationActivity;
    }

    public void setId(String id) {
        Id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(locationActivity!=null&&getView()!=null) {
            locationActivity.QrCreate(getView());
        }
        imageView = getView().findViewById(R.id.imageView);
        color = Color.GREEN;

        if(Id!=null) {
            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("state").getValue() != null) {
                        if(dataSnapshot.child("state").getValue().toString().compareTo("1")==0){
                            color = Color.GREEN;
                            generateQr();
                        }else if(dataSnapshot.child("state").getValue().toString().compareTo("2")==0){
                            color = Color.parseColor("#FFCE00");
                            generateQr();
                        }else if(dataSnapshot.child("state").getValue().toString().compareTo("3")==0){
                            color = Color.RED;
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
    public void generateQr(){
        Bitmap bitmap;
        QRGEncoder qrgEncoder = new QRGEncoder(Id, null, QRGContents.Type.TEXT, 1000);
        qrgEncoder.setColorBlack(color);
        qrgEncoder.setColorWhite(Color.parseColor("#E3E3E9"));
        bitmap = qrgEncoder.getBitmap();
        imageView.setImageBitmap(bitmap);
    }
}
