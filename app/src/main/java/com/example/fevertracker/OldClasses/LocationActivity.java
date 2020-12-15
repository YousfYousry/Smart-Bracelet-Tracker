package com.example.fevertracker.OldClasses;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fevertracker.Activities.MainActivity_RegisterActivity;
import com.example.fevertracker.Dialogs.ChangePicBottomSheetDialog;
import com.example.fevertracker.Fragments.User.AnnouncementFragment;
import com.example.fevertracker.Services.LocationService;
import com.example.fevertracker.Fragments.User.QrFragment;
import com.example.fevertracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class LocationActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener, ZBarScannerView.ResultHandler {

    ImageView selectedImage;
    Context context = this;
    final int RequestCameraPermissionID = 1001;
    LocationActivity locationActivity = this;
    public static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 100;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 2000; //1 second
    private static final long FASTEST_INTERVAL = 2000; // 1 second
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    GoogleMap googleMap;
    Button QrScanner;
    ImageView qrImage;
    boolean CameraMoved = false;
    boolean init = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    private DrawerLayout drawer;
    TextView name, email;
    LatLng currentLatLng;
    Intent serviceIntent;
    String Id = "";
    String Status = "";
    Marker currentLocationMarker;
    boolean dataUpdated = false;
    Location LastLocation;
    String data2 = "", data3 = "";
    ProgressBar progressBar;
    ChangePicBottomSheetDialog bottomSheet;
    ActionBarDrawerToggle toggle;

    public void update(View view) {
        updateMarkers();
    }

    public void addPic(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Id = loadData("Id");
        StartStatusListener();
        bottomSheet = new ChangePicBottomSheetDialog();
        bottomSheet.setLocationActivity(locationActivity);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.z_old_activity_location);

        serviceIntent = new Intent(this, LocationService.class);

        if (!isMyServiceRunning(LocationService.class)) {
            Toast.makeText(this, "service created", LENGTH_LONG).show();
            ContextCompat.startForegroundService(this, serviceIntent);
        }
//        else{
//            stopService(serviceIntent);
//            ContextCompat.startForegroundService(this, serviceIntent);
//        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

        if (loadData("not").compareTo("n") == 0) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel(2);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AnnouncementFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_announce);
            saveData("", "not");
        } else if (savedInstanceState == null) {
//            MapFragment mapFragment = new MapFragment();
//            mapFragment.setLocationActivity(locationActivity);
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    mapFragment).commit();
//            navigationView.setCheckedItem(R.id.nav_map);
        }

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);
        selectedImage = headerView.findViewById(R.id.profilePicture);
        setHeaderViewInfo();
        setProfilePicture();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
//                dataUpdated = false;
//                MapFragment mapFragment = new MapFragment();
//                mapFragment.setLocationActivity(locationActivity);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        mapFragment).commit();
                break;
            case R.id.nav_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setId(loadData("Id"));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileFragment).commit();
                break;
            case R.id.nav_announce:
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.cancel(2);
                saveData("", "not");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AnnouncementFragment()).commit();
                break;
            case R.id.nav_qrcode:
                QrFragment QR = new QrFragment();
                QR.setLocationActivity(locationActivity);
                QR.setId(loadData("Id"));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        QR).commit();
                break;
            case R.id.nav_logout:
                if (isMyServiceRunning(LocationService.class)) {
                    stopService(serviceIntent);
                }
                saveData("", "Id");
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), MainActivity_RegisterActivity.class));
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (QrActive) {
            qrBackPressed();
        } else {
            super.onBackPressed();
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
        googleMap.setMyLocationEnabled(true);
        init = true;
        currentLocationMarker = null;
        CameraMoved = false;
        mGoogleApiClient.connect();
        getData();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

//    public void gallery() {
//        if (checkSelfPermissions(this)) {
//            GetImageFromGallery();
//        }
//    }
//
//    public void camera() {
//        if (checkSelfPermissions(this)) {
//            ClickImageFromCamera();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_STORAGE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//            }
//            //bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
//            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
//                Toast.makeText(this, "ImageCropper needs Storage access in order to store your profile picture.", Toast.LENGTH_LONG).show();
//                finish();
//            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "ImageCropper needs Camera access in order to take profile picture.", Toast.LENGTH_LONG).show();
//                finish();
//            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
//                Toast.makeText(this, "ImageCropper needs Camera and Storage access in order to take profile picture.", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } else if (requestCode == ONLY_CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            }
//            //  bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
//            else {
//                Toast.makeText(this, "ImageCropper needs Camera access in order to take profile picture.", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } else if (requestCode == ONLY_STORAGE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            }
//            //  bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
//            else {
//                Toast.makeText(this, "ImageCropper needs Storage access in order to store your profile picture.", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }
//    }
//
//    public boolean checkSelfPermissions(Activity activity) {
//
//
//        PackageManager mPackageManager = activity.getPackageManager();
//        int hasPermStorage = mPackageManager.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
//        int hasPermCam = mPackageManager.checkPermission(android.Manifest.permission.CAMERA, activity.getPackageName());
//
//
//        if (hasPermStorage != PackageManager.PERMISSION_GRANTED && hasPermCam != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_STORAGE_REQUEST_CODE);
//            return false;
//        } else {
//            return true;
//        }
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_STORAGE_REQUEST_CODE);
////                return false;
////            } else if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, ONLY_CAMERA_REQUEST_CODE);
////                return false;
////            } else if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ONLY_STORAGE_REQUEST_CODE);
////                return false;
////            }
////        }
////        return true;
//    }
//
//    public void ClickImageFromCamera() {
//        try {
//
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            file = new File(android.os.Environment.getExternalStorageDirectory(), "makegifimage.jpg");
//            uri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            startActivityForResult(intent, 1);
//
//        } catch (ActivityNotFoundException anfe) {
//            Toast.makeText(getApplicationContext(), "couldnt open your camera", LENGTH_SHORT).show();
//        }
//    }
//
//    public void GetImageFromGallery() {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1, 1)
//                .start(this);
//    }

    Uri mImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                selectedImage.setImageURI(result.getUri());
                mImageUri = result.getUri();
                uploadFile();
            } else {
                if (!profilePic) {
                    finish();
                }
            }
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profilePic = true;
                            Toast.makeText(context, "Profile picture has been uploaded successfully", LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "Error while uploading profile picture", LENGTH_SHORT).show();
                        }
                    });
        }
    }

    File localFile = null;
    boolean profilePic = false;

    private void setProfilePicture() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));

        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                selectedImage.setImageURI(Uri.fromFile(localFile));
                profilePic = true;
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(LocationActivity.this);

