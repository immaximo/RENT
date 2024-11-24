package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecomputing.Activity.ForgotPassword;
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth mAuth;

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
        forgotPasswordText = findViewById(R.id.forgotpassword);
        signUpText = findViewById(R.id.textview_sign_up);
        mAuth = FirebaseAuth.getInstance();

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
                    reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User exists, retrieve the email
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String email = snapshot.child("email").getValue(String.class);

                                    if (email != null) {
                                        // Authenticate with Firebase Auth using email and password
                                        mAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(Login.this, task -> {
                                                    if (task.isSuccessful()) {
                                                        // Login successful
                                                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Login.this, CreateProfile.class); // Replace with your Dashboard activity
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }, 1000);  // 1 second delay
                                                    } else {
                                                        // Authentication failed
                                                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // Email not found in the database
                                        Toast.makeText(Login.this, "User email not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // Username does not exist
                                Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database error
                            Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to forgot password screen
                Intent intent = new Intent(Login.this, ForgotPassword.class); // Make sure ForgotPassword is the correct class name
                startActivity(intent);
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