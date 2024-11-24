package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateProfile extends AppCompatActivity {

    private ImageView profileImage, backArrow;
    private EditText username, bio, address, fullname;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);

        // Initialize Views
        profileImage = findViewById(R.id.profile_image);
        backArrow = findViewById(R.id.back_arrow);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        address = findViewById(R.id.address);
        saveButton = findViewById(R.id.save_button);

        // Handle the save button click
        saveButton.setOnClickListener(v -> {
            String user = username.getText().toString();
            String full = fullname.getText().toString();
            String userBio = bio.getText().toString();
            String userAddress = address.getText().toString();

            // Save profile data here (e.g., Firebase, SharedPreferences, etc.)
            // For now, display a Toast
            Toast.makeText(CreateProfile.this, "Profile Saved", Toast.LENGTH_SHORT).show();

            // Navigate to the Dashboard
            Intent intent = new Intent(CreateProfile.this, Dashboard.class);
            startActivity(intent);
            finish(); // Optional: Finish CreateProfile to remove it from the back stack
        });

        // Handle the back arrow click
        backArrow.setOnClickListener(v -> {
            // Navigate back to the Login activity
            Intent intent = new Intent(CreateProfile.this, Login.class);
            startActivity(intent);
            finish(); // Optional: Finish CreateProfile to remove it from the back stack
        });
    }
}
