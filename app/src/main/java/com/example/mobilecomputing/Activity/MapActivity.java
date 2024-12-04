package com.example.mobilecomputing.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "MapActivity";

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private ImageView backArrow;
    private Button saveButton;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);

        backArrow = findViewById(R.id.back_arrow);
        saveButton = findViewById(R.id.save_button);

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        });

        saveButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                saveLocationToDatabase();
            } else {
                Toast.makeText(MapActivity.this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissionsAndGetLocation();
    }

    private void checkPermissionsAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                loadMap();
            } else {
                Toast.makeText(MapActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Load user's saved location or default location
        loadUserLocation();

        // Enable clicks on the map
        mMap.setOnMapClickListener(latLng -> {
            // Update the selected location
            selectedLatLng = latLng;

            // Remove the old marker if it exists
            if (currentMarker != null) {
                currentMarker.remove();
            }

            // Add a new marker at the clicked location
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

            // Move the camera to the selected location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }

    private void loadUserLocation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        Log.d(TAG, "Current user's email: " + email);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    if (username != null) {
                        DatabaseReference profileRef = userRef.child(username).child("profile");

                        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot profileSnapshot) {
                                Double latitude = profileSnapshot.child("latitude").getValue(Double.class);
                                Double longitude = profileSnapshot.child("longitude").getValue(Double.class);

                                if (latitude != null && longitude != null) {
                                    LatLng userLocation = new LatLng(latitude, longitude);
                                    currentMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Saved Location"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                                } else {
                                    setDefaultMarker();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Failed to retrieve user location: " + error.getMessage());
                                setDefaultMarker();
                            }
                        });
                    } else {
                        Toast.makeText(MapActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        setDefaultMarker();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve username: " + error.getMessage());
                setDefaultMarker();
            }
        });
    }

    private void setDefaultMarker() {
        LatLng defaultLatLng = currentLocation != null ?
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()) :
                new LatLng(-34, 151); // Sydney, Australia (default location)

        currentMarker = mMap.addMarker(new MarkerOptions().position(defaultLatLng).title("Default Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
        selectedLatLng = defaultLatLng; // Set default location as selected
    }

    private void saveLocationToDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);

                    if (username != null) {
                        DatabaseReference profileRef = userRef.child(username).child("profile");

                        HashMap<String, Object> locationData = new HashMap<>();
                        locationData.put("latitude", selectedLatLng.latitude);
                        locationData.put("longitude", selectedLatLng.longitude);

                        profileRef.updateChildren(locationData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MapActivity.this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MapActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MapActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to save location: " + error.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
