package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page); // Replace with your signup activity layout file

        // Back arrow functionality
        ImageView backArrow = findViewById(R.id.back_arrow); // Reference to the back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login activity
                Intent intent = new Intent(Signup.this, Login.class); // Replace Login with your login activity class name
                startActivity(intent);
                finish(); // Optional: Close the signup activity to prevent going back to it using the back button
            }
        });

        // Sign up button functionality
        Button signUpButton = findViewById(R.id.button1);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login activity
                Intent intent = new Intent(Signup.this, Login.class); // Replace Login with your login activity class name
                startActivity(intent);
                finish(); // Optional: Close the signup activity
            }
        });
    }
}
