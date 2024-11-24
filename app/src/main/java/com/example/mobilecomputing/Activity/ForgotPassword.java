package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mobilecomputing.Login;
import com.example.mobilecomputing.R;

public class ForgotPassword extends AppCompatActivity {

    private ImageView backArrow;
    private TextView title;
    private TextView description;
    private TextView emailLabel;
    private EditText emailInput;
    private Button sendCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        backArrow = findViewById(R.id.back_arrow);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        emailLabel = findViewById(R.id.emailLabel);
        emailInput = findViewById(R.id.emailInput);
        sendCodeButton = findViewById(R.id.sendCodeButton);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Send code" button click (e.g., send a code or validation)
                // You can add logic to send a verification code, etc.
            }
        });
    }
}
