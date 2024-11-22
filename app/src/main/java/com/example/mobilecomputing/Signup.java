package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private EditText newusername, newemail, newpassword, confirmpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("usernames");

        newusername = findViewById(R.id.username);
        newemail = findViewById(R.id.email);
        newpassword = findViewById(R.id.userpass);
        confirmpass = findViewById(R.id.confirmpass);

        // Back arrow
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Login.class); // Replace Login with your login activity class name
            startActivity(intent);
            finish();
        });

        // Signup button
        Button signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(v -> {
            String username = newusername.getText().toString().trim();
            String email = newemail.getText().toString().trim();
            String password = newpassword.getText().toString().trim();
            String confirmpasswordtxt = confirmpass.getText().toString().trim();

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpasswordtxt.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill up all fields...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmpasswordtxt)) {
                Toast.makeText(getApplicationContext(), "Passwords are not matching...", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save additional user data in Realtime Database
                            HelperClass helper = new HelperClass(username, email);
                            databaseRef.child(username).setValue(helper).addOnCompleteListener(databaseTask -> {
                                if (databaseTask.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();

                                    // Navigate to Login activity
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                        finish();
                                    }, 1000);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(getApplicationContext(), "Signup failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}