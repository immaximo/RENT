package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullName, email, aboutMe, contact;
    private EditText fullNameInput, emailInput, aboutMeInput, contactInput;
    private ImageView backArrow;
    private Button editButton;

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
            finish();
        });

        // Find views
        fullName = findViewById(R.id.user_full_name);
        email = findViewById(R.id.user_email);
        aboutMe = findViewById(R.id.user_about_me);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        aboutMeInput = findViewById(R.id.about_me_input);

        // Find Edit Profile button
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(view -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Optionally, handle saving profile data here
        Button saveButton = findViewById(R.id.edit_button);
        saveButton.setOnClickListener(v -> saveProfileData());
    }

    private void toggleEditMode(TextView textView, EditText editText) {
        boolean isEditing = editText.getVisibility() == View.VISIBLE;
        textView.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        editText.setVisibility(isEditing ? View.GONE : View.VISIBLE);
    }

    private void saveProfileData() {
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
    }
}
