package com.example.fevertracker.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.fevertracker.BroadcastReceivers.NotificationBroadcastReceiver;
import com.example.fevertracker.Libraries.GeofenceHelper;
import com.example.fevertracker.OldClasses.LocationActivity;
import com.example.fevertracker.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.fevertracker.Notifications.Notification.CHANNEL_1_ID;

public class LocationService extends Service {

    Context context2 = this;
    private static final String TAG = "LocationActivity";
    public long INTERVAL = 500000; //5 seconds
    public long FASTEST_INTERVAL = 500000; // 5 seconds
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    //  String mLastUpdateTime;
    String NewLoc = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    String Status = "1", oldStatus = "1", oldCity = "";
    boolean firstTime = false;
    boolean timeIn = false, pushed = true, dataCleared = false;
    private NotificationManagerCompat notificationManager;
    boolean firstTimeNot = true;
//    String State = "", City = "", Address = "";
//    Intent notificationIntent;
    public Timer timer;
    public TimerTask timerTask;
    long period = 1000000;
    Loc loc = new Loc();
    boolean networkDis = false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!loadData("Id").isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("IsOnline").setValue(true);
        }
        createLocationRequest();
        startTimer();
        FireBaseListenner();
//        notificationManager = NotificationManagerCompat.from(this);
//        Intent notificationIntent = new Intent(this, LocationActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Fever Tracker")
//                .setContentText("Keep the app running for any announcement")
//                .setSmallIcon(R.drawable.ic_launcher_round)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChannel = new NotificationChannel(
                    "serviceNotificationChannelId",
                    "Hidden Notification Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notifManager != null) {
                notifManager.createNotificationChannel(serviceChannel);
            }

            Intent hidingIntent = new Intent(this, NotificationBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1,
                    hidingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification notification = new Notification.Builder(this, "serviceNotificationChannelId")
                    .setContentTitle("Hide Notification Example")
                    .setContentText("To hide me, click and uncheck \"Hidden Notification Service\"")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .getNotification();
            startForeground(1, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(this);
            this.startForeground(-1, builder.getNotification());
            stopSelf();
        }
//        notificationIntent = new Intent(this, LocationActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.enableVibration(true);
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
//        } else {
//            notificationBuilder = new NotificationCompat.Builder(this);
//        }
//        notificationBuilder
////              .setContentTitle(notification.getTitle())
//                .setContentText(String.format("R.string.workfield_driver_refuse"))
//                //.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
//                .setAutoCancel(true)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentIntent(pendingIntent)
//                .setLargeIcon(icon)
//                .setColor(Color.RED)
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//        notificationBuilder.setDefaults(DEFAULT_VIBRATE);
//        notificationBuilder.setLights(Color.YELLOW, 1000, 300);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notificationBuilder.build());
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TAG,
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Fever Tracker")
//                    .setContentText("Keep the app running for any announcement")
//                    .setSmallIcon(R.drawable.ic_launcher_round)
//                    .setContentIntent(pendingIntent)
//                    .build();
//            startForeground(1, notification);
//        } else {
//
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Fever Tracker")
//                    .setContentText("Keep the app running for any announcement")
//                    .setSmallIcon(R.drawable.ic_launcher_round)
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//            startForeground(1, notification);
//
//            // startForeground(1, notification);
//        }
//        notificationManager.cancelAll();
        return START_NOT_STICKY;
    }


    public void push() {
        if (!pushed && mCurrentLocation != null) {
            final String Id = loadData("Id");
            NewLoc = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," + Status;
            if (!Id.isEmpty() && !Status.isEmpty()) {
                long milliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
                Date resultdate = new Date(milliseconds);
//                getAddress();
//                if (!State.isEmpty() && !City.isEmpty()) {
                if (IsNetworkEnabled()) {
//                    long secondsPassed;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                        ZonedDateTime nowZoned = ZonedDateTime.now();
//                        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
//                        Duration duration = Duration.between(midnight, Instant.now());
//                        secondsPassed = duration.getSeconds();
//                    } else {
//                        Calendar c = Calendar.getInstance();
//                        long now = c.getTimeInMillis();
//                        c.set(Calendar.HOUR_OF_DAY, 0);
//                        c.set(Calendar.MINUTE, 0);
//                        c.set(Calendar.SECOND, 0);
//                        c.set(Calendar.MILLISECOND, 0);
//                        long passed = now - c.getTimeInMillis();
//                        secondsPassed = passed / 1000;
//                    }

//                    if (networkDis) {
//                        networkDis = false;
//                        saveData(NewLoc, Long.toString(milliseconds / 1000L));
//                        pushData(Id);
//                    }
                    FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("IsOnline").setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Member/" + Id + "/Location History/" + sdf.format(resultdate) + "/" + (milliseconds / 1000)).setValue(NewLoc);
//                    FirebaseDatabase.getInstance().getReference().child("Location/" + Id + "/Location History/" + sdf.format(resultdate) + "/" + State + "/" + City + "/" + (milliseconds / 1000L)).setValue(NewLoc).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            e.printStackTrace();
//                            // did not set value
//                            Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                if (!City.isEmpty()) {
                    String currLoc = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," + Status + "," + milliseconds / 1000L;
                    FirebaseDatabase.getInstance().getReference().child("Member/" + Id + "/CurrLocation").setValue(currLoc);
//                }
//                    Toast.makeText(getApplicationContext(),statuddd,Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();
//                    networkDis = true;
//                    saveData(NewLoc, Long.toString(milliseconds / 1000L));
                }
                pushed = true;
            }
        }
    }

