package com.example.fevertracker.OldClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fevertracker.OldClasses.LocationActivity;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class qrScanner extends Fragment {
    LocationActivity locationActivity;
    ZBarScannerView mScannerView;

    public void setLocationActivity(LocationActivity locationActivity) {
        this.locationActivity = locationActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());    // Programmatically initialize the scanner view
        locationActivity.qrCam(mScannerView);
        return mScannerView;
    }
}