//                Toast.makeText(context, "Error while downloading profile picture", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            Toast.makeText(this, "google not available", LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION);

                // MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void showAllMarker(LatLng currentLatLng, String color) {
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
                    .title("title")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }

    private void showMarker(@NonNull Location currentLocation) {
        LastLocation = currentLocation;
        // options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info
//        options = new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).title("Hello Maps");
//
//
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.self_icon_green));


        if (init) {
            int height = 150;
            int width = 100;
            BitmapDrawable bitmapdraw = null;

            if (Status.compareTo("Green") == 0) {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.selfgreen);
                if (currentLocationMarker != null) {
                    Bitmap c = bitmapdraw.getBitmap();
                    Bitmap NewMarker = Bitmap.createScaledBitmap(c, width, height, false);
                    currentLocationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(NewMarker));
                }
            } else if (Status.compareTo("Yellow") == 0) {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.selfyellow);
                if (currentLocationMarker != null) {
                    Bitmap c = bitmapdraw.getBitmap();
                    Bitmap NewMarker = Bitmap.createScaledBitmap(c, width, height, false);
                    currentLocationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(NewMarker));
                }
            } else if (Status.compareTo("Red") == 0) {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.selfred);
                if (currentLocationMarker != null) {
                    Bitmap c = bitmapdraw.getBitmap();
                    Bitmap NewMarker = Bitmap.createScaledBitmap(c, width, height, false);
                    currentLocationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(NewMarker));
                }
            }
            if (bitmapdraw != null) {
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                if (currentLocationMarker == null) {
                    currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .title("title")
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                } else {
                    MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLatLng, new LatLngInterpolator.Spherical());
                }
                if (!CameraMoved) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                            13));
                    CameraMoved = true;
                }
            }
