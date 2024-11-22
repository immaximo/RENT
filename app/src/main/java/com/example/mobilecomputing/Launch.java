package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchpage);

        // Handle the "Let's Get Started" button click
        findViewById(R.id.getStartedButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Sign Up page
                Intent intent = new Intent(Launch.this, Signup.class);
                startActivity(intent);
            }
        });

        // Handle the "Already have an account? Sign in" click
        findViewById(R.id.signInText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Login page
                Intent intent = new Intent(Launch.this, Login.class);
                startActivity(intent);
            }
        });
    }
}