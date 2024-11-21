package com.example.mobilecomputing;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPassword, signupConfirmPassword;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    ImageView backArrow, passwordToggle, confirmPasswordToggle;
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
        backArrow = findViewById(R.id.back_arrow); // Reference to the back arrow ImageView
        passwordToggle = findViewById(R.id.password_toggle);
        confirmPasswordToggle = findViewById(R.id.confirm_password_toggle);

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

            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                // Save user data to Firebase
                database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
                reference = database.getReference("users");

                HelperClass helperClass = new HelperClass(email, username, password);
                reference.child(username).setValue(helperClass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Signup.this, "Signup failed! Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
