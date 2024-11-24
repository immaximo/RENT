package com.example.mobilecomputing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateProfile extends AppCompatActivity {

    private ImageView profileImage, backArrow;
    private EditText username, bio, address, fullname;
    private Button saveButton;
    private Uri selectedImageUri;

    // For elect photo
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(profileImage);
                }
            });

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

        // Get the username passed from the Login activity
        String passedUsername = getIntent().getStringExtra("username");
        username.setText(passedUsername);  // Put in EditText

        // Select photo
        profileImage.setOnClickListener(v -> openImagePicker());

        // Handle the save button click
        saveButton.setOnClickListener(v -> {
            String full = fullname.getText().toString().trim();
            String userBio = bio.getText().toString().trim();
            String userAddress = address.getText().toString().trim();

            // Check if fields are not empty
            if (full.isEmpty() || userBio.isEmpty() || userAddress.isEmpty()) {
                Toast.makeText(this, "Please fill out all profile fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an instance of HelperClass2 with the profile data
            HelperClass2 profileData = new HelperClass2(full, userBio, userAddress);

            // For Real Time Stuff
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference userRef = database.getReference("users");

            // Save profile data using HelperClass2
            userRef.child(passedUsername).child("profile").setValue(profileData)
                    .addOnSuccessListener(task1 -> {
                        // Proceed to upload image after profile data is saved
                        if (selectedImageUri != null) {
                            uploadProfileImage(passedUsername, selectedImageUri);
                        }
                        Toast.makeText(this, "Profile details saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);  // 1 second delay
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save profile details", Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle the back arrow click
        backArrow.setOnClickListener(v -> {
            // Navigate back to the Login activity
            Intent intent = new Intent(CreateProfile.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);  // Launch the image picker
    }

    private void uploadProfileImage(String username, Uri selectedImageUri) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Save image in "users/<username>/profile_photos/<username>.jpg"
        StorageReference imageRef = storageRef.child("users/" + username + "/profile_photos/" + username + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                });
    }
}