package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullName, email, aboutMe, contact, user, address;
    private ImageView backArrow, profileImage;
    private Button editButton;


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            retrieveUsernameByEmail(email);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            // Go back to Dashboard
            Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        });

        // Find views
        fullName = findViewById(R.id.user_full_name);
        email = findViewById(R.id.user_email);
        aboutMe = findViewById(R.id.user_about_me);
        address = findViewById(R.id.address_user);
        contact = findViewById(R.id.number);
        user = findViewById(R.id.username);
        profileImage = findViewById(R.id.profile);


        // Find Edit Profile button
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(view -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);

        });

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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void retrieveUserData(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                address.setText(addressText);
                contact.setText(contactText);

                // Load the profile image using the URL from the database
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(profileImageUrl)  // URL directly from the database
                            .placeholder(R.drawable.blank_profile)  // Placeholder
                            .error(R.drawable.blank_profile)  // Fallback image
                            .into(profileImage);
                } else {
                    // If can't find photo
                    profileImage.setImageResource(R.drawable.blank_profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors here
            }
        });
    }
}
