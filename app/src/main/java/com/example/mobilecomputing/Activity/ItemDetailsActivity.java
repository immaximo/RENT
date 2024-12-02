package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private TextView itemAddress;
    private ImageView itemImageView;
    private Button confirmButton;
    private ImageView backArrow;

    private static final String TAG = "ItemDetailsActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details); // Layout file

        // Initialize views
        nameTextView = findViewById(R.id.item_name);
        priceTextView = findViewById(R.id.item_price);
        descriptionTextView = findViewById(R.id.item_description);
        itemImageView = findViewById(R.id.item_image);
        confirmButton = findViewById(R.id.confirm_button);
        backArrow = findViewById(R.id.back_arrow);
        itemAddress = findViewById(R.id.item_address);

        // Get the product details passed from the CardAdapter
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String productId = getIntent().getStringExtra("productId"); // Get productId from Intent

        // Set text for views
        nameTextView.setText(name);
        priceTextView.setText("Price: $" + price);
        descriptionTextView.setText(description);

        // Load item image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Change this as needed
                .error(R.drawable.uploadimg) // Change this as needed
                .into(itemImageView);

        // Fetch address from Firebase using productId
        if (productId != null) {
            fetchProductAddress(productId); // Fetch the address from Firebase
        } else {
            itemAddress.setText("Address not available");
        }

        // Handle confirm button click
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass product details to PaymentActivity
                Intent intent = new Intent(ItemDetailsActivity.this, PaymentActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("price", price);
                intent.putExtra("description", description);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("productId", productId); // Pass productId to PaymentActivity
                startActivity(intent);
            }
        });

        // Handle back arrow click (go back to Dashboard)
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");

        if (price == null || price.isEmpty()) {
            price = "Price not available";
        }

        // Set the values
        nameTextView.setText(name);
        priceTextView.setText("Price: $" + price);
        descriptionTextView.setText(description);

        // Load the item image again using Glide (just in case)
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Change this as needed
                .error(R.drawable.uploadimg) // Change this as needed
                .into(itemImageView);
    }

    // Fetch product address from Firebase using productId
    private void fetchProductAddress(String productId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference productRef = database.getReference("products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child("location").getValue(String.class);
                if (location != null) {
                    itemAddress.setText(location); // Set the location/address in the TextView
                    Log.d(TAG, "Address for productId " + productId + ": " + location); // Log the address
                } else {
                    itemAddress.setText("Address not available");
                    Log.d(TAG, "Address not found for productId " + productId); // Log if address is not found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch address: " + databaseError.getMessage());
                itemAddress.setText("Error fetching address");
            }
        });
    }
}
