package com.example.fevertracker.OldClasses;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.OldClasses.admin;
import com.example.fevertracker.R;

public class QrForAdmin extends Fragment {

    admin admin;
    public void setAdmin(com.example.fevertracker.OldClasses.admin admin) {
        this.admin = admin;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_for_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(admin!=null&&getView()!=null) {
            admin.QRonCreate(getView());
        }
    }
}
