package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilecomputing.Adapter.RentHistoryAdapter;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RentHistory extends AppCompatActivity {

    private RecyclerView rentHistoryRecyclerView;
    private RentHistoryAdapter rentHistoryAdapter;
    private List<RentHistoryItem> rentHistoryList;
    private ImageView backArrow;

    private static final String TAG = "RentHistory"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_history);

        // Initialize RecyclerView and data list
        rentHistoryRecyclerView = findViewById(R.id.rent_history_recycler_view);
        rentHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentHistoryList = new ArrayList<>();
        rentHistoryAdapter = new RentHistoryAdapter(rentHistoryList);
        rentHistoryRecyclerView.setAdapter(rentHistoryAdapter);

        // Set up the back button listener
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            // Go back to Dashboard
            Intent intent = new Intent(RentHistory.this, Dashboard.class);
            startActivity(intent);
            finish();
        });

        // Load products from Firebase
        loadProductsFromFirebase();
    }

    private void loadProductsFromFirebase() {
        // Step 1: Get the current user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user logged in.");
            Toast.makeText(RentHistory.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        Log.d(TAG, "Current user's email: " + email); // Log the email

        // Step 2: Get the username from Firebase using the email
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Step 3: Retrieve the username from the snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    Log.d(TAG, "Retrieved username: " + username); // Log the username

                    if (username != null) {
                        // Now use the username to fetch rented items
                        fetchRentedItems(username);
                    } else {
                        Log.e(TAG, "Username not found in Firebase.");
                        Toast.makeText(RentHistory.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to get username: " + error.getMessage());
                Toast.makeText(RentHistory.this, "Failed to get username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRentedItems(String username) {
        // Step 4: Use the username to get the rented items from 'users/{username}/profile/rented_items'
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference rentedItemsRef = database.getReference("users").child(username).child("rented_items");

        rentedItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rentHistoryList.clear();

                // Step 5: For each productId in rented_items, fetch the product details
                for (DataSnapshot rentedItemSnapshot : snapshot.getChildren()) {
                    String productId = rentedItemSnapshot.getValue(String.class);
                    Log.d(TAG, "Fetching details for productId: " + productId); // Log the productId

                    if (productId != null) {
                        fetchProductDetails(productId); // Fetch details for this productId
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load rented items: " + error.getMessage());
                Toast.makeText(RentHistory.this, "Failed to load rented items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetails(String productId) {
        // Step 6: Fetch the product details from the 'products' node using the productId
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference productRef = database.getReference("products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Step 7: Retrieve the product details (only name and price)
                String name = snapshot.child("name").getValue(String.class);
                String price = snapshot.child("price").getValue(String.class);

                Log.d(TAG, "Fetched product details: Name = " + name + ", Price = " + price); // Log name and price

                // Step 8: Create a RentHistoryItem for each product and add it to the list
                if (name != null && price != null) {
                    rentHistoryList.add(new RentHistoryItem(name, price));
                }

                // Update the RecyclerView adapter
                if (rentHistoryAdapter != null) {
                    rentHistoryAdapter.notifyDataSetChanged(); // Ensure this method notifies adapter of data changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product details: " + error.getMessage());
                Toast.makeText(RentHistory.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // RentHistoryItem class to represent each rental record (only name and price)
    public static class RentHistoryItem {
        String itemName;
        String price;

        public RentHistoryItem(String itemName, String price) {
            this.itemName = itemName;
            this.price = price;
        }

        public String getItemName() {
            return itemName;
        }

        public String getPrice() {
            return price;
        }
    }
}