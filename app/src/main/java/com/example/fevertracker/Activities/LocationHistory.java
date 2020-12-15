package com.example.fevertracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Person;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fevertracker.Adapters.DatesAdapter;
import com.example.fevertracker.Adapters.PlacesAdapter;
import com.example.fevertracker.Classes.Dates;
import com.example.fevertracker.Classes.LocationClass;
import com.example.fevertracker.Classes.PlacesClass;
import com.example.fevertracker.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.fevertracker.OldClasses.LocationActivity.SHARED_PREFS;

public class LocationHistory extends AppCompatActivity implements OnMapReadyCallback {


    //Views
    View oldView;
    FrameLayout dragger, DatesListFrame, exit;
    GoogleMap googleMap;
    ProgressBar progressBar;
    TextView chooseDateText;
    ImageView ChooseDateArrow, search_bar;
    LinearLayout selectDatesButton;
    AppCompatAutoCompleteTextView autoTextView;
    RelativeLayout Panel,locationsContainer;
    Toast dateLodingToast, doneLoadingToast;
    ListView DatesList, PlacesListView;
    SlidingUpPanelLayout slidingPanel;
    RelativeLayout.LayoutParams PanelParam;

    //Variables
    LocationClass locationsToBePrinted = new LocationClass();
    ArrayList<String> datesToBeArranged = new ArrayList<>();
    ArrayList<Dates> Dates = new ArrayList<>();
    ArrayList<PlacesClass> toPrintPlaces = new ArrayList<>();
    ArrayList<Polyline> road = new ArrayList<>();
    ArrayList<Marker> locationStops = new ArrayList<>();
    PlacesAdapter adapter;
    String PlaceName = "", Address = "";
    String oldChosenDate = "";
    String chosenDate = "";
    double panelHeight;
    public static int[] colour;
    public static int circleRange = 500;
    public static int maxInSec = 300;
    int height = 0, heightPanel = 0;
    int lastPos = -1, lastLoc = -1, PreviousView = -1;
    boolean doTaskAgain = true, firstStop = true;
    boolean drag;
    boolean controls = false;
    boolean Looding = false;
    boolean doubleBackToExitPressedOnce = false;
    boolean popupSelectDates = false, searchBar = false;
    boolean loading = true;


    public void update(View view) {
        if (controls) {
            progressBar.setVisibility(View.VISIBLE);
            getData();
        }
    }

    public void show(View view) {
    }

    public void Select(View view, int position) {
        if (PreviousView != -1) {
            adapter.setViewColor(PreviousView);
            oldView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            colour[PreviousView] = 0;
            deselectRoad();
            deselectPos();
        }

        if (PreviousView != position) {
            view.setBackgroundColor(Color.LTGRAY);
            colour[position] = 1;
            PreviousView = position;
            oldView = view;
            if (position % 2 == 0) {
                selectPos(position);
            } else {
                selectRoad(position);
            }
        } else {
            PreviousView = -1;
            setCamera();
        }
    }

