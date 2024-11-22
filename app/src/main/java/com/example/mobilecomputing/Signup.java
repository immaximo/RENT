package com.example.mobilecomputing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPassword, signupConfirmPassword;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    ImageView backArrow, passwordToggle, confirmPasswordToggle;
    FirebaseAuth mAuth;
    boolean isPasswordVisible = false, isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        signupEmail = findViewById(R.id.email);
        signupUsername = findViewById(R.id.username);
        signupPassword = findViewById(R.id.userpass);
        signupConfirmPassword = findViewById(R.id.confirmpass);
        signupButton = findViewById(R.id.signup_button);
        backArrow = findViewById(R.id.back_arrow);
        passwordToggle = findViewById(R.id.password_toggle);
        confirmPasswordToggle = findViewById(R.id.confirm_password_toggle);
        mAuth = FirebaseAuth.getInstance();

        // Back arrow click listener to navigate back to Login activity
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
            finish(); // Optional: Close the Signup activity to prevent back navigation to it
        });

        // Toggle password visibility
        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                signupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye); // Update to closed eye icon
            } else {
                // Show password
                signupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_open); // Update to open eye icon
            }
            isPasswordVisible = !isPasswordVisible;
            signupPassword.setSelection(signupPassword.getText().length()); // Move cursor to the end
        });

        // Toggle confirm password visibility
        confirmPasswordToggle.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                // Hide password
                signupConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPasswordToggle.setImageResource(R.drawable.ic_eye); // Update to closed eye icon
            } else {
                // Show password
                signupConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPasswordToggle.setImageResource(R.drawable.ic_eye_open); // Update to open eye icon
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            signupConfirmPassword.setSelection(signupConfirmPassword.getText().length()); // Move cursor to the end
        });
        signupButton.setOnClickListener(view -> {
            String email = signupEmail.getText().toString();
            String username = signupUsername.getText().toString();
            String password = signupPassword.getText().toString();
            String confirmPassword = signupConfirmPassword.getText().toString();
            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
                // Create user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User created successfully in Firebase Auth
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Initialize Firebase Realtime Database reference
                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                    DatabaseReference reference = database.getReference("users");

                                    // Create the HelperClass object with the user's data
                                    HelperClass helperClass = new HelperClass(email, username);

                                    // Save the user data under the "users" node in Realtime Database
                                    reference.child(username).setValue(helperClass)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    // Data saved successfully to Realtime Database
                                                    Toast.makeText(Signup.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

                                                    // Navigate to the login screen
                                                    Intent intent = new Intent(Signup.this, Login.class);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }, 100);  // 1 second delay
                                                } else {
                                                    // Error saving to Realtime Database
                                                    Toast.makeText(Signup.this, "Signup failed! Try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {
                                    // Authentication failed, show error message
                                    Toast.makeText(getApplicationContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // Fields are empty, show an error
                Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}