package com.example.fevertracker.Activities.User;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fevertracker.OldClasses.LocationActivity;
import com.example.fevertracker.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.fevertracker.OldClasses.LocationActivity.SHARED_PREFS;

public class RealTimeTrackerForUser extends AppCompatActivity implements OnMapReadyCallback {

    //views
    GoogleMap googleMap;
    ImageView selectedImage;
    ProgressBar progressBar;
    RelativeLayout UserDetails;
    FrameLayout backgroundScreen;
    TextView UserName, UserID, lastSeen, mode;
    ImageButton Button;

    //vars
    Timer timer;
    String profileId;
    ArrayList<Marker> allUsersMarkers = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    ArrayList<Integer> statusForAll = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    static int refreshRate = 1000;
    int idChoosen, max = 0, panelHeight = 500;
    boolean cameraMoved = false, slide = false, following = false;


    public void gps(View view) {
        if (!following) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(idChoosen).getPosition(), googleMap.getCameraPosition().zoom));
            Button.setImageResource(R.drawable.ic_gps_fixed);
            following = true;
        } else {
            Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
            following = false;
        }
    }

    public void empty(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_tracker);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profileId = null;
            } else {
                profileId = extras.getString("UserId");
            }
        } else {
            profileId = (String) savedInstanceState.getSerializable("UserId");
        }
        initVar();
        initMap();
    }

    public void startLiveListenning() {
        timer = new Timer();
        getUsersLocations();

        TimerTask timerTask = new TimerTask() {
            public void run() {
//                GetNumberOfUsers();
                getUsersLocations();
            }
        };
        timer.schedule(timerTask, 5000, refreshRate);
    }

    public void getUsersLocations() {
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (profileId != null) {
                    cameraMoved = true;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ((int) dataSnapshot.getChildrenCount() > max) {
                        if (googleMap != null) {
                            googleMap.clear();
                        }
                        allUsersMarkers.clear();
                        statusForAll.clear();
                        for (int i = 0; i <= (int) dataSnapshot.getChildrenCount(); i++) {
                            allUsersMarkers.add(null);
                            statusForAll.add(null);
                        }
                    }
                    max = (int) dataSnapshot.getChildrenCount();
                    if (ds.getKey() != null) {
                        Object object = dataSnapshot.child(ds.getKey()).child("CurrLocation").getValue();
                        if (object != null && dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
                            if (dataSnapshot.child(ds.getKey()).child("state").getValue().toString().compareTo("1") != 0) {
                                LatLng loc = new LatLng(getWithIndex(object, 0, 0), getWithIndex(object, 1, 0));
                                addMarker(getInt(dataSnapshot.child(ds.getKey()).child("state").getValue().toString()), loc, getInt(ds.getKey()));
                                if (!cameraMoved) {
                                    builder.include(loc);
                                }
                            }
                        }
                    }
                }

                if (!cameraMoved) {
                    LatLngBounds bounds = builder.build();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    googleMap.animateCamera(cu);
                    cameraMoved = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public void GetNumberOfUsers() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                max = (int) dataSnapshot.getChildrenCount();
//                getUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public void getUsers() {
//
////        if (!initA || oldMax != max) {
////            Time = new int[max + 1];
////            Loc = new LatLng[max + 1];
////            status = new int[max + 1];
////            isOnline = new boolean[max + 1];
////            counter = new int[max + 1];
////            allUsers.clear();
////            for (int i = 0; i < max + 1; i++) {
////                allUsers.add(null);
////            }
////            initA = true;
////            oldMax = max;
////        }
//
//
//        FirebaseDatabase.getInstance().getReference().child("CurrLocation").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    if (ds.getKey() != null) {
//                        if (isNumric(ds.getKey())) {
//                            LatLng loc = new LatLng(getWithIndex(ds.getValue(), 0, 0), getWithIndex(ds.getValue(), 1, 0));
//                            addMarker((int) getWithIndex(ds.getValue(), 2, 1), Integer.parseInt(ds.getKey()), loc, (int) getWithIndex(ds.getValue(), 3, 0));
//                        }
//                    }
//                }
//
//                if (!cameraMoved) {
//                    LatLngBounds bounds = builder.build();
//                    int width = getResources().getDisplayMetrics().widthPixels;
//                    int height = getResources().getDisplayMetrics().heightPixels;
//                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//                    googleMap.animateCamera(cu);
//                    cameraMoved = true;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//        FirebaseDatabase.getInstance().getReference().child("CurrLocation").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    if (ds.getKey() != null) {
//                        if (isNumric(ds.getKey())) {
//                            LatLng loc = new LatLng(getWithIndex(ds.getValue(), 0, 0), getWithIndex(ds.getValue(), 1, 0));
//                            addMarker((int) getWithIndex(ds.getValue(), 2, 1), Integer.parseInt(ds.getKey()), loc, (int) getWithIndex(ds.getValue(), 3, 0));
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    public double getWithIndex(Object s, int index, double defualt) {
        try {
            String[] arr = s.toString().split(",", -1);
            return Double.parseDouble(arr[index]);
        } catch (Exception ignored) {
            return defualt;
        }
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

//    public boolean isNumric(String s) {
//        try {
//            return (max > Long.parseLong(s));
//        } catch (Exception Ignored) {
//            return false;
//        }
//    }

    public void addMarker(int status, LatLng loc, int id) {
        if (max >= id && id >= 0) {
//            int color = 122;

//            if (status == 2) {
//                color = 61;
//            } else if (status == 3) {
//                color = 0;
//            }

            String DrawableFile = "green_user";

            if (status == 2) {
                DrawableFile = "yellow_user";
            } else if (status == 3) {
                DrawableFile = "red_user";
            }

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resizeBitmap(DrawableFile, 130, 130));

            if (allUsersMarkers.get(id) == null || statusForAll.get(id) == null) {
                statusForAll.set(id, status);
                if (getInt(loadData("Id")) == id) {
                    allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                            .icon(icon)
                            .position(loc)
                            .title("You")));
                } else {
                    allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                            .icon(icon)
                            .position(loc)
                            .title(Integer.toString(id))));
                }
            } else {
                if (statusForAll.get(id) != status) {
                    allUsersMarkers.get(id).remove();

                    if (getInt(loadData("Id")) == id) {
                        allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                                .icon(icon)
                                .position(loc)
                                .title("You")));
                    } else {
                        allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                                .icon(icon)
                                .position(loc)
                                .title(Integer.toString(id))));
                    }
                    statusForAll.set(id, status);
                }
                if (allUsersMarkers.get(id).getPosition() != loc) {
                    RealTimeTrackerForUser.MarkerAnimation.animateMarkerToGB(allUsersMarkers.get(id), loc, new LocationActivity.LatLngInterpolator.Spherical());
                }
            }
            if (id == idChoosen) {
                if (slide) {
                    if (following) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(id).getPosition(), googleMap.getCameraPosition().zoom));
                    }
                    updateDetails();
                }
            } else if (profileId != null && id == Integer.parseInt(profileId)) {
                idChoosen = Integer.parseInt(profileId);
                updateDetails();
                slideView(UserDetails, 0, panelHeight, 500);
                slide = true;
                cameraMoved = true;
                profileId = null;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(id).getPosition(), 16)
                        , new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                following = true;
                                Button.setImageResource(R.drawable.ic_gps_fixed);
                            }

                            @Override
                            public void onCancel() {

                            }
                        }
                );

            }
        }
    }


    public Bitmap resizeBitmap(String drawableName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

//    public void addMarker(int i, int id, LatLng loc, int time) {
//        long currTime = System.currentTimeMillis() / 1000L;
//        boolean putMarker = false;
//        if (Loc[id] != null || status[id] != i) {
//            if (time > Time[id]) {
//                putMarker = true;
//                Time[id] = time;
//                Loc[id] = loc;
//                counter[id] = 0;
//            } else {
//                counter[id]++;
//            }
//        } else {
//            Time[id] = time;
//            Loc[id] = loc;
//            counter[id] = 241;
//            putMarker = true;
//        }
//
//        if (counter[id] > 250) {
//            counter[id] = 241;
//        }
//
//        if (counter[id] < 240 || (currTime - time) < 240) {
//            isOnline[id] = true;
//        } else {
//            isOnline[id] = false;
//        }
//        if (!cameraMoved) {
//            builder.include(loc);
//        }
//        int color = 122;
//
////        if (putMarker) {
//            if (i == 2) {
//                color = 61;
//            } else if (i == 3) {
//                color = 0;
//            }
//
//            if (allUsers.get(id) == null) {
//                allUsers.set(id, googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(color)).position(loc).title(Integer.toString(id))));
//            } else {
//                if (status[id] != i) {
//                    allUsers.get(id).remove();
//                    allUsers.set(id, googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(color)).position(loc).title(Integer.toString(id))));
//                } else {
//                    RealTimeTracker.MarkerAnimation.animateMarkerToGB(allUsers.get(id), loc, new LocationActivity.LatLngInterpolator.Spherical());
//                }
//            }
//            status[id] = i;
//            if (id == idChoosen) {
//                if (slide) {
//                    if (following) {
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc[idChoosen], googleMap.getCameraPosition().zoom));
//                    }
//                    updateDetails();
//                }
//            } else if (profileId != null && id == Integer.parseInt(profileId)) {
//                idChoosen = Integer.parseInt(profileId);
//                updateDetails();
//                slideView(UserDetails, 0, panelHeight, 500);
//                slide = true;
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc[idChoosen], 16));
//                Button.setImageResource(R.drawable.ic_gps_fixed);
//                following = true;
//                cameraMoved = true;
//                profileId = null;
//            }
////        }
//    }

    public String TimeConverter(long unixSeconds) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public void slideView(final View view, int currentHeight, int newHeight, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void initVar() {
        progressBar = findViewById(R.id.progressBar);
        UserDetails = findViewById(R.id.UserDetails);
        UserName = findViewById(R.id.UserName);
        UserID = findViewById(R.id.UserID);
        lastSeen = findViewById(R.id.Time);
        mode = findViewById(R.id.status);
        selectedImage = findViewById(R.id.profilePicture);
        Button = findViewById(R.id.Button);
        backgroundScreen = findViewById(R.id.backgroundScreen);
        backgroundScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
                following = false;
                return false;
            }
        });

        UserDetails.post(new Runnable() {
            @Override
            public void run() {
                panelHeight = UserDetails.getHeight();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) UserDetails.getLayoutParams();
                params.height = 0;
                UserDetails.setLayoutParams(params);
                UserDetails.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    idChoosen = Integer.parseInt(marker.getTitle());
                }catch (Exception ignored){
                    idChoosen = getInt(loadData("Id"));
                }
                updateDetails();
                slideView(UserDetails, 0, panelHeight, 500);
                slide = true;
                return false;
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slide) {
                    slideView(UserDetails, panelHeight, 0, 500);
                    slide = false;
                    Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
                    following = false;
                }
            }
        });
        startLiveListenning();
    }

    public void updateDetails() {
        getPic(Integer.toString(idChoosen));

        FirebaseDatabase.getInstance().getReference().child("Member").child(Integer.toString(idChoosen)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isOnline = false;
                if (dataSnapshot.child("name").exists()) {
                    UserName.setText(dataSnapshot.child("name").getValue().toString());
                }
                if (dataSnapshot.child("CurrLocation").exists()) {
                    if ((System.currentTimeMillis() - (getWithIndex(dataSnapshot.child("CurrLocation").getValue(), 3, 0) * 1000L)) < 180000) {
                        isOnline = true;
                    }
                }
                if (isOnline) {
                    mode.setText("online");
                    mode.setTextColor(Color.GREEN);
                } else {
                    mode.setText("offline");
                    mode.setTextColor(Color.RED);
                }
                UserID.setText(Integer.toString(idChoosen));

                if (dataSnapshot.child("CurrLocation").exists()) {
                    lastSeen.setText(TimeConverter((long) getWithIndex(dataSnapshot.child("CurrLocation").getValue(), 3, 0)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getPic(String id) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(id);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    selectedImage.setImageURI(Uri.fromFile(localFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
//                    System.out.println(exception);
                    selectedImage.setImageResource(R.drawable.avatar);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class MarkerAnimation {
        public static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LocationActivity.LatLngInterpolator latLngInterpolator) {
            final LatLng startPosition = marker.getPosition();
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final Interpolator interpolator = new AccelerateDecelerateInterpolator();
            final float durationInMs = refreshRate - 100;
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

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onBackPressed() {
        if (slide) {
            slideView(UserDetails, panelHeight, 0, 500);
            slide = false;
        } else {
            super.onBackPressed();
        }
    }
}
