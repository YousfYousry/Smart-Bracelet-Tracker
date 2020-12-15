package com.example.fevertracker.OldClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fevertracker.Activities.MainActivity_RegisterActivity;
import com.example.fevertracker.Fragments.Admin.AnnouncementFragmentAdmin;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.LocationHistory;
import com.example.fevertracker.Fragments.Admin.profileForAdminFragment;
import com.example.fevertracker.Activities.Admin.searchByPlace;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ZBarScannerView.ResultHandler {

    GoogleMap googleMap;
    boolean CameraMoved = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    private DrawerLayout drawer;
    Context context = this;
    admin admin = this;
    admin adminClass = this;
    final int RequestCameraPermissionID = 1001;
    boolean dataUpdated = false;
    String NotInfectedPassword = "", SuspectedPassword = "", InfectedPassword = "";
    TextView name;
    String data = "", data2 = "", data3 = "";
    ProgressBar progressBar;
    File localFile = null;
    ImageView selectedImage;
    GoogleApiClient mGoogleApiClient;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";

    public void addGeo(View view) {

    }

    public void gpsTracker(View view) {
        if (!loadData("Id").isEmpty()) {
            startActivity(new Intent(getApplicationContext(), LocationHistory.class));
        } else {
            Toast.makeText(this, "Please get Id first", Toast.LENGTH_LONG).show();
        }
    }

    public void searchByPlace(View view){
        startActivity(new Intent(getApplicationContext(), searchByPlace.class));
    }


    public void edit(View view) {
        if (loadData("Id").isEmpty()){
            Toast.makeText(this, "Please get Id first", Toast.LENGTH_LONG).show();
        }
    }

    public void update(View view) {
//        updateMarkers();
        System.out.println("started");
        for(int i=0;i<49995000;i++){
            distance(new LatLng(0,0),new LatLng(3,100));
        }
        System.out.println("done");

    }
    public double distance(LatLng latLng1, LatLng latLng2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.

        double lat1 = latLng1.latitude, lat2 = latLng2.latitude, lon1 = latLng1.longitude, lon2 = latLng2.longitude;


        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r * 1000);
    }

    public void addPic(View view) {
    }

    public void notI(View view) {
        notInfected exampleDialog = new notInfected();
        exampleDialog.setPassWord(NotInfectedPassword);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void Sus(View view) {
        suspected exampleDialog = new suspected();
        exampleDialog.setPassWord(SuspectedPassword);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void Infected(View view) {
        infected exampleDialog = new infected();
        exampleDialog.setPassWord(InfectedPassword);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_old_activity_admin);
        CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        saveData("in", "log");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

//        if (savedInstanceState == null) {
//            MapFragmentForAdmin mapFragmentForAdmin = new MapFragmentForAdmin();
//            mapFragmentForAdmin.setAdminActivity(adminClass);
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    mapFragmentForAdmin).commit();
//            navigationView.setCheckedItem(R.id.nav_map);
//        }

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.name);
        selectedImage = headerView.findViewById(R.id.profilePicture);
        setHeaderViewInfo();
    }

    public void setHeaderViewInfo() {
        if (!loadData("Id").isEmpty()) {
            setProfilePicture();
            name.setText("Current user Id: " + loadData("Id"));
        } else {
            name.setText("Current user Id: ");
        }
    }

    public File getLocalFile(){
        return localFile;
    }

    ImageView selectedImageP;

    public void setProfilePictureP(ImageView profilePicture){
        selectedImageP = profilePicture;
        if(localFile!=null){
            selectedImageP.setImageURI(Uri.fromFile(localFile));
        }else{
            selectedImageP.setImageResource(R.drawable.avatar);
        }
    }

    private void setProfilePicture() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if(selectedImageP!=null){
                        selectedImageP.setImageURI(Uri.fromFile(localFile));
                    }
                    selectedImage.setImageURI(Uri.fromFile(localFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(context, "Error while downloading profile picture", Toast.LENGTH_SHORT).show();
                    if(selectedImageP!=null){
                        selectedImageP.setImageResource(R.drawable.avatar);
                    }
                    selectedImage.setImageResource(R.drawable.avatar);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setView(View v) {
        progressBar = v.findViewById(R.id.progressBar);
    }

    public void setViewForGeo(View v) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public void updateMarkers() {
        progressBar.setVisibility(View.VISIBLE);
        googleMap.clear();
        StringToCoord(data, "green");
        StringToCoord(data2, "yellow");
        StringToCoord(data3, "red");
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
//                dataUpdated = false;
//                MapFragmentForAdmin mapFragmentForAdmin = new MapFragmentForAdmin();
//                mapFragmentForAdmin.setAdminActivity(adminClass);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        mapFragmentForAdmin).commit();
                break;
            case R.id.nav_geo:

                break;
            case R.id.nav_profile:
                profileForAdminFragment profileForAdminFragment = new profileForAdminFragment();
                profileForAdminFragment.setContext(context);
//                profileForAdminFragment.setAdmin(admin);
                profileForAdminFragment.setId(loadData("Id"));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileForAdminFragment).commit();
                break;
            case R.id.nav_qrcode:
//              mCodeScanner = null;
                QrData();
                QrForAdmin qrForAdmin = new QrForAdmin();
                qrForAdmin.setAdmin(admin);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        qrForAdmin).commit();
                break;
            case R.id.nav_announce:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AnnouncementFragmentAdmin()).commit();
                break;
            case R.id.nav_logout:
                saveData("", "log");
                startActivity(new Intent(getApplicationContext(), MainActivity_RegisterActivity.class));
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    FrameLayout frameLayout;
    Activity activity;

    public void QRonCreate(View view) {
        activity = this;
        frameLayout = view.findViewById(R.id.frame_container);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startCam();
                } else {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID
                    );
                }
            }
        });
    }

    public void startCam() {
        qrScannerAdmin qrScanner = new qrScannerAdmin();
//        qrScanner.setAdmin(admin);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                qrScanner).commit();
    }

    ZBarScannerView mScannerView;

    public void qrCam(ZBarScannerView LocalmScannerView) {
        mScannerView = LocalmScannerView;
        LocalmScannerView.setResultHandler(this);
        LocalmScannerView.startCamera();
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        saveData(result.getContents().trim(), "Id");
        setHeaderViewInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "permission denied.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startCam();
                }
            }
            break;
        }
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

    public void onMapReady(GoogleMap googleMap2) {
        googleMap = googleMap2;
        getData();
    }

    public void onMapReady2(GoogleMap googleMap2) {
        googleMap = googleMap2;
    }

    private void showMarker(LatLng currentLatLng, String color, int id) {
        int height = 150;
        int width = 100;
        BitmapDrawable bitmapdraw = null;
        if (color.compareTo("green") == 0) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
        } else if (color.compareTo("yellow") == 0) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellow);
        } else if (color.compareTo("red") == 0) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red);
        }
        if (bitmapdraw != null) {
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title(Integer.toString(id))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            if (!CameraMoved) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                        20));
                CameraMoved = true;
            }
        }
    }

    public void getData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("CurrLocation");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Green").getValue() != null) {
                    data = dataSnapshot.child("Green").getValue().toString();
                }
                if (dataSnapshot.child("Yellow").getValue() != null) {
                    data2 = dataSnapshot.child("Yellow").getValue().toString();
                }
                if (dataSnapshot.child("Red").getValue() != null) {
                    data3 = dataSnapshot.child("Red").getValue().toString();
                }
                if (!dataUpdated) {
                    dataUpdated = true;
                    updateMarkers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void QrData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Infected").getValue() != null) {
                    InfectedPassword = dataSnapshot.child("Infected").getValue().toString();
                }
                if (dataSnapshot.child("Not infected").getValue() != null) {
                    NotInfectedPassword = dataSnapshot.child("Not infected").getValue().toString();
                }
                if (dataSnapshot.child("Suspected").getValue() != null) {
                    SuspectedPassword = dataSnapshot.child("Suspected").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void StringToCoord(String data, String color) {
        System.out.println(data);
        ArrayList<String> lat = new ArrayList<>();
        data = data.replaceFirst("\\{", "*, ");
        String[] arrOfStr = data.split("\\*, ", -1);
        for (String a : arrOfStr) {
            String[] arrOfStr2 = a.split("=\\*", -1);
            for (String b : arrOfStr2) {
                String[] arrOfStr3 = b.split("\\*\\*", -1);
                for (String c : arrOfStr3) {
                    String[] arrOfStr4 = c.split("\\*", -1);
                    for (String d : arrOfStr4) {
                        if (isNumric(d)) {
                            lat.add(d);
                        }
                    }
                }
            }
        }
//        for (int i = 0; i < lat.size(); i += 3) {
//            if (lat.get(i) != null && lat.get(i + 1) != null && lat.get(i + 2) != null && isNumric(lat.get(i)) && isNumric(lat.get(i + 1)) && isNumric(lat.get(i + 2))) {
//                LatLng TempLatLng = new LatLng(Double.parseDouble(lat.get(i + 1)), Double.parseDouble(lat.get(i + 2)));
////                showMarker(TempLatLng, color, Integer.parseInt(lat.get(i)));
//                System.out.println(i);
//            }
//        }
    }

    public boolean isNumric(String string) {
        boolean numeric = true;
        try {
            Double num = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }

    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, admin.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
