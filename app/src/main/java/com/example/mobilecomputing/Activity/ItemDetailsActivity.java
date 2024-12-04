package com.example.mobilecomputing.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
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
    private TextView distanceTextView;  // Add TextView for distance
    private ImageView itemImageView;
    private Button confirmButton;
    private ImageView backArrow;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;  // LocationCallback for location updates
    private LocationRequest locationRequest;  // LocationRequest for continuous updates

    private static final String TAG = "ItemDetailsActivity"; // Tag for logging
    private Double productLatitude, productLongitude;

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
        distanceTextView = findViewById(R.id.distance); // Initialize distance TextView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        // Fetch product address and coordinates from Firebase using productId
        if (productId != null) {
            fetchProductDetails(productId); // Fetch the details including coordinates
        } else {
            itemAddress.setText("Address not available");
            distanceTextView.setText("Distance: N/A");
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

        // Initialize the location request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize the location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLocations().get(0);
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();

                    // Recalculate the distance every time the location changes
                    if (productLatitude != null && productLongitude != null) {
                        calculateDistanceToProduct(productLatitude, productLongitude, userLatitude, userLongitude);
                    }
                }
            }
        };
    }

    private void fetchProductDetails(String productId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference productRef = database.getReference("products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child("userAddress").getValue(String.class);
                productLatitude = dataSnapshot.child("latitude").getValue(Double.class);
                productLongitude = dataSnapshot.child("longitude").getValue(Double.class);

                // Set the address text
                if (location != null) {
                    itemAddress.setText(location); // Set the location/address in the TextView
                } else {
                    itemAddress.setText("Address not available");
                }

                // If latitude and longitude are available, start location updates
                if (productLatitude != null && productLongitude != null) {
                    startLocationUpdates();
                } else {
                    distanceTextView.setText("Distance: N/A");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch product details: " + databaseError.getMessage());
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void calculateDistanceToProduct(double productLatitude, double productLongitude, double userLatitude, double userLongitude) {
        // Create Location objects for the user and product
        Location productLocation = new Location("Product Location");
        productLocation.setLatitude(productLatitude);
        productLocation.setLongitude(productLongitude);

        Location userLocation = new Location("User Location");
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);

        // Calculate the distance in meters
        float distanceInMeters = userLocation.distanceTo(productLocation);

        // Convert distance to kilometers
        float distanceInKilometers = distanceInMeters / 1000;  // Convert meters to kilometers

        // Set the distance in the TextView
        distanceTextView.setText(String.format("Distance: %.2f km", distanceInKilometers));
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback); // Stop location updates when the activity is paused
    }
}
