package com.example.fevertracker.OldClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.fevertracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment{

    TextView name, email, phone, adress, passport;
    String Id="";

    public void setId(String id) {
        Id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView()!=null) {
            name = getView().findViewById(R.id.Name);
            email = getView().findViewById(R.id.Email);
            phone = getView().findViewById(R.id.phone);
            adress = getView().findViewById(R.id.address);
            passport = getView().findViewById(R.id.passport);

            FirebaseDatabase.getInstance().getReference().child("Member").child(Id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("name").exists() && dataSnapshot.child("email").exists() && dataSnapshot.child("phone").exists() && dataSnapshot.child("address").exists() && dataSnapshot.child("passport").exists() && dataSnapshot.child("state").exists()) {
                        name.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        email.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                        phone.setText(Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString());
                        adress.setText(Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString());
                        passport.setText(Objects.requireNonNull(dataSnapshot.child("passport").getValue()).toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
