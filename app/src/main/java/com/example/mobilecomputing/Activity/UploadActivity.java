package com.example.mobilecomputing.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecomputing.R;

public class UploadActivity extends AppCompatActivity {

    private TextView titleAddProduct;
    private RelativeLayout uploadImageContainer;
    private TextView uploadText;
    private ImageView uploadIcon;
    private EditText productName;
    private EditText productCategory;
    private EditText productDescription;
    private Button submitProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_item); // Assuming your layout XML file is named activity_add_product.xml

        // Initialize Views
        titleAddProduct = findViewById(R.id.title_add_product);
        uploadImageContainer = findViewById(R.id.upload_image_container);
        uploadText = findViewById(R.id.upload_text);
        uploadIcon = findViewById(R.id.upload_icon);
        productName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.product_category);
        productDescription = findViewById(R.id.product_description);
        submitProductButton = findViewById(R.id.submit_product_button);

        // Upload image container click listener
        uploadImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file picker or perform upload actions here
                Toast.makeText(UploadActivity.this, "Upload Image clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Submit product button click listener
        submitProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String name = productName.getText().toString().trim();
                String category = productCategory.getText().toString().trim();
                String description = productDescription.getText().toString().trim();

                // Simple validation before submitting
                if (name.isEmpty() || category.isEmpty() || description.isEmpty()) {
                    Toast.makeText(UploadActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Logic to handle the product upload (e.g., save to Firebase or backend)
                    Toast.makeText(UploadActivity.this, "Product uploaded: " + name, Toast.LENGTH_SHORT).show();

                    // Clear the fields after submission (optional)
                    productName.setText("");
                    productCategory.setText("");
                    productDescription.setText("");
                }
            }
        });
    }
}
