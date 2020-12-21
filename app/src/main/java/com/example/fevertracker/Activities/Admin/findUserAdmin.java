package com.example.fevertracker.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fevertracker.R;
import com.example.fevertracker.Activities.LocationHistory;
import com.example.fevertracker.Fragments.Admin.profileForAdminFragment;
import com.example.fevertracker.OldClasses.qrScannerAdmin;
import com.example.fevertracker.Classes.userSearch;
import com.example.fevertracker.Adapters.userSearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class findUserAdmin extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    //Views
    Activity activity = this;
    ListView userFound;
    EditText input_search;
    ProgressBar progressBar;
    LinearLayout qrButtonAdmin;
    FrameLayout FrameContainer;

    //Vars
    Context context = this;
    ZBarScannerView mScannerView;
    String search_input, Qr_result;
    findUserAdmin findUserAdmin = this;
    ArrayList<Long> Users = new ArrayList<>();
    ArrayList<userSearch> toPrint = new ArrayList<>();
    com.example.fevertracker.Fragments.Admin.profileForAdminFragment profileForAdminFragment;
    //    long max;
    int width, height;
    final int RequestCameraPermissionID = 1001;
    public static final String SHARED_PREFS = "sharedPrefs";
    boolean search_opened = false, cameraStarted = false, buttOpened = true, userFoundBool = false;

    public void realTimeMap(View view) {
        if (!loadData("Id").isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            findUserOnMap();
        }
    }

    public void Geofence(View view) {
        startActivity(new Intent(getApplicationContext(), GeoFenceForAdmin.class));
        CustomIntent.customType(context, "left-to-right");
    }

    public void LocationHistory(View view) {
        startActivity(new Intent(getApplicationContext(), LocationHistory.class));
        CustomIntent.customType(context, "left-to-right");
    }

    public void back(View view) {
        onBackPressed();
    }

    public void search_bar(View view) {
        if (!search_opened) {
            if (cameraStarted) {
                mScannerView.stopCamera();
                closeCam();
            }
            openSearch();
        } else {
            hideKeyBoard(input_search);
        }
    }

    public void qrScanner(View view) {
        if (search_opened) {
            closeSearch();
        }
        openQrScannerCam();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user_admin);

        initViews();
        if (userFoundBool) {
            userFoundFunc(loadData("Id"));
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(this::openSearch, 1500);
        }
    }

    public void initViews() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        FrameContainer = findViewById(R.id.frame_container);
        qrButtonAdmin = findViewById(R.id.qrButtonAdmin);

        if (!loadData("Id").isEmpty()) {
            userFoundBool = true;
        }

        input_search = findViewById(R.id.input_search);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        userFound = findViewById(R.id.userFound);
        userFound.setOnItemClickListener((parent, view, position, id) -> userFoundFunc(Long.toString(Users.get(position))));
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setUpSearch();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setUpSearch() {
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //                    if (userFoundBool) {
                //                        frame_container.setVisibility(View.GONE);
                //                        userFound.setVisibility(View.VISIBLE);
                //                    }
                //                    if (userFoundBool) {
                //                        frame_container.setVisibility(View.VISIBLE);
                //                        userFound.setVisibility(View.GONE);
                //                    }
                getUsers(input_search.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!input_search.getText().toString().isEmpty()) {
////                    if (userFoundBool) {
////                        frame_container.setVisibility(View.GONE);
////                        userFound.setVisibility(View.VISIBLE);
////                    }
//                    getUsers();
//                } else {
////                    if (userFoundBool) {
////                        frame_container.setVisibility(View.VISIBLE);
////                        userFound.setVisibility(View.GONE);
////                    }
//                    printAllUsers();
//                }
            }
        });
    }

