package com.example.mobilecomputing;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateProfile extends AppCompatActivity {

    private ImageView profileImage;
    private EditText username, bio, address;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);

        // Initialize Views
        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        address = findViewById(R.id.address);
        saveButton = findViewById(R.id.save_button);

        // Handle the save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String userBio = bio.getText().toString();
                String userAddress = address.getText().toString();

                // You can save the user profile data here (e.g., to Firebase, SharedPreferences, or a database)
                // For now, just log the info
                // For example:
                // Log.d("Profile", "Username: " + user + ", Bio: " + userBio + ", Address: " + userAddress);

                // Optionally, show a Toast to confirm saving
                // Toast.makeText(ProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
