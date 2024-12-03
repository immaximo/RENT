package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RelativeLayout uploadImageContainer;
    private ImageView uploadIcon, backArrow;
    private EditText productName, productPrice, productDescription;
    private Button submitProductButton;
    private ProgressBar uploadProgressBar;

    private Uri selectedImageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_item);

        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance("gs://mobilecomputing-f9ac0.firebasestorage.app").getReference("product_images");
        databaseReference = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");

        // Initialize Views
        uploadImageContainer = findViewById(R.id.upload_image_container);
        uploadIcon = findViewById(R.id.upload_icon);
        backArrow = findViewById(R.id.back_arrow);  // Back button
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        submitProductButton = findViewById(R.id.submit_product_button);
        uploadProgressBar = findViewById(R.id.upload_progress_bar);

        backArrow.setOnClickListener(v -> finish());  // Handle back button press

        uploadImageContainer.setOnClickListener(v -> {
            // Allow the user to choose an image from their gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        submitProductButton.setOnClickListener(v -> {
            // Handle the product upload
            String name = productName.getText().toString().trim();
            String price = productPrice.getText().toString().trim();
            String description = productDescription.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty() || description.isEmpty() || selectedImageUri == null) {
                Toast.makeText(UploadActivity.this, "Please fill in all fields and select an image.", Toast.LENGTH_SHORT).show();
            } else {
                uploadProduct(name, price, description);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadIcon.setImageURI(selectedImageUri);  // Update the image view
        }
    }

    private void uploadProduct(String name, String price, String description) {
        uploadProgressBar.setVisibility(View.VISIBLE);  // Show the progress bar

        // Upload image to Firebase Storage
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(selectedImageUri)
                .addOnProgressListener(taskSnapshot -> {
                    // Calculate and update progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    uploadProgressBar.setProgress((int) progress);
                })
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Get the image URL after upload
                    String imageUrl = uri.toString();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String username = user != null ? user.getDisplayName() : "Anonymous";
                    uploadProductToDatabase(name, price, description, username, imageUrl);
                }))
                .addOnFailureListener(e -> {
                    // Handle image upload failure
                    Toast.makeText(UploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    uploadProgressBar.setVisibility(View.GONE);  // Hide progress bar
                });
    }

    private void uploadProductToDatabase(String name, String price, String description, String username, String imageUrl) {
        String productId = databaseReference.push().getKey();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("price", price);
        productData.put("description", description);
        productData.put("username", username);
        productData.put("imageUrl", imageUrl);

        databaseReference.child(productId).setValue(productData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity.this, "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                        clearFields();  // Clear input fields
                        uploadProgressBar.setVisibility(View.GONE);  // Hide progress bar
                    } else {
                        Toast.makeText(UploadActivity.this, "Failed to upload product", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error uploading product: " + task.getException().getMessage());
                        uploadProgressBar.setVisibility(View.GONE);  // Hide progress bar
                    }
                });
    }

    private void clearFields() {
        productName.setText("");
        productPrice.setText("");
        productDescription.setText("");
        uploadIcon.setImageResource(R.drawable.uploadimg);
    }
}