//            mapMarker = googleMap.addMarker(options);
//            mapMarker.setTitle(mLastUpdateTime);
//
//            if (!CameraMoved) {
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
//                        13));
//                CameraMoved = true;
//            }

        }
    }

    public static class MarkerAnimation {
        public static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
            final LatLng startPosition = marker.getPosition();
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final Interpolator interpolator = new AccelerateDecelerateInterpolator();
            final float durationInMs = 2000;
            handler.post(new Runnable() {
                long elapsed;
                float t;
                float v;

                @Override
                public void run() {
                    // Calculate progress using interpolator
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = elapsed / durationInMs;
                    v = interpolator.getInterpolation(t);
                    marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                    // Repeat till progress is complete.
                    if (t < 1) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    public interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Spherical implements LatLngInterpolator {
            /* From github.com/googlemaps/android-maps-utils */
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                // http://en.wikipedia.org/wiki/Slerp
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);
                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6) {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;
                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);
                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }

    }

    public void getData() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("CurrLocation");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

    boolean QrActive = false;
    String notInData1 = "", susData2 = "", inData3 = "";
    final Activity activity = this;
    ConnectivityManager cm;

    public void QrCreate(View v) {
        getQrCodes();
        QrScanner = v.findViewById(R.id.QrScanner);
        qrImage = v.findViewById(R.id.imageView);
        QrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    public boolean IsNetworkEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).

                getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).

                        getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startQrCam();
                }
            }
            break;
        }
    }

    public void startQrCam() {
        QrActive = true;
        qrScanner qrScanner = new qrScanner();
        qrScanner.setLocationActivity(locationActivity);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
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
        String key = FirebaseDatabase.getInstance().getReference().child("Location").child(loadData("Id")).child("QR scanner location").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Location").child(loadData("Id")).child("QR scanner location").child(key).setValue("*" + mCurrentLocation.getLatitude() + "**" + mCurrentLocation.getLongitude() + "**" + Calendar.getInstance().getTime() + "*");
        gotResult(result.getContents());
    }

    public void getQrCodes() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Infected").getValue() != null) {
                    inData3 = dataSnapshot.child("Infected").getValue().toString();
                }
                if (dataSnapshot.child("Suspected").getValue() != null) {
                    susData2 = dataSnapshot.child("Suspected").getValue().toString();
                }
                if (dataSnapshot.child("Not infected").getValue() != null) {
                    notInData1 = dataSnapshot.child("Not infected").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void qrBackPressed() {
        QrActive = false;
        mScannerView.stopCamera();
        QrFragment QR = new QrFragment();
        QR.setLocationActivity(locationActivity);
        QR.setId(loadData("Id"));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                QR).commit();
    }

    public void gotResult(String result) {
        qrBackPressed();
        if (!result.isEmpty() && result.compareTo(notInData1) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("1");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Not infected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
        } else if (!result.isEmpty() && result.compareTo(susData2) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("2");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Suspected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
        } else if (!result.isEmpty() && result.compareTo(inData3) == 0) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").setValue("3");
            FirebaseDatabase.getInstance().getReference().child("adminInfo").child("QR").child("Infected").setValue(getAlphaNumericString(20));
            Toast.makeText(this, "Status updated successfully.", LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "The QR was invalid.", LENGTH_LONG).show();
        }
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

    public void setView(View v) {
        progressBar = v.findViewById(R.id.progressBar);
    }

    public void updateMarkers() {
        progressBar.setVisibility(View.VISIBLE);
        googleMap.clear();
        if (LastLocation != null) {
            currentLocationMarker = null;
            showMarker(LastLocation);
        }
        StringToCoord(data2, "yellow");
        StringToCoord(data3, "red");
        progressBar.setVisibility(View.GONE);
    }

    public void StringToCoord(String data, String color) {
        ArrayList<String> lat = new ArrayList<>();
        String[] arrOfStr = data.split(" \\*", -1);
        for (String a : arrOfStr) {
            String[] arrOfStr2 = a.split("\\*\\*", -1);
            for (String b : arrOfStr2) {
                String[] arrOfStr3 = b.split("\\*", -1);
                for (String c : arrOfStr3) {
                    if (isNumric(c)) {
                        lat.add(c);
                    }
                }
            }
        }
        for (int i = 0; i < lat.size() - 1; i += 2) {
            if (lat.get(i) != null && lat.get(i + 1) != null && isNumric(lat.get(i)) && isNumric(lat.get(i + 1))) {
                LatLng TempLatLng = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lat.get(i + 1)));
                showAllMarker(TempLatLng, color);
                System.out.println(i);
            }
        }
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        showMarker(location);
    }

    //    private boolean isMyServiceRunning(){
//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        assert am != null;
//        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
//        for (ActivityManager.RunningServiceInfo runningServiceInfo : l) {
//            if (runningServiceInfo.service.getClassName().equals("LocationService")) {
//                if (runningServiceInfo.foreground) {
//                    return true;
//                    //service run in foreground
//                }
//                return true;
//            }
//        }
//        return false;
//    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
        }
    }

    public void setHeaderViewInfo() {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id"));
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue() != null) {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                }
                if (dataSnapshot.child("email").getValue() != null) {
                    email.setText(dataSnapshot.child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void StartStatusListener() {
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("Member").child(Id);
        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("state").getValue() != null) {
                    status(dataSnapshot.child("state").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void status(String status) {
        if (Integer.parseInt(status) == 1) {
            Status = "Green";
        }
        if (Integer.parseInt(status) == 2) {
            Status = "Yellow";
        }
        if (Integer.parseInt(status) == 3) {
            Status = "Red";
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}