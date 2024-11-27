package com.example.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

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
        username.setText(passedUsername);

        // Address API
        Places.initialize(getApplicationContext(), "AIzaSyBUiGjbJP1irC08jV1NGcjT3icjvhIJx1k");

        // Define ActivityResultLauncher
        final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Place place = Autocomplete.getPlaceFromIntent(intent);
                            address.setText(place.getName());
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // Canceled
                    }
                });

        address.setFocusable(false);
        address.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Create the intent for Autocomplete
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startAutocomplete.launch(intent);
        });

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

            // Initially create an instance of HelperClass2 without the image URL
            HelperClass2 profileData = new HelperClass2(full, userBio, userAddress, "No Number Available", null); // userPhoto is null initially

            // For Real-Time Database Stuff
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference userRef = database.getReference("users");

            // Save profile data without the image URL
            assert passedUsername != null;
            userRef.child(passedUsername).child("profile").setValue(profileData)
                    .addOnSuccessListener(task1 -> {
                        // After profile data is saved, upload the profile image
                        if (selectedImageUri != null) {
                            uploadProfileImage(passedUsername, selectedImageUri);
                        } else {
                            Toast.makeText(this, "Profile saved without image", Toast.LENGTH_SHORT).show();
                        }
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

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://mobilecomputing-f9ac0.firebasestorage.app");
        StorageReference storageRef = storage.getReference();

        // Save image in "users/<username>/profile_photos/<username>.jpg"
        StorageReference imageRef = storageRef.child("users/" + username + "/profile_photos/" + username + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // After the image is uploaded, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the download URL of the uploaded image
                        String userPhoto = uri.toString();

                        // Create an instance of HelperClass2 with the profile data and image URL
                        String full = fullname.getText().toString().trim();
                        String userBio = bio.getText().toString().trim();
                        String userAddress = address.getText().toString().trim();
                        String userNumber = "No Number Available";

                        HelperClass2 profileData = new HelperClass2(full, userBio, userAddress, userNumber, userPhoto); // Pass the image URL here

                        // For Real Time Stuff
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        DatabaseReference userRef = database.getReference("users");

                        // Save profile data using HelperClass2
                        userRef.child(username).child("profile").setValue(profileData)
                                .addOnSuccessListener(task1 -> {
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

                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                });
    }

}