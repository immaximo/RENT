package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Adapter.PaymentAdapter;
import com.example.mobilecomputing.Adapter.PaymentItem;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private PaymentAdapter cartAdapter;
    private List<PaymentItem> paymentItemList;
    private Button confirm;
    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page); // Layout file

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirm = findViewById(R.id.confirm_button);

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getIntent().getStringExtra("name");
                String price = getIntent().getStringExtra("price");
                String imageUrl = getIntent().getStringExtra("imageUrl");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", name);
                returnIntent.putExtra("price", price);
                returnIntent.putExtra("imageUrl", imageUrl);

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        // Create the payment item list
        paymentItemList = new ArrayList<>();
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String productId = getIntent().getStringExtra("productId");

        if (name != null && price != null && imageUrl != null) {
            PaymentItem item = new PaymentItem(name, price, imageUrl);
            paymentItemList.add(item);
        }

        cartAdapter = new PaymentAdapter(this, paymentItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        // Confirm button click listener
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductIdToRentedItems(productId); // Save productId to rented items in Firebase
            }
        });
    }

    // Method to save the productId to the user's rented items in Firebase
    private void saveProductIdToRentedItems(String productId) {
        if (productId == null) {
            Log.e(TAG, "Product ID is null");
            Toast.makeText(PaymentActivity.this, "Product ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user logged in.");
            Toast.makeText(PaymentActivity.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        Log.d(TAG, "Current user's email: " + email);

        // Get the username from Firebase using the email
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    Log.d(TAG, "Retrieved username: " + username);

                    if (username != null) {
                        // Save the productId under the user's rented items
                        saveProductToRentedItems(username, productId);
                    } else {
                        Log.e(TAG, "Username not found in Firebase.");
                        Toast.makeText(PaymentActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to get username: " + error.getMessage());
                Toast.makeText(PaymentActivity.this, "Failed to get username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProductToRentedItems(String username, String productId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference rentedItemsRef = database.getReference("users").child(username).child("rented_items");

        // Fetch existing rented items to determine the next available key
        rentedItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new HashMap to store the updated rented items
                HashMap<String, Object> rentedItemsMap = new HashMap<>();

                // Check if any items already exist, and if so, determine the next key (e.g., "product1", "product2", etc.)
                int productCount = 1;
                while (dataSnapshot.hasChild("product" + productCount)) {
                    productCount++;
                }

                // Create the new key and add the productId
                String newKey = "product" + productCount;
                rentedItemsMap.put(newKey, productId);

                // Save the updated map to the rented_items node
                rentedItemsRef.updateChildren(rentedItemsMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Product ID saved to rented items");
                        Toast.makeText(PaymentActivity.this, "Product rented successfully", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(home);
                        finish();
                    } else {
                        Log.e(TAG, "Failed to save product ID to rented items");
                        Toast.makeText(PaymentActivity.this, "Failed to rent product", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch rented items: " + databaseError.getMessage());
                Toast.makeText(PaymentActivity.this, "Failed to fetch rented items", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
