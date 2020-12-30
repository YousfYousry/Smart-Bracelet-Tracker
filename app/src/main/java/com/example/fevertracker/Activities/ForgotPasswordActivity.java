package com.example.fevertracker.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.fevertracker.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    Context context=this;
    EditText emailAddress;
    ProgressBar progressBar;

    public void onback(View view) {
        onBackPressed();
    }

    public void SendEmail(View view) {
        final String emailString = emailAddress.getText().toString().trim();

        if (TextUtils.isEmpty(emailString)) {
            emailAddress.setError("Email address is Required.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(context, "A resent link has been sent to your email successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailAddress = findViewById(R.id.emailAddress);
        emailAddress.setText(getIntent().getStringExtra("EmailAddress"));
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}