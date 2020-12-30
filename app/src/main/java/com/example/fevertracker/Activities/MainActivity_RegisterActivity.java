package com.example.fevertracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fevertracker.Activities.Admin.DashboardAdmin;
import com.example.fevertracker.Classes.Member;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.User.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity_RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private static final int REQUEST_ENABLE_BT = 110;
    private BluetoothAdapter mBTAdapter;
    public static String DeviceAdress="";

    long maxid = 1;
    LinearLayout linearLayout, forLogin;
    TextView enterTitle,forgotText;
    EditText nameE, emailE, phoneE, passportE, addressE, passE, confirmpassE, focused;
    FrameLayout name, email, phone, passport, address, pass, confirmpass, forReg;
    ScrollView scrollView;
    ImageView back;
    //    ImageView background;
    DatabaseReference reff;
    Context context=this;
    Member member;
    Button reglog;
    Boolean register = false;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    boolean fail = false;
    String AdminName = "", AdminPassword = "";
    int PERMISSION_ID = 44;
    public static final String SHARED_PREFS = "sharedPrefs";
    DocumentReference noteRef;
    Activity activity = this;

    public void onback(View view) {
        onBackPressed();
    }

    public void Forgot(View view){
        Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
        intent.putExtra("EmailAddress", emailE.getText().toString());
        startActivity(intent);
    }

    public void Register(View view) {
//        if (checkPermissions()) {
            if (register) {
                final String name = nameE.getText().toString().trim();
                final String email = emailE.getText().toString().trim();
                final String phone = phoneE.getText().toString().trim();
                final String address = addressE.getText().toString().trim();
                final String passport = passportE.getText().toString().trim();
                String password = passE.getText().toString().trim();
                String ConfinrmPassword = confirmpassE.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    nameE.setError("Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailE.setError("Email address is Required.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    phoneE.setError("Phone is Required.");
                    return;
                }

                if (TextUtils.isEmpty(passport)) {
                    passportE.setError("Passport number is Required.");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    addressE.setError("Address is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passE.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    passE.setError("Password Must be >= 6 Characters");
                    return;
                }

                if (password.compareTo(ConfinrmPassword) != 0) {
                    confirmpassE.setError("Password is not similar.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity_RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            reff.child(String.valueOf(maxid + 1)).child("name").setValue(name);
                            reff.child(String.valueOf(maxid + 1)).child("email").setValue(email);
                            reff.child(String.valueOf(maxid + 1)).child("phone").setValue(phone);
                            reff.child(String.valueOf(maxid + 1)).child("address").setValue(address);
                            reff.child(String.valueOf(maxid + 1)).child("passport").setValue(passport);
                            reff.child(String.valueOf(maxid + 1)).child("state").setValue("1");


                            user.put("Id", Long.toString(maxid + 1));
                            saveData(Long.toString(maxid + 1), "Id");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fail = false;
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                    progressBar.setVisibility(View.GONE);
                                    fail = true;
                                }
                            });
                            if (!fail) {
                                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity_RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }


                });
            } else {

                final String email = emailE.getText().toString().trim();
                String password = passE.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    emailE.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passE.setError("Password is Required.");
                    return;
                }

                if (password.compareTo(AdminPassword) == 0 && email.compareTo(AdminName) == 0) {
                    startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
                    finish();
                    return;
                }

                if (password.length() < 6) {
                    passE.setError("Password Must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveId();
                        } else {
                            Toast.makeText(MainActivity_RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
//        } else {
//            Toast.makeText(this, "Please accept permission first.", Toast.LENGTH_LONG).show();
//        }
    }

    public void Login(View view) {
        clearFocus();
        if (register) {
            name.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
            passport.setVisibility(View.GONE);
            confirmpass.setVisibility(View.GONE);
            forgotText.setVisibility(View.VISIBLE);
            emailE.setText("");
            passE.setText("");
            emailE.setError(null);
            passE.setError(null);
            emailE.clearFocus();
            passE.clearFocus();
            emailE.setHint("Email address");
            passE.setHint("Password");
            enterTitle.setText("LOGIN");
            linearLayout.setVisibility(View.VISIBLE);
//            Title.setVisibility(View.VISIBLE);
            forReg.setVisibility(View.GONE);
            forLogin.setVisibility(View.VISIBLE);
            register = false;

        } else {
            name.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            passport.setVisibility(View.VISIBLE);
            confirmpass.setVisibility(View.VISIBLE);
            forgotText.setVisibility(View.GONE);

            nameE.setText("");
            nameE.clearFocus();
            nameE.setError(null);

            emailE.setText("");
            emailE.clearFocus();
            emailE.setError(null);

            phoneE.setText("");
            phoneE.clearFocus();
            phoneE.setError(null);

            addressE.setText("");
            addressE.clearFocus();
            addressE.setError(null);

            passportE.setText("");
            passportE.clearFocus();
            passportE.setError(null);

            confirmpassE.setText("");
            confirmpassE.clearFocus();
            confirmpassE.setError(null);

            passE.setText("");
            passE.clearFocus();
            passE.setError(null);

            linearLayout.setVisibility(View.GONE);
            emailE.setHint("Email address (Must remember)");
            passE.setHint("Password (Must remember)");
            forReg.setVisibility(View.VISIBLE);
            forLogin.setVisibility(View.GONE);
            enterTitle.setText("CREATE");
            register = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        boolean permissions = checkPermissions();

        if (!permissions) {
            requestPermissions();
        }else{
            checkBackgroundPermission();
        }
        iniFunc();
        initBackground();

        if (permissions) {
            if (!loadData("log").isEmpty()) {
                startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
                finish();
            }
            if (fAuth.getCurrentUser() != null) {
                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                finish();
            }
//            }else{
//                mBTAdapter = BluetoothAdapter.getDefaultAdapter();
//                if (mBTAdapter == null) {
//                    Toast.makeText(this, "Bluetooth not found", Toast.LENGTH_SHORT).show();
//                }else if (!mBTAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                }
//            }
        }
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initBackground() {
        back = findViewById(R.id.scrollView2);
        scrollView = findViewById(R.id.scrole);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearFocus();
                return false;
            }
        });
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearFocus();
                return false;
            }
        });
    }

    public void checkBackgroundPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void clearFocus() {
        if (focused != null) {
            focused.clearFocus();
            hideKeyboard(focused);
        }
    }

    public void iniFunc() {

        forLogin = findViewById(R.id.forLoginTitle);
        forReg = findViewById(R.id.forRegisTiltle);
        enterTitle = findViewById(R.id.enterTitle);
        linearLayout = findViewById(R.id.forLogin);
        name = findViewById(R.id.name);
//        Title = findViewById(R.id.RegistrationTitle);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        forgotText = findViewById(R.id.forgotText);
        passport = findViewById(R.id.passport);
        pass = findViewById(R.id.pass);
        confirmpass = findViewById(R.id.conPass);
//        reglog = findViewById(R.id.button2);
        nameE = findViewById(R.id.username);
        nameE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = nameE;
                }
            }
        });
        emailE = findViewById(R.id.emailadress);
        emailE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = emailE;
                }
            }
        });
        phoneE = findViewById(R.id.phoneNum);
        phoneE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = phoneE;
                }
            }
        });
        addressE = findViewById(R.id.HomeAddress);
        addressE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = addressE;
                }
            }
        });
        passportE = findViewById(R.id.passport_number);
        passportE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = passportE;
                }
            }
        });
        passE = findViewById(R.id.password);
        passE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = passE;
                }
            }
        });
        confirmpassE = findViewById(R.id.confirmpassword);
        confirmpassE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focused = confirmpassE;
                }
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        reff = FirebaseDatabase.getInstance().getReference().child("Member");

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("adminInfo");
        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminName = dataSnapshot.child("adminName").getValue().toString();
                AdminPassword = dataSnapshot.child("adminPass").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean checkPermissions() {
        //We need background permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 29) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_ID
            );
        }else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show();
                checkBackgroundPermission();
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                    finish();
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {// When the request to enable Bluetooth returns
//            if (resultCode == Activity.RESULT_OK) {
//                // Bluetooth is now enabled, so set up a chat session
//                DeviceAdress = mBTAdapter.getAddress();
//            } else {
//                // User did not enable Bluetooth or an error occured
//                Toast.makeText(this, "Blutooth is not Enabled", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public void saveId() {
        String userID = fAuth.getCurrentUser().getUid();
        noteRef = fStore.collection("users").document(userID);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity_RegisterActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    FirebaseAuth.getInstance().signOut();//logout
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (documentSnapshot.exists()) {
                    String Id = documentSnapshot.getString("Id");
                    saveData(Id, "Id");
                    Toast.makeText(MainActivity_RegisterActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                    finish();
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    }

    @Override
    public void onBackPressed() {
        if (register) {
            Login(null);
        }
    }
}


