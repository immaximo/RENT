package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RelativeLayout uploadImageContainer;
    private ImageView uploadIcon, backArrow;
    private EditText productName, productPrice, productDescription;
    private Button submitProductButton;

    private Uri selectedImageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_item);

        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference("product_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Initialize Views
        uploadImageContainer = findViewById(R.id.upload_image_container);
        uploadIcon = findViewById(R.id.upload_icon);
        backArrow = findViewById(R.id.back_arrow);  // Back button
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        submitProductButton = findViewById(R.id.submit_product_button);

        // Set back button listener to navigate to DashboardActivity
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, Dashboard.class); // Change to your DashboardActivity
                startActivity(intent);
                finish(); // Optional: close this activity
            }
        });

        // Upload image container click listener
        uploadImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Submit product button click listener
        submitProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadIcon.setImageURI(selectedImageUri); // Update the UI with selected image

            // Make the image fill the container
            uploadIcon.setVisibility(View.VISIBLE);

            // Hide the "Click to Upload" text
            TextView uploadText = findViewById(R.id.upload_text);
            uploadText.setVisibility(View.GONE);

            // Check the file size (5MB limit)
            long fileSize = new File(selectedImageUri.getPath()).length();
            if (fileSize > 5 * 1024 * 1024) { // 5MB
                Toast.makeText(this, "File size is too large", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    private void uploadProduct() {
        String name = productName.getText().toString().trim();
        String price = productPrice.getText().toString().trim();
        String description = productDescription.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference for the image to be uploaded in Firebase Storage
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        // Upload image to Firebase Storage
        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString(); // Get the URL of the uploaded image

                    // Save product details to Firebase Realtime Database
                    String productId = databaseReference.push().getKey();
                    if (productId != null) {
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("id", productId);
                        productData.put("name", name);
                        productData.put("price", price);
                        productData.put("description", description);
                        productData.put("imageUrl", imageUrl); // Store the image URL

                        // Store the product data in Firebase Realtime Database
                        databaseReference.child(productId).setValue(productData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UploadActivity.this, "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                                        clearFields(); // Clear input fields after upload
                                    } else {
                                        Toast.makeText(UploadActivity.this, "Failed to upload product", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        productName.setText("");
        productPrice.setText("");
        productDescription.setText("");
        uploadIcon.setImageResource(R.drawable.uploadimg); // Reset to default icon

        // Make the "Click to Upload" text visible again
        TextView uploadText = findViewById(R.id.upload_text);
        uploadText.setVisibility(View.VISIBLE);

        selectedImageUri = null;
    }

}