//    public void getAddress() {
//        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                State = addresses.get(0).getAdminArea().trim();
//                City = addresses.get(0).getLocality().trim();
////                Address = addresses.get(0).getSubThoroughfare().trim();
//                if (State == null || State.isEmpty()) {
//                    State = "unKnown";
//                }
//                if (City == null || City.isEmpty()) {
//                    City = "unKnown";
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void FireBaseListenner() {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("geofencing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("location").getValue() != null && dataSnapshot.child("radius").getValue() != null) {
                    setGeoFence(getLoc(dataSnapshot.child("location").getValue()), getRadius(dataSnapshot.child("radius").getValue()));
                } else {
                    if (geofencingClient != null) {
                        Toast.makeText(context2, "Geofence Removed", Toast.LENGTH_LONG).show();
                        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent()); // Result processed in onResult().
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    status(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().toString().isEmpty()) {
                    Intent notificationIntent = new Intent(context2, LocationActivity.class);
                    saveData("n", "not");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context2,
                            0, notificationIntent, 0);


                    if (dataSnapshot.getValue() != null) {
                        NotificationCompat.Builder notificationBuilder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationBuilder = new NotificationCompat.Builder(context2, "channel5");
                            NotificationChannel notificationChannel = new NotificationChannel("channel5", TAG, NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.enableVibration(true);
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                        } else {
                            notificationBuilder = new NotificationCompat.Builder(context2);
                        }
                        notificationBuilder
                                .setSmallIcon(R.drawable.ic_launcher_round)
                                .setContentTitle("Fever Tracker")
                                .setContentText(dataSnapshot.child("Notification").getValue().toString())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2, notificationBuilder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("getLoc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    if (isNumric(dataSnapshot.getValue().toString())) {
//                        timer.cancel();
//                        timer = new Timer();
//                        timerTask = new TimerTask() {
//                            public void run() {
                loc.create();
//                            }
//                        };
//                        timer.scheduleAtFixedRate(timerTask, 0, Long.parseLong(dataSnapshot.getValue().toString()) * 1000); //
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!firstTimeNot) {
                    Intent notificationIntent = new Intent(context2, LocationActivity.class);
                    saveData("n", "not");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context2,
                            0, notificationIntent, 0);
                    if (dataSnapshot.getValue() != null) {
                        NotificationCompat.Builder notificationBuilder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationBuilder = new NotificationCompat.Builder(context2, CHANNEL_1_ID);
                            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_1_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.enableVibration(true);
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                        } else {
                            notificationBuilder = new NotificationCompat.Builder(context2);
                        }
                        notificationBuilder
                                .setSmallIcon(R.drawable.ic_launcher_round)
                                .setContentTitle("Fever Tracker")
                                .setContentText(dataSnapshot.getValue().toString())
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2, notificationBuilder.build());
                    }
                }
                firstTimeNot = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public LatLng getLoc(Object loc) {
        try {
            String[] arr = loc.toString().split(",", -1);
            return new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
        } catch (Exception ignored) {
            return null;
        }
    }

    public float getRadius(Object radius) {
        try {
            return Float.parseFloat(radius.toString());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    public void setGeoFence(LatLng latLng, float radius) {
        if (latLng != null && radius != 0) {
            geofencingClient = LocationServices.getGeofencingClient(this);
            geofenceHelper = new GeofenceHelper(this);

            Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context2, "Geofence Added...", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "onSuccess: Geofence Added...");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            Toast.makeText(context2, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.d("TAG", "onFailure: " + errorMessage);
                        }
                    });
        }
    }

    public void status(String status) {
        try {
            if (Integer.parseInt(status) == 1) {
                Status = "1";
            }
            if (Integer.parseInt(status) == 2) {
                Status = "2";
            }
            if (Integer.parseInt(status) == 3) {
                Status = "3";
            }
        } catch (Exception ignored) {
        }
    }

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
//                loc.create();
            }
        };
        timer.schedule(timerTask, 0, 3600000);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(context2)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(loc)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!loadData("Id").isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("IsOnline").setValue(false);
        }
    }

    public class Loc implements LocationListener, GoogleApiClient.ConnectionCallbacks {
        public void create() {

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            pushed = false;
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
            mCurrentLocation = location;
            mGoogleApiClient.disconnect();
            push();
        }
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

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }
}