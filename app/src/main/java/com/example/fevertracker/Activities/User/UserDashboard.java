package com.example.fevertracker.Activities.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import com.example.fevertracker.Activities.LocationHistory;
import com.example.fevertracker.Activities.MainActivity_RegisterActivity;
import com.example.fevertracker.Dialogs.QrDialogForUser;
import com.example.fevertracker.OldClasses.qrScannerForUser;
import com.example.fevertracker.R;
import com.example.fevertracker.Services.LocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.example.fevertracker.OldClasses.LocationActivity.SHARED_PREFS;

public class UserDashboard extends AppCompatActivity implements ZBarScannerView.ResultHandler, AppBarLayout.OnOffsetChangedListener {

    //views
    ImageView profilePicture;
    TextView id, Name;
    QrDialogForUser cdd;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FrameLayout State1Back, State2Back, profileBack;
    LinearLayout LinearText;

    //vars
    String notInData1 = "", susData2 = "", inData3 = "", QrRes = "";
    Context context = this;
    File localFile = null;
    Intent serviceIntent;
    ZBarScannerView mScannerView;
    CoordinatorLayout.LayoutParams profileBackParams, State1BackParams, State2BackParams;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Loc loc = new Loc();
    long INTERVAL = 500000, FASTEST_INTERVAL = 500000;
    int RequestCameraPermissionID = 1001, status = 1, x = 0, y = 0;
    float oldPerc = 0;
    final Activity activity = this;
    public static String Id = "";
    static boolean doubleBackToExitPressedOnce = false;
    boolean QRdialog = false, QrActive = false, pinned = false;

    public void empty(View view) {
    }

    public void addPic(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(UserDashboard.this);
        Toast.makeText(context, "Error while downloading profile picture", Toast.LENGTH_SHORT).show();
    }

    public void realTimeMap(View view) {
        startActivity(new Intent(getApplicationContext(), RealTimeTrackerForUser.class));
        CustomIntent.customType(this, "left-to-right");
    }

    public void Geofence(View view) {
    }

    public void announcement(View view) {
        startActivity(new Intent(getApplicationContext(), announcementForUser.class));
        CustomIntent.customType(this, "left-to-right");
    }

    public void qrtoscan(View view) {
        cdd = new QrDialogForUser(UserDashboard.this, loadData("Id"));
        cdd.show();
    }

    public void logout(View view) {
        if (isMyServiceRunning()) {
            stopService(serviceIntent);
        }
        if (!loadData("Id").isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("IsOnline").setValue(false);
        }
        saveData("", "Id");
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), MainActivity_RegisterActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void LocationHistory(View view) {
        startActivity(new Intent(getApplicationContext(), LocationHistory.class));
        CustomIntent.customType(this, "left-to-right");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        id = findViewById(R.id.id);
        Name = findViewById(R.id.Name);
        Id = loadData("Id");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        State1Back = findViewById(R.id.State1Back);
        State2Back = findViewById(R.id.State2Back);
        profileBack = findViewById(R.id.profileBack);
        LinearText = findViewById(R.id.LinearText);

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        collapsingToolbarLayout = findViewById(R.id.colaps);
        collapsingToolbarLayout.setTitle(" ");
//        createLocationRequest();

        dynamicToolbarColor();
//        toolbarTextAppernce();

        if (!Id.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(Id).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            if(dataSnapshot.child("name").exists()) {
                                Name.setText("Name :   " + Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                            }else{
                                Name.setText("Name :   ");
                            }
                            id.setText("ID :   " + Id);
                            status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("state").getValue()).toString());
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        serviceIntent = new Intent(this, LocationService.class);//*************************************************************************
        if (!isMyServiceRunning()) {
            ContextCompat.startForegroundService(this, serviceIntent);
        }

        profilePicture = findViewById(R.id.profilePicture);
        setProfilePicture();

//        State1Back.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int[] location = new int[2];
//                State1Back.getLocationInWindow(location);
//                x = location[0];
//                y = location[1];
//                animate(0);
//            }
//        }, 1000);

        profileBackParams = (CoordinatorLayout.LayoutParams) profileBack.getLayoutParams();

        State1BackParams = (CoordinatorLayout.LayoutParams) State1Back.getLayoutParams();

        State2BackParams = (CoordinatorLayout.LayoutParams) State2Back.getLayoutParams();
        profileBack.setLayoutParams(State1BackParams);
        pinned = true;

        State1Back.post(() -> {
            int[] location = new int[2];
            State1Back.getLocationOnScreen(location);
            x = location[0];
            y = location[1] - dpToPx(50);
//                animate(0);
            profileBack.setLayoutParams(State1BackParams);
            pinned = true;
        });
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        if (percentage != oldPerc) {
            float startAnim = 0.8f;
            if (percentage >= startAnim) {
                if (pinned) {
                    profileBack.setLayoutParams(State2BackParams);
                    pinned = false;
                }
                animate((percentage - startAnim) / (1f - startAnim));
            } else {
//                FrameLayout.LayoutParams state1Params = (FrameLayout.LayoutParams) state1.getLayoutParams();
//                CoordinatorLayout.LayoutParams State1BackParams = (CoordinatorLayout.LayoutParams) State1Back.getLayoutParams();
//                profileFrame.setLayoutParams(state1Params);
//                profileBack.setLayoutParams(State1BackParams);
                int[] location = new int[2];
                State1Back.getLocationInWindow(location);
                x = location[0];
                y = location[1] - dpToPx(50);
//                animate(0);
                LinearText.setAlpha(0);
                if (!pinned) {
                    profileBack.setLayoutParams(State1BackParams);
                    pinned = true;
                }
            }
        }
    }

