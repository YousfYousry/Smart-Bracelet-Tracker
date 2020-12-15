package com.example.fevertracker.OldClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.Activities.User.UserDashboard;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class qrScannerForUser extends Fragment {
    UserDashboard userDashboard;
    ZBarScannerView mScannerView;

    public qrScannerForUser(UserDashboard userDashboard) {
        this.userDashboard = userDashboard;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());    // Programmatically initialize the scanner view
        userDashboard.qrCam(mScannerView);
        return mScannerView;
    }
}