//    public void printAllUsers() {
//        toPrint.clear();
//        Users.clear();
//        search_input = input_search.getText().toString();
//        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
////                        if (ds.getKey() != null) {
////                            if (isFound(search_input, ds.getKey())) {
////                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
////                                    Users.add(Long.parseLong(ds.getKey()));
////                                    String id = ds.getKey();
////                                    String name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
////                                    String passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
////                                    int Status = 1;
////                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
////                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
////                                    }
//////                                    getUri(id,toPrint.size());
////                                    toPrint.add(new userSearch(name, passport, id, Status));
////                                }
////                            } else if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null && isFound(search_input.toLowerCase().trim(), Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString().toLowerCase().trim())) {
////                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
////                                    Users.add(Long.parseLong(ds.getKey()));
////                                    String id = ds.getKey();
////                                    String name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
////                                    String passport = "";
////                                    if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null) {
////                                        passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
////                                    }
////                                    int Status = 1;
////                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
////                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
////                                    }
//////                                    getUri(id,toPrint.size());
////                                    toPrint.add(new userSearch(name, passport, id, Status));
////                                }
////                            } else if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null && isFound(search_input, Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString().toLowerCase().trim())) {
////                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
////                                    Users.add(Long.parseLong(ds.getKey()));
////                                    String id = ds.getKey();
////                                    String name = "";
////                                    if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null) {
////                                        name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
////                                    }
////                                    String passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
////                                    int Status = 1;
////                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
////                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
////                                    }
//////                                    getUri(id,toPrint.size());
////                                    toPrint.add(new userSearch(name, passport, id, Status));
////                                }
////                            }
////                        }
//                        if (ds.child("name").exists() && ds.child("passport").exists() && ds.child("state").exists() && ds.getKey() != null) {
//                            String name = Objects.requireNonNull(ds.child("name").getValue()).toString(),
//                                    passport = Objects.requireNonNull(ds.child("passport").getValue()).toString(), id = ds.getKey();
//                            int status = getInt(ds.child("state").getValue());
//                            toPrint.add(new userSearch(name, passport, id, status));
//                            Users.add(getLong(ds.getKey()));
//                        }
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//                userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
//                userFound.setAdapter(adapter);
//                getUserPics();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    public void getUsers(final boolean allUsers) {
        toPrint.clear();
        Users.clear();
        search_input = input_search.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        if (ds.getKey() != null) {
//                            if (isFound(search_input, ds.getKey())) {
//                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
//                                    Users.add(Long.parseLong(ds.getKey()));
//                                    String id = ds.getKey();
//                                    String name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
//                                    String passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
//                                    int Status = 1;
//                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
//                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
//                                    }
////                                    getUri(id,toPrint.size());
//                                    toPrint.add(new userSearch(name, passport, id, Status));
//                                }
//                            } else if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null && isFound(search_input.toLowerCase().trim(), Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString().toLowerCase().trim())) {
//                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
//                                    Users.add(Long.parseLong(ds.getKey()));
//                                    String id = ds.getKey();
//                                    String name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
//                                    String passport = "";
//                                    if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null) {
//                                        passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
//                                    }
//                                    int Status = 1;
//                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
//                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
//                                    }
////                                    getUri(id,toPrint.size());
//                                    toPrint.add(new userSearch(name, passport, id, Status));
//                                }
//                            } else if (dataSnapshot.child(ds.getKey()).child("passport").getValue() != null && isFound(search_input, Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString().toLowerCase().trim())) {
//                                if (!Users.contains(Long.parseLong(ds.getKey()))) {
//                                    Users.add(Long.parseLong(ds.getKey()));
//                                    String id = ds.getKey();
//                                    String name = "";
//                                    if (dataSnapshot.child(ds.getKey()).child("name").getValue() != null) {
//                                        name = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("name").getValue()).toString();
//                                    }
//                                    String passport = Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("passport").getValue()).toString();
//                                    int Status = 1;
//                                    if (dataSnapshot.child(ds.getKey()).child("state").getValue() != null) {
//                                        Status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("state").getValue()).toString());
//                                    }
////                                    getUri(id,toPrint.size());
//                                    toPrint.add(new userSearch(name, passport, id, Status));
//                                }
//                            }
//                        }
                        if (ds.child("name").exists() && ds.child("passport").exists() && ds.child("state").exists() && ds.getKey() != null) {
                            String name = Objects.requireNonNull(ds.child("name").getValue()).toString(),
                                    passport = Objects.requireNonNull(ds.child("passport").getValue()).toString(), id = ds.getKey();
                            int status = getInt(ds.child("state").getValue());
                            if (allUsers || id.contains(search_input) || name.contains(search_input) || passport.contains(search_input)) {
                                toPrint.add(new userSearch(name, passport, id, status));
                                Users.add(getLong(ds.getKey()));
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

//                userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
//                userFound.setAdapter(adapter);
//                getUserPics();
                userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
                userFound.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    int i;

//    public void getUserPics() {
//        for (i = 0; i < toPrint.size(); i++) {
//            StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(toPrint.get(i).getId());
//            try {
//                final File localFile = File.createTempFile("images", "jpg");
//                storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//                    try {
//                        toPrint.get(i).setUri(Uri.fromFile(localFile));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }).addOnFailureListener(exception -> {
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
//        userFound.setAdapter(adapter);
//    }

//    public void getUri(String id, final int i) {
//        FirebaseStorage.getInstance().getReference("uploads").child(id).getDownloadUrl().addOnSuccessListener(uri -> {
//            // Got the download URL for 'users/me/profile.png'
//            toPrint.get(i).setUri(uri);
//
////            generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
//        }).addOnFailureListener(exception -> {
//            userSearchAdapter adapter = new userSearchAdapter(getApplicationContext(), R.layout.search_users_adapter, toPrint);
//            userFound.setAdapter(adapter);
//            // Handle any errors
//        });
//    }

    public int getInt(Object o) {
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return 1;
        }
    }

    public Long getLong(String o) {
        try {
            return Long.parseLong(o);
        } catch (Exception e) {
            return 0L;
        }
    }


//    public boolean isNumeric(String s) {
//        try {
//            Long.parseLong(s);
//            return true;
//        } catch (Exception Ignored) {
//            return false;
//        }
//    }

    public void findUserOnMap() {
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.child(loadData("Id")).child("CurrLocation").getValue() != null) {
                    Intent i = new Intent(getApplicationContext(), RealTimeTracker.class);
                    String UserId = loadData("Id");
                    i.putExtra("UserId", UserId);
                    startActivity(i);
                    CustomIntent.customType(context, "left-to-right");
                } else {
                    Toast.makeText(context, "User was not found on the map.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openQrScannerCam() {
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

    public void startCam() {
        FrameContainer.setVisibility(View.VISIBLE);
        userFound.setVisibility(View.GONE);
        qrScannerAdmin qrScanner = new qrScannerAdmin();
        qrScanner.setAdmin(findUserAdmin);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                qrScanner).commit();
        cameraStarted = true;
    }

    public void qrCam(ZBarScannerView LocalScannerView) {
        mScannerView = LocalScannerView;
        LocalScannerView.setResultHandler(this);
        LocalScannerView.startCamera();
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        Qr_result = result.getContents().trim();
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.child(Qr_result).exists()) {
                        Toast.makeText(getApplicationContext(), "User Id is: " + Qr_result, Toast.LENGTH_LONG).show();
                        userFoundFunc(Qr_result);
                    } else {
                        Toast.makeText(getApplicationContext(), "User was not found.", Toast.LENGTH_LONG).show();
                        startCam();
                    }
                } catch (Exception ignored) {
                    Toast.makeText(getApplicationContext(), "User was not found.", Toast.LENGTH_LONG).show();
                    startCam();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCameraPermissionID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "permission denied.", Toast.LENGTH_LONG).show();
                    return;
                }
                startCam();
            }
        }
    }

    public void userFoundFunc(String id) {
        userFoundBool = true;
        saveData(id, "Id");
        profileForAdminFragment = new profileForAdminFragment();
        profileForAdminFragment.setContext(context);
        profileForAdminFragment.setfindUserAdmin(findUserAdmin);
        profileForAdminFragment.setLocalFile(Uri.parse(loadData("pic")));
        profileForAdminFragment.setId(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                profileForAdminFragment).commit();

        Intent intent = new Intent();
        setResult(2, intent);
        if (search_opened) {
            closeSearch();
        }
        if (cameraStarted) {
            closeCam();
        }
        if (buttOpened) {
            animationOut(qrButtonAdmin);
            buttOpened = false;
        }
        FrameContainer.setVisibility(View.VISIBLE);
        userFound.setVisibility(View.GONE);
    }

    public void LoadPic(final ImageView selectedImage) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                saveData(Uri.fromFile(localFile).toString(), "pic");
                selectedImage.setImageURI(Uri.fromFile(localFile));
            }).addOnFailureListener(exception -> {
                Toast.makeText(getApplicationContext(), "Error while downloading profile picture", Toast.LENGTH_SHORT).show();
                saveData("", "pic");
                selectedImage.setImageResource(R.drawable.avatar);
            });
        } catch (IOException e) {
            e.printStackTrace();
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

    public void openSearch() {
        if (!search_opened) {
            if (userFoundBool) {
                animationIn(qrButtonAdmin);
                FrameContainer.setVisibility(View.GONE);
                userFound.setVisibility(View.VISIBLE);
                buttOpened = true;
            }
            getUsers(true);
            slideViewWidth(input_search, dpToPx(40), width - dpToPx(60), 500);
            input_search.setHint("Enter User's name, passport or Id.");
//            showSoftKeyboard(input_search);
            search_opened = true;
        }
    }

    public void closeSearch() {
        if (search_opened) {
            if (userFoundBool) {
                animationOut(qrButtonAdmin);
                FrameContainer.setVisibility(View.VISIBLE);
                userFound.setVisibility(View.GONE);
                buttOpened = false;
            }
            hideKeyBoard(input_search);
            slideViewWidth(input_search, width - dpToPx(60), dpToPx(40), 500);
            input_search.setHint("");
            input_search.setText("");
            input_search.clearFocus();
            search_opened = false;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (search_opened) {
            closeSearch();
            if (userFoundBool) {
                userFoundFunc(loadData("Id"));
            }
        } else if (cameraStarted) {
            closeCam();
            if (userFoundBool) {
                userFoundFunc(loadData("Id"));
            }
        } else {
            super.onBackPressed();
        }
    }

    public void closeCam() {
        FrameContainer.setVisibility(View.GONE);
        userFound.setVisibility(View.VISIBLE);
        cameraStarted = false;
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().width = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

//    public void slideViewHeight(final View view, int currentHeight, int newHeight, long duration) {
//
//        ValueAnimator slideAnimator = ValueAnimator
//                .ofInt(currentHeight, newHeight)
//                .setDuration(duration);
//
//        /* We use an update listener which listens to each tick
//         * and manually updates the height of the view  */
//
//        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation1) {
//                Integer value = (Integer) animation1.getAnimatedValue();
//                view.getLayoutParams().height = value.intValue();
//                view.requestLayout();
//            }
//        });
//
//        /*  We use an animationSet to play the animation  */
//
//        AnimatorSet animationSet = new AnimatorSet();
//        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
//        animationSet.play(slideAnimator);
//        animationSet.start();
//    }

    public void animationIn(View view) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(1000);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setFillAfter(true);
        view.startAnimation(inFromBottom);
    }

    private void animationOut(View view) {
        Animation outToBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        outToBottom.setDuration(500);
        outToBottom.setInterpolator(new AccelerateInterpolator());
        outToBottom.setFillAfter(true);
        view.startAnimation(outToBottom);
    }

//    public void showSoftKeyboard(View view) {
//        if (view.requestFocus()) {
//            InputMethodManager imm = (InputMethodManager)
//                    getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
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
}
