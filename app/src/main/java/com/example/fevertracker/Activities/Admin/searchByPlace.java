package com.example.fevertracker.Activities.Admin;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fevertracker.Adapters.PlaceUsersAdapter;
import com.example.fevertracker.Classes.PlaceUsers;
import com.example.fevertracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class searchByPlace extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {
    //views
    AutoCompleteTextView mSearchText;
    FrameLayout chooseRadius, SearchFrame, SearchFrame2, exit, enter;
    RelativeLayout UsersContainer;
    SlidingUpPanelLayout slidingPanel;
    TextView PlaceText, DateText;
    EditText radiusEdit;
    ProgressBar progressBar;

    //vars
    Uri pic;
    Circle circle;
    Context context = this;
    LatLng placeLocation;
    String date = "", searchString, State = "", City = "", Address = "";
    Address address = null;
    ArrayList<PlaceUsers> toPrintPlaces = new ArrayList<>();
    ArrayList<Integer> users = new ArrayList<>();
    ArrayList<String> usersPrinted = new ArrayList<>();
    ListView UsersList;
    RelativeLayout.LayoutParams PanelParam;
    boolean searchOpened = false, button = true, controlsOpened = false, sliding = false;
    boolean[][] timeUsers;
    int height, width, slidingHeight, max = 0;
    private static final String TAG = "searchByPlaceMap";
    private GoogleMap mMap;
    double radius = 100;

    public void searchClicked(View view) {
        if (!searchOpened) {
            if (sliding) {
                if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
            searchOpened = true;
            showSoftKeyboard(mSearchText);
            slideViewWidth(SearchFrame, dpToPx(45), (width - dpToPx(30)), 500);
            slideViewHeight(SearchFrame2, dpToPx(45), dpToPx(145), 300);
            mSearchText.setHint("Enter Place name, Address, City or Zip Code");
            exit.setVisibility(View.VISIBLE);
        } else {
            geoLocate();
        }
    }

    public void empty(View view) {
    }

    public void searchPlaceClicked(View view) {
        if (!searchOpened) {
            if (sliding) {
                if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
            searchOpened = true;
            slideViewWidth(SearchFrame, dpToPx(45), (width - dpToPx(30)), 500);
            slideViewHeight(SearchFrame2, dpToPx(45), dpToPx(145), 300);
            mSearchText.setHint("Enter Place name, Address, City or Zip Code");
            exit.setVisibility(View.VISIBLE);
            showSoftKeyboard(mSearchText);
        } else {
            geoLocate();
        }
    }

    public void datePickerClicked(View view) {
        showDatePickerDialog();
    }

    public void RadiusChanger(View view) {
        if (address == null) {
            Toast.makeText(this, "Search for a Place First.", Toast.LENGTH_SHORT).show();
            showSoftKeyboard(mSearchText);
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Choose a date First.", Toast.LENGTH_SHORT).show();
            showDatePickerDialog();
            return;
        }
        if (!controlsOpened) {
            openControls();
        } else {
            if (searchOpened) {
                closeSearch();
            }
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void show(View view) {
    }

    public void exit(View view) {
        if (searchOpened) {
            closeSearch();
        }
    }

    public void bigger(View view) {
        if (placeLocation != null) {
            radius *= 1.25;
            changeRadius();
            button = false;
            radiusEdit.setText(Integer.toString((int) radius));
            button = true;
        }
    }

    public void smaller(View view) {
        if (placeLocation != null) {
            radius /= 1.25;
            changeRadius();
            button = false;
            radiusEdit.setText(Integer.toString((int) radius));
            button = true;
        }
    }

    public void Enter(View view) {
        if (address != null && !date.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            EnterPressed_getUsers();
            sliding = false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_place);
        initViews();
        initMap();
    }

    @Override
    public void onBackPressed() {
        if (searchOpened) {
            closeSearch();
        } else {
            startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViews() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        UsersList = (ListView) findViewById(R.id.UsersList);
        UsersList.post(new Runnable() {
            @Override
            public void run() {
                PanelParam = (RelativeLayout.LayoutParams) UsersList.getLayoutParams();
            }
        });
        UsersContainer = findViewById(R.id.UsersContainer);
        UsersContainer.post(new Runnable() {
            @Override
            public void run() {
                slidingHeight = UsersContainer.getHeight();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        slidingPanel = findViewById(R.id.slidingPanel);
        slidingPanel.setAnchorPoint(0.5f);
        slidingPanel.post(new Runnable() {
            @Override
            public void run() {
                slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (slideOffset >= 0.5) {
                            PanelParam.height = (int) (slideOffset * (double) slidingHeight);
                        }
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        UsersList.setLayoutParams(PanelParam);
                    }
                });
            }
        });
        FrameLayout dragger = findViewById(R.id.dragger);
        dragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!sliding || toPrintPlaces.size() == 0) {
                    if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                    slidingPanel.setEnabled(false);
                } else {
                    slidingPanel.setEnabled(true);
                }
                if (searchOpened) {
                    closeSearch();
                }
                return false;
            }
        });
        UsersList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                slidingPanel.setEnabled(false);
                return false;
            }
        });
        radiusEdit = findViewById(R.id.radius);
        radiusEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (placeLocation != null && !radiusEdit.getText().toString().isEmpty() && button) {
                    radius = Double.parseDouble(radiusEdit.getText().toString());
                    changeRadius();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (placeLocation != null && !radiusEdit.getText().toString().isEmpty() && button) {
                    radius = Double.parseDouble(radiusEdit.getText().toString());
                    changeRadius();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (placeLocation != null && !radiusEdit.getText().toString().isEmpty() && button) {
                    radius = Double.parseDouble(radiusEdit.getText().toString());
                    changeRadius();
                }
            }
        });

        chooseRadius = findViewById(R.id.chooseRadius);
        animationOut(chooseRadius);
        enter = findViewById(R.id.enter);
        PlaceText = findViewById(R.id.PlaceText);
        DateText = findViewById(R.id.DateText);
        SearchFrame2 = findViewById(R.id.SearchFrame2);
        SearchFrame = findViewById(R.id.SearchFrame);
        mSearchText = findViewById(R.id.input_search);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            geoLocate();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        exit = findViewById(R.id.exit);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSearch);
        mapFragment.getMapAsync(searchByPlace.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (sliding) {
                    if (toPrintPlaces.size() == 0) {
                        sliding = false;
                    }
                    if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        if (toPrintPlaces.size() != 0) {
                            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        } else {
                            sliding = false;
                        }
                    }
                }
                if (controlsOpened) {
                    if (toPrintPlaces.size() != 0) {
                        sliding = true;
                    }
                    closeControls();
                }
            }
        });

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(2.944490, 101.602753))
                .zoom(10).build()));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchClicked(null);
            }
        }, 3000);
    }

    private void geoLocate() {
        searchString = mSearchText.getText().toString();
        if (TextUtils.isEmpty(searchString)) {
            mSearchText.setError("Location name required.");
            return;
        }

        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(searchByPlace.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            address = list.get(0);
            State = address.getAdminArea();
            City = address.getLocality();
            Address = address.getSubThoroughfare();
            if (State == null) {
                State = "unKnown";
            }
            if (City == null) {
                City = "unKnown";
            }
        }
        if (address != null) {
            searchString = toTitleCase(searchString);
            PlaceText.setText(searchString);
            if (sliding) {
                sliding = false;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 17), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    putMarker(new LatLng(address.getLatitude(), address.getLongitude()), 15, searchString);
                    if (!date.isEmpty()) {
                        if (!controlsOpened) {

//                            chooseRadius.animate().translationYBy(dpToPx(150)).setDuration(500);
                            animationIn(chooseRadius);
//                            slideViewHeight(chooseRadius, 0, dpToPx(150), 500);
                            controlsOpened = true;
                        }
                    } else {
                        showDatePickerDialog();
                    }
                }

                @Override
                public void onCancel() {

                }
            });

            if (searchOpened) {
                closeSearch();
            }
        } else {
            mSearchText.setError("Location name was not found.");
        }
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.DialogTheme,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (dayOfMonth < 10) {
            date = year + " " + getMonth(month) + " 0" + dayOfMonth;
        } else {
            date = year + " " + getMonth(month) + " " + dayOfMonth;
        }
        DateText.setText(date);
        if (sliding) {
            sliding = false;
        }
        if (address == null) {
            if (!searchOpened) {
                searchOpened = true;
                slideViewWidth(SearchFrame, dpToPx(45), (width - dpToPx(30)), 500);
                slideViewHeight(SearchFrame2, dpToPx(45), dpToPx(145), 300);
                mSearchText.setHint("Enter Place name, Address, City or Zip Code");
                exit.setVisibility(View.VISIBLE);
            }
            showSoftKeyboard(mSearchText);
        } else {
            if (searchOpened) {
                closeSearch();
            }
            if (!controlsOpened) {
                animationIn(chooseRadius);

//                slideViewHeight(chooseRadius, 0, dpToPx(150), 500);
                controlsOpened = true;
            }
        }
    }

    public void EnterPressed_getUsers() {
        if (controlsOpened) {
            closeControls();
        }
        max = 0;
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    if (dataSnapshot.getChildrenCount() > max) {
                        max = (int) dataSnapshot.getChildrenCount() + 1;
                    }
                    if (users != null) {
                        users.clear();
                    }
                    if (usersPrinted != null) {
                        usersPrinted.clear();
                    }
                    if (toPrintPlaces != null) {
                        toPrintPlaces.clear();
                    }

                    timeUsers = new boolean[max][86401];

                    for (int i = 0; i < max; i++) {
                        DataSnapshot object = dataSnapshot.child(Integer.toString(i)).child("Location History").child(date);
                        if (object.getValue() != null) {
                            for (DataSnapshot ds : object.getChildren()) {
                                if (ds.getValue() != null) {
                                    LatLng location = getLocation(ds.getValue().toString());
                                    if (distance(location, new LatLng(address.getLatitude(), address.getLongitude())) <= radius) {
                                        if (!users.contains(i)) {
                                            users.add(i);
                                        }
                                        timeUsers[i][TimeConverter(ds.getKey())] = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (users == null || users.size() <= 0) {
                    if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                    sliding = false;
                    Toast.makeText(context, "No User found", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    GetUsersInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void GetUsersInfo() {
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < users.size(); i++) {
                    if (dataSnapshot.child(Integer.toString(users.get(i))).child("name").getValue() != null) {
                        getUsersPic(Integer.toString(users.get(i)), dataSnapshot.child(Integer.toString(users.get(i))).child("name").getValue().toString(), Integer.toString(users.get(i)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getUsersPic(String id, final String name, final String Id) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(id);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    pic = Uri.fromFile(localFile);
                    toPrintPlaces.add(new PlaceUsers(pic, name, Id, getTime(Id)));
                    usersPrinted.add(Id);
                    if (toPrintPlaces.size() >= users.size()) {
                        ShowUsers();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pic = null;
                    toPrintPlaces.add(new PlaceUsers(pic, name, Id, getTime(Id)));
                    usersPrinted.add(Id);
                    if (toPrintPlaces.size() >= users.size()) {
                        ShowUsers();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ShowUsers() {
        PlaceUsersAdapter adapter = new PlaceUsersAdapter(context, R.layout.users, toPrintPlaces);
        UsersList.setAdapter(adapter);

        if (controlsOpened) {
            closeControls();
        }
        if (searchOpened) {
            closeSearch();
        }
        sliding = true;
        slidingPanel.setEnabled(true);
        if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void putMarker(LatLng latLng, float zoom, String title) {
        placeLocation = latLng;
        if (mMap != null) {
            mMap.clear();
        }

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
            circle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.argb(255, 70, 70, 70))
                    .fillColor(Color.argb(20, 0, 0, 0))
                    .radius(radius));
        }
    }

    public void openControls() {
        if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        sliding = false;
        animationIn(chooseRadius);

//        slideViewHeight(chooseRadius, 0, dpToPx(150), 500);
        controlsOpened = true;
        if (searchOpened) {
            closeSearch();
        }
    }

    public void closeControls() {
        animationOut(chooseRadius);

//        slideViewHeight(chooseRadius, dpToPx(150), 0, 500);
        controlsOpened = false;
    }

    public void closeSearch() {
        hideKeyBoard(mSearchText);
        searchOpened = false;
        slideViewWidth(SearchFrame, (width - dpToPx(30)), dpToPx(45), 300);
        slideViewHeight(SearchFrame2, dpToPx(145), dpToPx(45), 300);
        mSearchText.setHint("");
        mSearchText.setText("");
        mSearchText.clearFocus();
        mSearchText.setError(null);
        exit.setVisibility(View.GONE);
        if (sliding) {
            if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().width = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void slideViewHeight(final View view, int currentHeight, int newHeight, long duration) {

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
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        outtoBottom.setDuration(500);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }

//    public boolean isFound(String p, String hph) {
//        boolean Found = hph.indexOf(p) != -1 ? true : false;
//        return Found;
//    }

    public String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public String getMonth(int month) {
        month += 1;
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "";
    }

//    private void delay(final int time) {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    Thread.sleep(time);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    public void changeRadius() {
        circle.remove();
        circle = mMap.addCircle(new CircleOptions()
                .center(placeLocation)
                .strokeColor(Color.argb(255, 70, 70, 70))
                .fillColor(Color.argb(20, 0, 0, 0))
                .radius(radius));
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

    public LatLng getLocation(String LocationString) {
        try {
            String[] arr = LocationString.split(",", -1);
            return new LatLng(getDouble(arr[0]), getDouble(arr[1]));
        } catch (Exception ignored) {
            return new LatLng(0, 0);
        }
    }

    public double getDouble(String latlng) {
        try {
            return Double.parseDouble(latlng);
        } catch (Exception ignored) {
            return 0D;
        }
    }

    public int TimeConverter(String unixString) {
        try {
            long unixSeconds = Long.parseLong(unixString);
            int seconds = 0;
            Date date = new java.util.Date(unixSeconds * 1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
            String formattedDate = sdf.format(date);
            String[] arrOfStr = formattedDate.split(":", -1);
            for (int i = 0; i < arrOfStr.length; i++) {
                if (i == 0) {
                    seconds = Integer.parseInt(arrOfStr[i]) * 60 * 60;
                } else if (i == 1) {
                    seconds += Integer.parseInt(arrOfStr[i]) * 60;
                } else if (i == 2) {
                    seconds += Integer.parseInt(arrOfStr[i]);
                }
            }
            return seconds;
        } catch (Exception ignored) {
            return 0;
        }
//        try {
//            long unix = Long.parseLong(unixString) * 1000;
//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.HOUR_OF_DAY, 0);
//            c.set(Calendar.MINUTE, 0);
//            c.set(Calendar.SECOND, 0);
//            c.set(Calendar.MILLISECOND, 0);
//            return Math.max((int) ((c.getTimeInMillis() - unix) / 1000), 0);
//        } catch (Exception ignored) {
//            return 0;
//        }
    }

    public String getTime(String id) {
        try {
            int idInt = Integer.parseInt(id);
            boolean firstTime = false;
            int counter = 0, lastInt = 0;
            String start = "";
            StringBuilder TimeString = new StringBuilder();
            for (int i = 0; i < 86401; i++) {
                if (timeUsers[idInt][i]) {
                    counter = 0;
                    if (start.isEmpty()) {
                        start = SecondsToTime(i);
                    }
                    lastInt = i;
                } else {
                    if (!start.isEmpty()) {
                        counter++;
                        if (counter >= 3600 || (i >= 86400 && lastInt > 0)) {
                            if (firstTime) {
                                TimeString.append("\n");
                            }else{
                                firstTime = true;
                            }
                            TimeString.append(start).append(" - ").append(SecondsToTime(lastInt));
                            lastInt = 0;
                            start = "";
                        }
                    }
                }
            }
            return TimeString.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    public String SecondsToTime(int seconds) {
        String secondsString = "", minutesString = "", hoursString = "";
        int minutes = 0;
        int hours = 0;
        while (seconds > 59) {
            minutes += 1;
            seconds -= 60;
        }
        while (minutes > 59) {
            hours += 1;
            minutes -= 60;
        }
        if (Integer.toString(seconds).length() < 2) {
            secondsString = "0" + seconds;
        } else {
            secondsString = Integer.toString(seconds);
        }

        if (Integer.toString(minutes).length() < 2) {
            minutesString = "0" + minutes;
        } else {
            minutesString = Integer.toString(minutes);
        }

        String ampm = "AM";
        if (hours >= 12) {
            ampm = "PM";
        }
        if (hours > 12) {
            hours -= 12;
        }

        if (Integer.toString(hours).length() < 2) {
            if (hours == 0) {
                hoursString = "12";
            } else {
                hoursString = "0" + hours;
            }
        } else {
            hoursString = Integer.toString(hours);
        }
        return (hoursString + ":" + minutesString + ":" + secondsString + " " + ampm);
    }

//    public LatLng GetLocFromString(String location) {
//        String temp = "";
//        Matcher m = Pattern.compile("\\*([^\\*]+)\\*").matcher(location);
//        while (m.find()) {
//            if (m.group(1) != null) {
//                if (temp.isEmpty()) {
//                    temp = m.group(1);
//                } else {
//                    return new LatLng(Double.parseDouble(temp), Double.parseDouble(m.group(1)));
//                }
//            }
//        }
//        return null;
//    }

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
}
