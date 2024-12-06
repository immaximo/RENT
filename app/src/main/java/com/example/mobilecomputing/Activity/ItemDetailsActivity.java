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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView nameTextView, priceTextView, descriptionTextView, itemAddress, distanceTextView;
    private ImageView itemImageView, backArrow;
    private Button confirmButton;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private static final String TAG = "ItemDetailsActivity";
    private Double productLatitude, productLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        // Initialize views
        nameTextView = findViewById(R.id.item_name);
        priceTextView = findViewById(R.id.item_price);
        descriptionTextView = findViewById(R.id.item_description);
        itemImageView = findViewById(R.id.item_image);
        confirmButton = findViewById(R.id.confirm_button);
        backArrow = findViewById(R.id.back_arrow);
        itemAddress = findViewById(R.id.item_address);
        distanceTextView = findViewById(R.id.distance);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get product details from the Intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String productId = getIntent().getStringExtra("productId");

        // Set text for views
        nameTextView.setText(name);
        priceTextView.setText("Price: ₱" + price);
        descriptionTextView.setText(description);

        // Load product image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.uploadimg)
                .into(itemImageView);

        // Fetch product details from Firebase
        if (productId != null) {
            fetchProductDetails(productId);
        }

        // Fetch user details based on the current logged-in user's email
        fetchUserDetails();

        // Handle confirm button click
        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, PaymentActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("price", price);
            intent.putExtra("description", description);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("productId", productId);
            startActivity(intent);
        });

        // Handle back arrow click (go back to Dashboard)
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        });

        // Initialize location updates
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLocations().get(0);
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();

                    Log.d(TAG, "User Latitude: " + userLatitude + ", User Longitude: " + userLongitude);

                    if (productLatitude != null && productLongitude != null) {
                        calculateDistanceToProduct(productLatitude, productLongitude, userLatitude, userLongitude);
                    }
                }
            }
        };
    }

    private void fetchUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference userRef = database.getReference("users");

            userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String username = snapshot.child("username").getValue(String.class);
                            String userAddress = snapshot.child("profile").child("userAddress").getValue(String.class);
                            Double latitude = snapshot.child("profile").child("latitude").getValue(Double.class);
                            Double longitude = snapshot.child("profile").child("longitude").getValue(Double.class);

                            if (userAddress == null) userAddress = "Unknown Location";
                            if (latitude == null) latitude = 0.0;
                            if (longitude == null) longitude = 0.0;

                            itemAddress.setText(userAddress);

                            if (productLatitude != null && productLongitude != null) {
                                calculateDistanceToProduct(productLatitude, productLongitude, latitude, longitude);
                            }
                        }
                    } else {
                        Toast.makeText(ItemDetailsActivity.this, "User not found in the database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ItemDetailsActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
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

                if (location != null) {
                    itemAddress.setText(location);
                } else {
                    itemAddress.setText("Address not available");
                }

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
        Location productLocation = new Location("Product Location");
        productLocation.setLatitude(productLatitude);
        productLocation.setLongitude(productLongitude);

        Location userLocation = new Location("User Location");
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);

        float distanceInMeters = userLocation.distanceTo(productLocation);
        float distanceInKilometers = distanceInMeters / 1000;

        distanceTextView.setText(String.format("Distance: %.2f km", distanceInKilometers));
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
