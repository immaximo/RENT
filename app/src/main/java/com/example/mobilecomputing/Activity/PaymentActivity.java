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

import com.example.mobilecomputing.Adapter.PaymentAdapter;
import com.example.mobilecomputing.Adapter.PaymentItem;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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
        setContentView(R.layout.payment_page);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirm = findViewById(R.id.confirm_button);

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        });

        // Create the payment item list
        paymentItemList = new ArrayList<>();
        String name = getIntent().getStringExtra("name");
        String priceString = getIntent().getStringExtra("price");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String productId = getIntent().getStringExtra("productId");
        String uploaderUsername = getIntent().getStringExtra("uploaderUsername");

        double price = 0.0;
        if (priceString != null) {
            try {
                price = Double.parseDouble(priceString);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid price format", e);
            }
        }

        if (name != null && imageUrl != null) {
            PaymentItem item = new PaymentItem(name, price, imageUrl, 1); // Default quantity is 1
            paymentItemList.add(item);
        }

        cartAdapter = new PaymentAdapter(this, paymentItemList, (position, newQuantity) -> {
            PaymentItem item = paymentItemList.get(position);
            item.setQuantity(newQuantity);

            double totalPrice = item.getPrice() * newQuantity;
            item.setTotalPrice(totalPrice);

            cartAdapter.notifyItemChanged(position);
        });

        cartRecyclerView.setAdapter(cartAdapter);

        // Confirm button click listener
        confirm.setOnClickListener(v -> {
            double totalPrice = 0.0;
            for (PaymentItem item : paymentItemList) {
                totalPrice += item.getTotalPrice(); // Assuming total price is calculated in the adapter
            } // Save the product to the active user's rented items

            saveProductToRentedItems(productId, totalPrice);

            // Prepare notification data
            String message = "Someone wants to rent " + name;
            HashMap<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", "New Rent Request");
            notificationData.put("message", message);
            saveNotificationToDatabase(productId, notificationData); // Save notification for the product
        });
    }

    private void saveNotificationToDatabase(String productId, HashMap<String, Object> notificationData) {
        // Fetch the username from the product node using productId
        DatabaseReference productRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products")
                .child(productId);

        productRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null) {
                    // Save the notification under the user's notifications node
                    DatabaseReference notificationsRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users")
                            .child(username)
                            .child("notifications");

                    // Generate a unique ID for the new notification (e.g., using a timestamp or auto-increment)
                    String notificationId = notificationsRef.push().getKey();

                    if (notificationId != null) {
                        notificationsRef.child(notificationId).setValue(notificationData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Notification saved to database.");
                                    } else {
                                        Log.e(TAG, "Failed to save notification.");
                                    }
                                });
                    } else {
                        Log.e(TAG, "Failed to generate notification ID.");
                    }
                } else {
                    Log.e(TAG, "Username not found for product: " + productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching username from product: " + error.getMessage());
            }
        });
    }

    private void saveProductToRentedItems(String productId, double totalPrice) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user logged in.");
            Toast.makeText(PaymentActivity.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user's email and fetch their username
        String email = currentUser.getEmail();
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        // Find the username from the email
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);

                    if (username != null) {
                        // Fetch the product's name using the productId
                        DatabaseReference productRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("products")
                                .child(productId);

                        productRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                String itemName = productSnapshot.getValue(String.class);

                                if (itemName != null) {
                                    // Now save the product to the user's rented items
                                    DatabaseReference rentedItemsRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                            .getReference("users")
                                            .child(username)
                                            .child("rented_items");

                                    rentedItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot rentedItemsSnapshot) {
                                            HashMap<String, Object> itemData = new HashMap<>();
                                            itemData.put("item_name", itemName); // Use the item_name
                                            itemData.put("total_price", String.valueOf(totalPrice)); // Convert totalPrice to string

                                            // Generate a unique key for the rented item
                                            int itemCount = 1;
                                            while (rentedItemsSnapshot.hasChild("item" + itemCount)) {
                                                itemCount++;
                                            }

                                            String newKey = "item" + itemCount;
                                            rentedItemsRef.child(newKey).setValue(itemData).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Item name and total price saved to rented items.");
                                                    Toast.makeText(PaymentActivity.this, "Item rented successfully", Toast.LENGTH_SHORT).show();
                                                    Intent home = new Intent(getApplicationContext(), Dashboard.class);
                                                    startActivity(home);
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "Failed to save rented item.");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "Failed to fetch rented items: " + databaseError.getMessage());
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "Product name not found for productId: " + productId);
                                    Toast.makeText(PaymentActivity.this, "Product details not found.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error fetching product name: " + error.getMessage());
                            }
                        });
                    } else {
                        Log.e(TAG, "Username not found in Firebase.");
                        Toast.makeText(PaymentActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to get username: " + error.getMessage());
            }
        });
    }
}
