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

    private TextView fullName, email, aboutMe, contact, editBtn, aboutEditBtn, contactEditBtn;
    private EditText fullNameInput, emailInput, aboutMeInput, contactInput;
    private ImageView backArrow;

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


        // Toggle edit mode when edit button for Full Name is clicked
        editBtn.setOnClickListener(view -> toggleEditMode(fullName, fullNameInput));

        // Toggle edit mode when edit button for About Me is clicked
        aboutEditBtn.setOnClickListener(view -> toggleEditMode(aboutMe, aboutMeInput));

        // Toggle edit mode when edit button for Contact is clicked
        contactEditBtn.setOnClickListener(view -> toggleEditMode(email, emailInput));

        // Optionally, handle saving profile data here
        Button saveButton = findViewById(R.id.save_button);
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
