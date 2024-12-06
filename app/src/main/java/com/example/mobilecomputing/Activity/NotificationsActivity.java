package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecomputing.Adapter.NotificationsAdapter;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<NotificationItem> notificationsList;
    private ImageView backArrow;
    private static final String TAG = "NotificationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_page);

        // Set up the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back arrow functionality
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationsActivity.this, Dashboard.class);
            startActivity(intent);
            finish(); // Close NotificationsActivity
        });

        // Initialize RecyclerView and data list
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();

        // Load notifications data
        loadNotificationsData();

        // Set up the adapter
        notificationsAdapter = new NotificationsAdapter(notificationsList);
        notificationsRecyclerView.setAdapter(notificationsAdapter);
    }

    private void loadNotificationsData() {
        // Fetch the current user from Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserEmail = auth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users");

            // Query to get the username associated with the email
            userRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String username = snapshot.child("username").getValue(String.class);

                        if (username != null) {
                            // Now fetch notifications for the specific username
                            fetchNotificationsForUser(username);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to get username: " + databaseError.getMessage());
                }
            });
        }
    }

    private void fetchNotificationsForUser(String username) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(username)
                .child("rented_items");

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productId = snapshot.child("productId").getValue(String.class);
                    Double totalPrice = snapshot.child("total_price").getValue(Double.class);

                    if (productId != null && totalPrice != null) {
                        String message = "Product ID: " + productId + ", Total Price: " + totalPrice;
                        notificationsList.add(new NotificationItem("Rented Item", message));
                    }
                }

                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load notifications: " + databaseError.getMessage());
            }
        });
    }


    // NotificationItem class to represent each notification
    public static class NotificationItem {
        private final String title;
        private final String message;

        public NotificationItem(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }
}
