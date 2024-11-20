package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecomputing.Adapter.NotificationsAdapter;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<NotificationItem> notificationsList;
    private ImageView backArrow;

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
            finish();  // Close NotificationsActivity
        });

        // Initialize RecyclerView and data list
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();
        loadNotificationsData();

        // Set up the adapter
        notificationsAdapter = new NotificationsAdapter(notificationsList);
        notificationsRecyclerView.setAdapter(notificationsAdapter);
    }

    private void loadNotificationsData() {
        // Sample data - replace this with actual data from your database or API
        notificationsList.add(new NotificationItem("New Rent Alert", "You have a new rent due on 2024-01-15"));
        notificationsList.add(new NotificationItem("Payment Reminder", "Your payment for 'Laptop' is due tomorrow."));
        notificationsList.add(new NotificationItem("Account Update", "Your account has been updated successfully."));
    }

    // NotificationItem class to represent each notification
    public static class NotificationItem {
        String title;
        String message;

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
