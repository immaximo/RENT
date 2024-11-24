package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Redirect to Dashboard
            Intent intent = new Intent(Launch.this, Dashboard.class);
            startActivity(intent);
            finish(); // Close the Launch activity
            return;
        }

        // Continue if logged out
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
