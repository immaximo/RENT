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

    private TextView fullName, username, email;
    private EditText fullNameInput, usernameInput, emailInput;
    private ImageView editFullName, editUsername, editEmail;
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

        fullName = findViewById(R.id.user_full_name);
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);

        fullNameInput = findViewById(R.id.full_name_input);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);

        editFullName = findViewById(R.id.editFullName);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);

        editFullName.setOnClickListener(view -> toggleEditMode(fullName, fullNameInput));
        editUsername.setOnClickListener(view -> toggleEditMode(username, usernameInput));
        editEmail.setOnClickListener(view -> toggleEditMode(email, emailInput));

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
        String updatedUsername = usernameInput.getText().toString().trim();
        String updatedEmail = emailInput.getText().toString().trim();

        // Check if the data is not empty before updating
        if (!updatedFullName.isEmpty()) {
            fullName.setText(updatedFullName);
        }
        if (!updatedUsername.isEmpty()) {
            username.setText(updatedUsername);
        }
        if (!updatedEmail.isEmpty()) {
            email.setText(updatedEmail);
        }

        // Hide the EditText and show TextView again for the fields that were edited
        if (fullNameInput.getVisibility() == View.VISIBLE) {
            fullNameInput.setVisibility(View.GONE);
            fullName.setVisibility(View.VISIBLE);
        }
        if (usernameInput.getVisibility() == View.VISIBLE) {
            usernameInput.setVisibility(View.GONE);
            username.setVisibility(View.VISIBLE);
        }
        if (emailInput.getVisibility() == View.VISIBLE) {
            emailInput.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);
        }
    }
}
