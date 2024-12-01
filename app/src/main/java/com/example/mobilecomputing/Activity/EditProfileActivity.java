package com.example.mobilecomputing.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Activity.ProfileActivity;
import com.example.mobilecomputing.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private EditText fullname, aboutme, contact, address;
    private TextView username, editphoto;
    private ImageView profilephoto;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        username = findViewById(R.id.edit_user);
        fullname = findViewById(R.id.user_full_name);
        aboutme = findViewById(R.id.user_about_me);
        contact = findViewById(R.id.user_contact);
        address = findViewById(R.id.user_address);
        editphoto = findViewById(R.id.edit_photo);
        profilephoto = findViewById(R.id.profilePhoto);

        // Handle "edit photo" click
        editphoto.setOnClickListener(v -> openImagePicker());

        // Address API
        Places.initialize(getApplicationContext(), "AIzaSyBUiGjbJP1irC08jV1NGcjT3icjvhIJx1k");

        final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            // Get the selected place from the intent
                            Place place = Autocomplete.getPlaceFromIntent(intent);

                            // Display the full formatted address
                            String fullAddress = place.getAddress();  // This gives you the full formatted address
                            address.setText(fullAddress);  // Set the full address in the address EditText or TextView
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // Canceled
                    }
                });

        address.setFocusable(false);
        address.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.FORMATTED_ADDRESS);  // Changed 'ADDRESS' to 'FORMATTED_ADDRESS'

            // Create the intent for Autocomplete
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);

            // Launch the autocomplete intent
            startAutocomplete.launch(intent);
        });

        // Back button
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Get the username passed from the Login activity
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            retrieveUsernameByEmail(email);
        }
    }

    // For select photo
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(profilephoto);
                }
            });

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void retrieveUsernameByEmail(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        // Use orderByChild to filter by the email field
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String currentuser = snapshot.child("username").getValue(String.class);
                    retrieveUserData(currentuser);

                    // Save Button here
                    Button saveProfileButton = findViewById(R.id.save_profile_button);
                    saveProfileButton.setOnClickListener(v -> saveProfileData(currentuser));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void retrieveUserData(String currentuser) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(currentuser);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullNameText = dataSnapshot.child("profile").child("full").getValue(String.class);
                String aboutMeText = dataSnapshot.child("profile").child("userBio").getValue(String.class);
                String addressText = dataSnapshot.child("profile").child("userAddress").getValue(String.class);
                String contactText = dataSnapshot.child("profile").child("userNumber").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("profile").child("userPhoto").getValue(String.class);

                // Set data to views
                username.setText(currentuser);
                fullname.setHint(fullNameText);
                aboutme.setHint(aboutMeText);
                address.setHint(addressText);
                contact.setHint(contactText);

                // Load the profile image using the URL from the database
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(profileImageUrl)  // URL directly from the database
                            .placeholder(R.drawable.blank_profile)  // Placeholder
                            .error(R.drawable.blank_profile)  // Fallback image
                            .into(profilephoto);
                } else {
                    // If can't find photo
                    profilephoto.setImageResource(R.drawable.blank_profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors here
            }
        });
    }

    private void saveProfileData(String currentuser) {
        if (currentuser == null) {
            Toast.makeText(this, "Unable to identify user!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(currentuser).child("profile");

        HashMap<String, Object> updates = new HashMap<>();

        if (!TextUtils.isEmpty(fullname.getText())) {
            updates.put("full", fullname.getText().toString());
        }
        if (!TextUtils.isEmpty(aboutme.getText())) {
            updates.put("userBio", aboutme.getText().toString());
        }
        if (!TextUtils.isEmpty(contact.getText())) {
            updates.put("userNumber", contact.getText().toString());
        }
        if (!TextUtils.isEmpty(address.getText())) {
            updates.put("userAddress", address.getText().toString());
        }

        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("users/" + currentuser + "/profile_photos/" + currentuser + ".jpg");
            storageRef.putFile(selectedImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("userPhoto", uri.toString());
                        userRef.updateChildren(updates).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
                    });
                }
            });
        } else {
            userRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
