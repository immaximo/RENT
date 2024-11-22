package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users");
    private EditText usernameEditText, passwordEditText;
    private ImageView passwordToggle;
    private Button loginButton;
    private TextView forgotPasswordText, signUpText;

    private boolean isPasswordVisible = false; // Track password visibility state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.userpass);
        passwordToggle = findViewById(R.id.password_toggle);
        loginButton = findViewById(R.id.button1);
        forgotPasswordText = findViewById(R.id.textview2);
        signUpText = findViewById(R.id.textview_sign_up);

        // Password visibility toggle logic
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordToggle.setImageResource(R.drawable.ic_eye); // Set to closed-eye icon
                    isPasswordVisible = false;
                } else {
                    // Show password
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordToggle.setImageResource(R.drawable.ic_eye_open); // Set to open-eye icon
                    isPasswordVisible = true;
                }
                passwordEditText.setSelection(passwordEditText.getText().length()); // Move cursor to end
            }
        });
        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Check if fields are empty
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform Firebase database check for user credentials
                    reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User exists, check password
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String dbPassword = snapshot.child("password").getValue(String.class);
                                    if (dbPassword != null && dbPassword.equals(password)) {
                                        // Login successful
                                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, Dashboard.class); // Change to your Dashboard activity
                                        startActivity(intent);
                                        finish(); // Optionally finish this activity so the user cannot go back to login
                                    } else {
                                        // Invalid password
                                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // User does not exist
                                Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database error
                            Toast.makeText(Login.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Set up forgot password click
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
                // Redirect to forgot password screen if needed
            }
        });

        // Navigate to signup screen
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class); // Replace with your signup class name
                startActivity(intent);
            }
        });
    }
}