    public void selectRoad(int pos) {
        lastPos = pos;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int i;
        for (i = convertor(pos); i < convertor(pos + 1); i++) {
            road.get(i).remove();
            road.set(i, googleMap.addPolyline(new PolylineOptions().add(locationsToBePrinted.LocationsCompressed.get(i)).add(locationsToBePrinted.LocationsCompressed.get(i + 1)).width(10f).color(Color.RED)));
            builder.include(locationsToBePrinted.LocationsCompressed.get(i));
            if (i > convertor(pos)) {

                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(locationsToBePrinted.LocationsCompressed.get(i).latitude);
                prevLoc.setLongitude(locationsToBePrinted.LocationsCompressed.get(i).longitude);

                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(locationsToBePrinted.LocationsCompressed.get(i + 1).latitude);
                newLoc.setLongitude(locationsToBePrinted.LocationsCompressed.get(i + 1).longitude);

                float bearing = prevLoc.bearingTo(newLoc);

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.playred);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.get(i).remove();
                locationStops.set(i, googleMap.addMarker(new MarkerOptions()
                        .position(locationsToBePrinted.LocationsCompressed.get(i))
                        .title(locationsToBePrinted.timeTitle.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
            }
        }
        builder.include(locationsToBePrinted.LocationsCompressed.get(i));
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    public void selectPos(int pos) {
        int locPos = convertor(pos);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stop);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap LocationStop = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
        locationStops.get(locPos).remove();
        locationStops.set(locPos, googleMap.addMarker(new MarkerOptions()
                .position(locationsToBePrinted.LocationsCompressed.get(locPos))
                .icon(BitmapDescriptorFactory.fromBitmap(LocationStop)).title(locationsToBePrinted.timeTitle.get(locPos)).anchor(0.5f, 0.5f)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationsToBePrinted.LocationsCompressed.get(locPos), 15));
        lastLoc = pos;
    }

    public void deselectRoad() {
        if (lastPos != -1) {
            for (int i = convertor(lastPos); i < convertor(lastPos + 1); i++) {
                road.get(i).remove();
                road.set(i, googleMap.addPolyline(new PolylineOptions().add(locationsToBePrinted.LocationsCompressed.get(i)).add(locationsToBePrinted.LocationsCompressed.get(i + 1)).width(8f).color(Color.BLUE)));
                if (i > convertor(lastPos)) {
                    Location prevLoc = new Location("service Provider");
                    prevLoc.setLatitude(locationsToBePrinted.LocationsCompressed.get(i).latitude);
                    prevLoc.setLongitude(locationsToBePrinted.LocationsCompressed.get(i).longitude);

                    Location newLoc = new Location("service Provider");
                    newLoc.setLatitude(locationsToBePrinted.LocationsCompressed.get(i + 1).latitude);
                    newLoc.setLongitude(locationsToBePrinted.LocationsCompressed.get(i + 1).longitude);

                    float bearing = prevLoc.bearingTo(newLoc);

                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.play);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                    locationStops.get(i).remove();
                    locationStops.set(i, googleMap.addMarker(new MarkerOptions()
                            .position(locationsToBePrinted.LocationsCompressed.get(i))
                            .title(locationsToBePrinted.timeTitle.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
                }
            }
            lastPos = -1;
        }
    }

    public void deselectPos() {
        if (lastLoc != -1) {
            int locPos = convertor(lastLoc);
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stopblue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap LocationStop = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
            locationStops.get(locPos).remove();
            locationStops.set(locPos, googleMap.addMarker(new MarkerOptions()
                    .position(locationsToBePrinted.LocationsCompressed.get(locPos))
                    .icon(BitmapDescriptorFactory.fromBitmap(LocationStop)).title(locationsToBePrinted.timeTitle.get(locPos)).anchor(0.5f, 0.5f)));
            lastLoc = -1;
        }
    }

    public void setCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int x = 0; x < locationsToBePrinted.LocationsCompressed.size(); x++) {
            builder.include(locationsToBePrinted.LocationsCompressed.get(x));
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    public void getAddress(LatLng Marker) {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(Marker.latitude, Marker.longitude, 1);
            if (addresses.size() > 0) {
                PlaceName = addresses.get(0).getLocality();
                if (PlaceName == null) {
                    PlaceName = "";
                }
                Address = CheckNullString(addresses.get(0).getPostalCode()).trim() + " " + PlaceName + " " + CheckNullString(addresses.get(0).getSubThoroughfare()).trim() + ","
                        + CheckNullString(addresses.get(0).getAdminArea()).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int convertor(int pos) {
        if (pos > 0) {
            if (pos % 2 != 0) pos--;
            pos /= 2;
        }
        pos++;
        int i;
        for (i = 0; pos > 0 && i < locationsToBePrinted.isStop.size(); i++) {
            if (locationsToBePrinted.isStop.get(i)) {
                pos--;
            }
        }
        i--;
        return i;
    }

    public String getCoordenates(LatLng loc) {
        String lat = Double.toString(loc.latitude), lon = Double.toString(loc.longitude);
        if (lat.length() > 10) {
            lat = lat.substring(0, 10);
        }
        if (lon.length() > 10) {
            lon = lon.substring(0, 10);
        }
        return lat + ", " + lon;
    }

    public void locationfinalAnalizer() {
        if (googleMap != null) {
            googleMap.clear();
        }
        toPrintPlaces.clear();
        locationStops.clear();
        road.clear();
        firstStop = true;
        for (int i = 0; i < locationsToBePrinted.LocationsCompressed.size(); i++) {
            if (locationsToBePrinted.isStop.get(i)) {
                if (firstStop) {
                    firstStop = false;
                } else {
                    toPrintPlaces.add(new PlacesClass("", "", "", locationsToBePrinted.timeTaken.get(i), locationsToBePrinted.distance.get(i), ""));
                }
                getAddress(locationsToBePrinted.LocationsCompressed.get(i));
                toPrintPlaces.add(new PlacesClass(PlaceName, locationsToBePrinted.timeTitle.get(i), Address, "", "", getCoordenates(locationsToBePrinted.LocationsCompressed.get(i))));
                CircleOptions circleOptions = new CircleOptions()
                        .center(locationsToBePrinted.LocationsCompressed.get(i))
                        .strokeColor(Color.argb(0, 70, 70, 70))
                        .fillColor(Color.argb(50, 150, 150, 150))
                        .radius(circleRange);
                googleMap.addCircle(circleOptions);
            }
            if (i == locationsToBePrinted.LocationsCompressed.size() - 1) {
                PutAMarker(locationsToBePrinted.LocationsCompressed.get(i), null, locationsToBePrinted.timeTitle.get(i), locationsToBePrinted.isStop.get(i));
            } else {
                PutAMarker(locationsToBePrinted.LocationsCompressed.get(i), locationsToBePrinted.LocationsCompressed.get(i + 1), locationsToBePrinted.timeTitle.get(i), locationsToBePrinted.isStop.get(i));
            }
            if (i > 0) {
                road.add(googleMap.addPolyline(new PolylineOptions().add(locationsToBePrinted.LocationsCompressed.get(i - 1)).add(locationsToBePrinted.LocationsCompressed.get(i)).width(8f).color(Color.BLUE)));
            }
        }

        int size = toPrintPlaces.size();
        colour = new int[size];
        for (int i = 0; i < size; i++) {
            colour[i] = 0;
        }

        adapter = new PlacesAdapter(getApplicationContext(), R.layout.location_stops_layout, toPrintPlaces, size);
        adapter.setLocationHistory(this);
        PlacesListView.setAdapter(adapter);

        setCamera();

        lastPos = -1;
        lastLoc = -1;
        PreviousView = -1;

        PlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Select(view, position);
            }
        });

        slidingPanel.setEnabled(true);
        if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.ANCHORED) {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void exit(View view) {
        exit.setVisibility(View.GONE);
        selectDatesButton.setVisibility(View.VISIBLE);
        autoTextView.setVisibility(View.GONE);
        autoTextView.setText("");
        rotate(ChooseDateArrow, false);
        hideKeyBoard(autoTextView);
        slideViewHeihgt(DatesListFrame, DatesListFrame.getHeight(), 0, 300);
        popupSelectDates = false;
        search_bar.setImageResource(R.drawable.ic_search_black_24dp);
        searchBar = false;
    }

    public void search_bar(View view) {
        if (!searchBar) {
            selectDatesButton.setVisibility(View.GONE);
            autoTextView.setVisibility(View.VISIBLE);
            exit.setVisibility(View.VISIBLE);
            if (slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                showSoftKeyboard(autoTextView);
            }
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            search_bar.setImageResource(R.drawable.ic_close_black_24dp);
            searchBar = true;
            updateAutoText();
        } else {
            selectDatesButton.setVisibility(View.VISIBLE);
            autoTextView.setVisibility(View.GONE);
            autoTextView.setText("");
            rotate(ChooseDateArrow, false);
            hideKeyBoard(autoTextView);
            slideViewHeihgt(DatesListFrame, DatesListFrame.getHeight(), 0, 300);
            popupSelectDates = false;
            search_bar.setImageResource(R.drawable.ic_search_black_24dp);
            searchBar = false;
        }
    }

    public void popup(View view) {
        if (!loading) {
            if (!popupSelectDates) {
                exit.setVisibility(View.VISIBLE);
                rotate(ChooseDateArrow, true);
                slideViewHeihgt(DatesListFrame, 0, heightPanel, 300);
                popupSelectDates = true;
            } else {
                rotate(ChooseDateArrow, false);
                exit.setVisibility(View.GONE);
                slideViewHeihgt(DatesListFrame, DatesListFrame.getHeight(), 0, 300);
                popupSelectDates = false;
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "ShowToast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_for_admin);

        DatesListFrame = findViewById(R.id.DatesListFrame);
        PlacesListView = findViewById(R.id.PlacesListView);
        ChooseDateArrow = findViewById(R.id.ChooseDateArrow);
        slidingPanel = findViewById(R.id.slidingPanel);
        slidingPanel.setAnchorPoint(0.3f);
        dragger = findViewById(R.id.dragger);
        locationsContainer = findViewById(R.id.locationsContainer);
        dragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                slidingPanel.setEnabled(true);
                return false;
            }
        });

        locationsContainer.post(new Runnable() {
            @Override
            public void run() {
                height = locationsContainer.getHeight();
            }
        });

        PlacesListView.post(new Runnable() {
            @Override
            public void run() {
                PanelParam = (RelativeLayout.LayoutParams) PlacesListView.getLayoutParams();
            }
        });

        PlacesListView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                slidingPanel.setEnabled(false);
                return false;
            }
        });

        Panel = findViewById(R.id.Panel);
        drag = false;
        slidingPanel.post(new Runnable() {
            @Override
            public void run() {
                slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (slideOffset >= 0.3) {
                            PanelParam.height = (int) (slideOffset * (double) height);
                        }
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        PlacesListView.setLayoutParams(PanelParam);
                    }
                });
            }
        });
        panelHeight = 0;
        chooseDateText = findViewById(R.id.chooseDateText);
        exit = findViewById(R.id.exit);
        selectDatesButton = findViewById(R.id.selectDatesButton);
        search_bar = findViewById(R.id.search_bar);
        progressBar = findViewById(R.id.progressBar);
        autoTextView = findViewById(R.id.searchBar);
        DatesList = findViewById(R.id.DatesList);
        doneLoadingToast = Toast.makeText(getApplicationContext(), "Done loading", Toast.LENGTH_LONG);
        getData();
        initMap();
    }

    public void getData() {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("Location History").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Dates.clear();
                    datesToBeArranged.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey() != null) {
                            datesToBeArranged.add(ds.getKey());
                        }
                    }

                    Collections.sort(datesToBeArranged, new Comparator<String>() {
                        @SuppressLint("SimpleDateFormat")
                        final DateFormat df = new SimpleDateFormat("yyyy MMM dd");

                        @Override
                        public int compare(String s1, String s2) {
                            try {
                                return df.parse(s2).compareTo(df.parse(s1));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });

                    for (int i = 0; i < datesToBeArranged.size(); i++) {
                        Dates.add(new Dates(datesToBeArranged.get(i)));
                    }

                    if (Dates.size() > 5) {
                        heightPanel = 5 * dpToPx(41);
                    } else {
                        heightPanel = Dates.size() * dpToPx(41);
                    }

                    DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, Dates);
                    DatesList.setAdapter(adapter);
                    autoTextView.setAdapter(adapter);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading = false;
                            progressBar.setVisibility(View.GONE);
                            popupSelectDates = false;
                            popup(null);
                        }
                    }, 10);
                    SearchInit();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "There is no data saved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
    }

    public void SearchInit() {
        autoTextView.setThreshold(1); //will start working from first character
        DatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenDate = Dates.get(position).getDate();
                if (chosenDate.isEmpty() && oldChosenDate.isEmpty()) {//fist time only
                    doTaskAgain = true;
                } else {
                    doTaskAgain = oldChosenDate.compareTo(chosenDate) != 0;
                }
                if (doTaskAgain) {
                    dateLodingToast = Toast.makeText(getApplicationContext(), chosenDate + " is loading", Toast.LENGTH_LONG);
                    chooseDateText.setText(chosenDate);

                    progressBar.setVisibility(View.VISIBLE);
                    dateLodingToast.show();
                    exit(null);

                    Looding = true;
                    slidingPanel.setEnabled(true);
                    if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                    if (googleMap != null) {
                        googleMap.clear();
                    }


                    dateChoosed();
                }
            }
        });
        autoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateAutoText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateAutoText();
            }
        });
    }

    public void updateAutoText() {
        Dates.clear();
        if (TextUtils.isEmpty(autoTextView.getText())) {
            slideView(DatesListFrame, DatesListFrame.getLayoutParams().height, 5 * dpToPx(41), 100L);
            for (int s = 0; s < datesToBeArranged.size(); s++) {
                Dates.add(new Dates(datesToBeArranged.get(s)));
            }
            DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, Dates);
            DatesList.setAdapter(adapter);
        } else {
            boolean found = false;
            for (int x = 0; x < datesToBeArranged.size(); x++) {
                if (isFound(autoTextView.getText().toString().toLowerCase(), datesToBeArranged.get(x).toLowerCase())) {
                    Dates.add(new Dates(datesToBeArranged.get(x)));
                    found = true;
                }
            }
            int size = Dates.size();
            if (size > 5) {
                size = 5;
            }
            int height = size * dpToPx(41);
            slideView(DatesListFrame, DatesListFrame.getLayoutParams().height, height, 300L);
            if (!found) {
                slideView(DatesListFrame, DatesListFrame.getLayoutParams().height, 0, 100L);
            }
            DatesAdapter adapter = new DatesAdapter(getApplicationContext(), R.layout.dates_layout, Dates);
            DatesList.setAdapter(adapter);
        }
    }

    private void dateChoosed() {
        new Thread(new Runnable() {
            public void run() {
                locationAnalizer();
            }
        }).start();
    }

    public void locationAnalizer() {
        oldChosenDate = chosenDate;
        locationsToBePrinted.clear();

        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("Location History").child(chosenDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey() != null && ds.getValue() != null) {
                            locationsToBePrinted.add(ds.getKey(), ds.getValue().toString());
                        }
                    }
                    locationsToBePrinted.compute();
                    mainThread();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void mainThread() {
        LocationHistory.this.runOnUiThread(new Runnable() {
            public void run() {
                if (dateLodingToast != null) {
                    dateLodingToast.cancel();
                }
                locationfinalAnalizer();
                doneLoadingToast.show();
                Looding = false;
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void PutAMarker(LatLng currentLatLng, LatLng newLocation, String title, boolean isStop) {
        if (isStop) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.stopblue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(20), dpToPx(20), false);
            locationStops.add(googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f)));
        } else {
            float bearing = -1;
            if (newLocation != null) {
                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(currentLatLng.latitude);
                prevLoc.setLongitude(currentLatLng.longitude);

                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(newLocation.latitude);
                newLoc.setLongitude(newLocation.longitude);

                bearing = prevLoc.bearingTo(newLoc);
            }
            if (bearing != -1) {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.play);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.add(googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f).rotation(bearing)));
            } else {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.rec);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, dpToPx(15), dpToPx(15), false);
                locationStops.add(googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).anchor(0.5f, 0.5f)));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap2) {
        googleMap = googleMap2;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyBoard(autoTextView);
                slidingPanel.setEnabled(true);
                if (slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }
        });

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(2.944490, 101.602753))
                .zoom(10).build()));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (!Looding) {
            finish();
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Application is Loading! click BACK again to exit.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void slideViewHeihgt(final View view, int currentHeight, int newHeight, long duration) {

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

    public String CheckNullString(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

    public void rotate(View view, boolean up) {
        RotateAnimation rotate;
        if (up) {
            rotate = new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public boolean isFound(String p, String hph) {
        boolean Found = hph.indexOf(p) != -1 ? true : false;
        return Found;
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
