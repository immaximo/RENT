package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.example.mobilecomputing.UpdateProfileHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullName, email, aboutMe, contact, editBtn, aboutEditBtn, contactEditBtn, user, addressInput;
    private EditText fullNameInput, emailInput, aboutMeInput, contactInput;
    private ImageView backArrow, profileImage;
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
        setContentView(R.layout.profile_page);

        // Set up the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Back arrow functionality
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            // Go back to Dashboard
            Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
            startActivity(intent);
            finish();  // Optional: To finish ProfileActivity and remove it from the back stack
        });

        // Find views
        fullName = findViewById(R.id.user_full_name);
        email = findViewById(R.id.user_email);
        aboutMe = findViewById(R.id.user_about_me);  // About Me TextView
        editBtn = findViewById(R.id.editbtn);  // Link the "Edit" button for Full Name
        aboutEditBtn = findViewById(R.id.abouteditbtn);  // Edit button for About Me
        contactEditBtn = findViewById(R.id.contacteditbtn);  // Edit button for Contact
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        aboutMeInput = findViewById(R.id.about_me_input);  // About Me EditText
        addressInput = findViewById(R.id.address_input);
        contact = findViewById(R.id.number);
        user = findViewById(R.id.username);
        profileImage = findViewById(R.id.profile);

        // Toggle edit mode when edit button for Full Name is clicked
        editBtn.setOnClickListener(view -> toggleEditMode(fullName, fullNameInput));

        // Toggle edit mode when edit button for About Me is clicked
        aboutEditBtn.setOnClickListener(view -> toggleEditMode(aboutMe, aboutMeInput));

        // Toggle edit mode when edit button for Contact is clicked
        contactEditBtn.setOnClickListener(view ->
                toggleEditMode(email, emailInput));

        // Select photo
        profileImage.setOnClickListener(v -> openImagePicker());


        // Get the username passed from the Login activity
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();  // Use the email to search for the user
            retrieveUsernameByEmail(email);
        }
    }

    private void retrieveUsernameByEmail(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        // Use orderByChild to filter by the email field
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    user.setText(username); // For username
                    retrieveUserData(username); // For everything else

                    // Optionally, handle saving profile data here
                    Button saveButton = findViewById(R.id.save_button);
                    String currentPhoto = dataSnapshot.child("userPhoto").getValue(String.class);
                    saveButton.setOnClickListener(v -> uploadProfileImage(username, Uri.parse(currentPhoto)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);  // Launch the image picker
    }

    private void retrieveUserData(String username) {
        // Firebase Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user data from the database
                String fullNameText = dataSnapshot.child("profile").child("full").getValue(String.class);
                String emailText = dataSnapshot.child("email").getValue(String.class);
                String aboutMeText = dataSnapshot.child("profile").child("userBio").getValue(String.class);
                String addressText = dataSnapshot.child("profile").child("userAddress").getValue(String.class);
                String contactText = dataSnapshot.child("profile").child("userNumber").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("profile").child("userPhoto").getValue(String.class);

                // Set data to views
                fullName.setText(fullNameText);
                email.setText(emailText);
                aboutMe.setText(aboutMeText);
                addressInput.setText(addressText);
                contact.setText(contactText);

                // Load the profile image using the URL from the database
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(profileImageUrl)  // URL directly from the database
                            .placeholder(R.drawable.profile_circle)  // Placeholder image while loading
                            .error(R.drawable.profile_circle)  // Fallback image in case of error
                            .into(profileImage);
                } else {
                    // Set a placeholder image if no URL is available
                    profileImage.setImageResource(R.drawable.profile_circle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors here
            }
        });
    }


    private void toggleEditMode(TextView textView, EditText editText) {
        boolean isEditing = editText.getVisibility() == View.VISIBLE;
        textView.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        editText.setVisibility(isEditing ? View.GONE : View.VISIBLE);
    }

    private void saveProfileData(String username, String userPhoto) {
        String updatedFullName = fullNameInput.getText().toString().trim();
        String updatedEmail = emailInput.getText().toString().trim();
        String updatedAboutMe = aboutMeInput.getText().toString().trim();

        // Check if the data is not empty before updating
        if (!updatedFullName.isEmpty()) {
            fullName.setText(updatedFullName);
        }
        if (!updatedEmail.isEmpty()) {
            email.setText(updatedEmail);
        }
        if (!updatedAboutMe.isEmpty()) {
            aboutMe.setText(updatedAboutMe);
        }

        // Hide the EditText and show TextView again for the fields that were edited
        if (fullNameInput.getVisibility() == View.VISIBLE) {
            fullNameInput.setVisibility(View.GONE);
            fullName.setVisibility(View.VISIBLE);
        }
        if (emailInput.getVisibility() == View.VISIBLE) {
            emailInput.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);
        }
        if (aboutMeInput.getVisibility() == View.VISIBLE) {
            aboutMeInput.setVisibility(View.GONE);
            aboutMe.setVisibility(View.VISIBLE);
        }

        // Instance of UpdateProfileHelper with updated data
        UpdateProfileHelper helper = new UpdateProfileHelper(
                updatedFullName.isEmpty() ? fullName.getText().toString().trim() : updatedFullName,
                updatedAboutMe.isEmpty() ? aboutMe.getText().toString().trim() : updatedAboutMe,
                userPhoto
        );

        // Real-Time Database update
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        // Update the user's profile in the database
        if (username != null) {
            userRef.child(username).child("profile").setValue(helper)
                    .addOnSuccessListener(aVoid -> {
                        // Data has been updated in the database successfully
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update data
                        Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Username not found. Unable to update profile.", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadProfileImage(String username, Uri selectedImageUri) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://mobilecomputing-f9ac0.firebasestorage.app");
        StorageReference storageRef = storage.getReference();

        // Define the storage reference where the new image will be saved
        StorageReference imageRef = storageRef.child("users/" + username + "/profile_photos/" + username + ".jpg");

        // Upload the new profile image to Firebase Storage
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image upload is successful, now get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Get the download URL for the uploaded image
                        String userPhotoUrl = uri.toString();

                        // After uploading the image, update the user's profile data in the database
                        saveProfileData(username, userPhotoUrl);

                        // Optionally, show a success message
                        Toast.makeText(this, "Profile image uploaded and updated!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Failed to get the download URL
                        Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                });
    }


}
