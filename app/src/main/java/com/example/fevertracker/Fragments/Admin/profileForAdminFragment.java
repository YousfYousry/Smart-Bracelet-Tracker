package com.example.fevertracker.Fragments.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.Activities.Admin.findUserAdmin;
import com.example.fevertracker.Activities.LocationHistory;
import com.example.fevertracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class profileForAdminFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    TextView name, email, phone, adress, passport, status, profileName, profileStatus;
    ImageButton nameB, passportB, emailB, addressB, phoneB, statusB;
    String Id = "";
    boolean init = false;
    LinearLayout PreviewMode;
    Context context;
    int statuss = 1;
    boolean popUp = true;
    Toast userFound, userNotFound, EmptyText;
    ImageView selectedImage;
    Uri localFile = null;
    com.example.fevertracker.Activities.Admin.findUserAdmin findUserAdmin = new findUserAdmin();
    BottomSheetDialog bottomSheet;
    profileForAdminFragment profileForAdminFragment = this;

    public void setLocalFile(Uri localFile) {
        this.localFile = (Uri) localFile;
    }

    public void setfindUserAdmin(findUserAdmin findUserAdmin) {
        this.findUserAdmin = findUserAdmin;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setId(String id) {
        Id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment_for_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Id != null && !Id.isEmpty() && getView() != null) {
            UserFound();
        } else {
            userFound = Toast.makeText(context, "User's data is downloaded.", Toast.LENGTH_SHORT);
            userNotFound = Toast.makeText(context, "User was not found!", Toast.LENGTH_SHORT);
            EmptyText = Toast.makeText(context, "Please write Id first.", Toast.LENGTH_SHORT);

            name = Objects.requireNonNull(getView()).findViewById(R.id.Name);
            email = getView().findViewById(R.id.Email);
            phone = getView().findViewById(R.id.phone);
            adress = getView().findViewById(R.id.address);
            passport = getView().findViewById(R.id.passport);
            profileName = getView().findViewById(R.id.userName);
            profileStatus = getView().findViewById(R.id.userStatus);
            profileName.setText("User name");
            profileStatus.setText("User status");
            name.setText("Select user first");
            email.setText("Select user first");
            phone.setText("Select user first");
            adress.setText("Select user first");
            passport.setText("Select user first");
        }
    }

    public void UserFound() {
        init = true;
        userFound = Toast.makeText(context, "User's data is downloaded.", Toast.LENGTH_SHORT);
        userNotFound = Toast.makeText(context, "User was not found!", Toast.LENGTH_SHORT);
        EmptyText = Toast.makeText(context, "Please write Id first.", Toast.LENGTH_SHORT);
        selectedImage = Objects.requireNonNull(getView()).findViewById(R.id.profilePictureAdmin);
        if (localFile != null) {
            selectedImage.setImageURI(localFile);
        }
        profileName = getView().findViewById(R.id.userName);
        profileStatus = getView().findViewById(R.id.userStatus);
        status = getView().findViewById(R.id.Status);
        name = getView().findViewById(R.id.Name);
        email = getView().findViewById(R.id.Email);
        phone = getView().findViewById(R.id.phone);
        adress = getView().findViewById(R.id.address);
        passport = getView().findViewById(R.id.passport);
        PreviewMode = getView().findViewById(R.id.PreviewMode);
        nameB = getView().findViewById(R.id.nameB);
        nameB.setOnClickListener(v -> createBottomSheet(name.getText().toString(), "Enter user's name", "name"));
        passportB = getView().findViewById(R.id.passportB);
        passportB.setOnClickListener(v -> createBottomSheet(passport.getText().toString(), "Enter user's passport", "passport"));
        emailB = getView().findViewById(R.id.emailB);
        emailB.setOnClickListener(v -> createBottomSheet(email.getText().toString(), "Enter user's email", "email"));
        addressB = getView().findViewById(R.id.addressB);
        addressB.setOnClickListener(v -> createBottomSheet(adress.getText().toString(), "Enter user's address", "address"));
        phoneB = getView().findViewById(R.id.phoneB);
        phoneB.setOnClickListener(v -> createBottomSheet(phone.getText().toString(), "Enter user's phone", "phone"));
        statusB = getView().findViewById(R.id.statusB);
        statusB.setOnClickListener(v -> showPopUp(status));
        updateInfo();
    }

    public void cancel() {
        bottomSheet.dismiss();
    }

    public void updateInfo() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue() != null && dataSnapshot.child("email").getValue() != null && dataSnapshot.child("phone").getValue() != null && dataSnapshot.child("address").getValue() != null && dataSnapshot.child("passport").getValue() != null && dataSnapshot.child("state").getValue() != null) {
                    profileName.setText(dataSnapshot.child("name").getValue().toString());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    email.setText(dataSnapshot.child("email").getValue().toString());
                    phone.setText(dataSnapshot.child("phone").getValue().toString());
                    adress.setText(dataSnapshot.child("address").getValue().toString());
                    passport.setText(dataSnapshot.child("passport").getValue().toString());
                    statuss = Integer.parseInt(dataSnapshot.child("state").getValue().toString());
                    setButtonStatus(statuss);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        findUserAdmin.LoadPic(selectedImage);
    }

    public void createBottomSheet(String content, String title, String val) {
        bottomSheet = new BottomSheetDialog();
        bottomSheet.setProfileForAdminFragment(profileForAdminFragment);
        bottomSheet.setId(Id);
        bottomSheet.setValString(content);
        bottomSheet.setTitle(title);
        bottomSheet.setDataName(val);
        if (getFragmentManager() != null) {
            bottomSheet.show(getFragmentManager(), "exampleBottomSheet");
        }
    }

    public void showPopUp(View view) {
        popUp = true;
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_pop_up);
        popup.show();
    }

    public void setButtonStatus(int statuss) {
        if (statuss == 1) {
            profileStatus.setText("Healthy");
            profileStatus.setTextColor(Color.GREEN);
            status.setText("Healthy");
        } else if (statuss == 2) {
            profileStatus.setText("Suspicious case");
            profileStatus.setTextColor(Color.parseColor("#FFCE00"));
            status.setText("Suspicious case");
        } else if (statuss == 3) {
            profileStatus.setText("Infected with corona");
            profileStatus.setTextColor(Color.RED);
            status.setText("Infected with corona");
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (popUp) {
            switch (item.getItemId()) {
                case R.id.item1:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("state").setValue("1");
                    statuss = 1;
                    status.setText("Healthy");
                    return true;
                case R.id.item2:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("state").setValue("2");
                    statuss = 2;
                    status.setText("suspicious case");
                    return true;
                case R.id.item3:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("state").setValue("3");
                    statuss = 3;
                    status.setText("Infected with corona");
                    return true;
                default:
                    return false;
            }
        }
        return true;
    }
}