    public void animate(float percentage) {
        LinearText.setAlpha(percentage);

        profileBackParams.width = (int) ((State2BackParams.width - dpToPx(10)) + (1.0 - percentage) * (((float) (State1BackParams.width + dpToPx(10))) - ((float) State2BackParams.width)));
        profileBackParams.height = (int) ((State2BackParams.height - dpToPx(10)) + (1.0 - percentage) * (((float) (State1BackParams.height + dpToPx(10))) - ((float) State2BackParams.height)));
        profileBackParams.leftMargin = (int) ((x) * (1.0 - percentage) + dpToPx(10) * (percentage));
        profileBackParams.topMargin = (int) ((y) * (1.0 - percentage) + dpToPx(5) * (percentage));

        profileBack.setLayoutParams(profileBackParams);

        oldPerc = percentage;
    }

    private void dynamicToolbarColor() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.comdelta);
        Palette.from(bitmap).generate(palette -> {
            int colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
            collapsingToolbarLayout.setContentScrimColor(colorPrimary);
            collapsingToolbarLayout.setStatusBarScrimColor(colorPrimary);
        });
    }

    private void setProfilePicture() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));

        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> profilePicture.setImageURI(Uri.fromFile(localFile))).addOnFailureListener(exception -> Toast.makeText(this, "You don't have a profile picture!", LENGTH_LONG).show());
    }

    public void qrPressed(View view) {
        if (view == null) {
            QRdialog = true;
        }
        if (IsNetworkEnabled()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startQrCam();
                } else {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID
                    );
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please turn GPS on first.", LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Check internet connection", LENGTH_LONG).show();
        }
    }

    public boolean IsNetworkEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).
                getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).
                        getState() == NetworkInfo.State.CONNECTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void uploadFile(Uri mImageUri) {
        if (mImageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
//                        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("ProfileImage").setValue();
                        Toast.makeText(context, "Profile picture has been uploaded successfully", LENGTH_LONG).show();
                    }).addOnFailureListener(exception -> Toast.makeText(context, "Error while uploading profile picture", LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                profilePicture.setImageURI(result.getUri());
                uploadFile(result.getUri());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCameraPermissionID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startQrCam();
            }
        }
    }

    public void qrCam(ZBarScannerView LocalmScannerView) {
        mScannerView = LocalmScannerView;
        LocalmScannerView.setResultHandler(this);
        LocalmScannerView.startCamera();
    }

    public void startQrCam() {
        getQrCodes();
        QrActive = true;
        try {
            cdd.dismiss();
        } catch (Exception ignored) {

        }
        QrRes = "";
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        qrScannerForUser qrScanner = new qrScannerForUser(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                qrScanner).commit();
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        QrRes = result.getContents();
        loc.create();
    }

    public void gotResult(Location location) {
        if (!QrRes.isEmpty() && QrRes.compareTo(notInData1) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Location").setValue(location.getLatitude() + "," + location.getLongitude());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Altitude&accuracy").setValue(location.getAltitude() + "," + location.getAccuracy());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Status").setValue(status + ",1");
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("1");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Not infected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        } else if (!QrRes.isEmpty() && QrRes.compareTo(susData2) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Location").setValue(location.getLatitude() + "," + location.getLongitude());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Altitude&accuracy").setValue(location.getAltitude() + "," + location.getAccuracy());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Status").setValue(status + ",2");
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("2");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Suspected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        } else if (!QrRes.isEmpty() && QrRes.compareTo(inData3) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Location").setValue(location.getLatitude() + "," + location.getLongitude());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Altitude&accuracy").setValue(location.getAltitude() + "," + location.getAccuracy());
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("QR scanner location").child(Long.toString((Long) (System.currentTimeMillis() / 1000))).child("Status").setValue(status + ",3");
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("3");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Infected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
            findViewById(R.id.fragment_container).setVisibility(View.GONE);

        } else {
            qrScannerForUser qrScanner = new qrScannerForUser(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    qrScanner).commit();
            Toast.makeText(this, "The QR was invalid.", LENGTH_LONG).show();
        }
    }

    public void qrBackPressed() {
        QrActive = false;
        mScannerView.stopCamera();
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        if (QRdialog) {
            cdd = new QrDialogForUser(UserDashboard.this, loadData("Id"));
            cdd.show();
            QRdialog = false;
        }
    }

    public void getQrCodes() {
        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Infected").exists()) {
                    inData3 = Objects.requireNonNull(dataSnapshot.child("Infected").getValue()).toString();
                }
                if (dataSnapshot.child("Suspected").exists()) {
                    susData2 = Objects.requireNonNull(dataSnapshot.child("Suspected").getValue()).toString();
                }
                if (dataSnapshot.child("Not infected").exists()) {
                    notInData1 = Objects.requireNonNull(dataSnapshot.child("Not infected").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
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

//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(loc)
//                .build();
//    }

    public class Loc implements LocationListener, GoogleApiClient.ConnectionCallbacks {
        public void create() {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
            startLocationUpdates();
        }

        @SuppressLint("MissingPermission")
        public void startLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, Looper.getMainLooper());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onLocationChanged(Location location) {
            mGoogleApiClient.disconnect();
            gotResult(location);
        }
    }

    @Override
    public void onBackPressed() {
        if (QrActive) {
            qrBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}
