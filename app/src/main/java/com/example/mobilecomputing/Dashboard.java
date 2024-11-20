package com.example.mobilecomputing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecomputing.Activity.MapActivity;
import com.example.mobilecomputing.Activity.NotificationsActivity;
import com.example.mobilecomputing.Activity.ProfileActivity;
import com.example.mobilecomputing.Activity.RentHistory;
import com.example.mobilecomputing.Activity.UploadActivity; // Import the Upload Activity
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.SearchView;
import com.example.mobilecomputing.Adapter.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CardAdapter cardAdapter;
    private List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the hamburger icon for the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the hamburger icon
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon); // Set custom hamburger icon if needed
        }

        // Initialize Navigation Drawer
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // Set the listener

        // Initialize RecyclerView with GridLayoutManager (2 items per row)
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 items per row

        // Create a list of items for the adapter
        itemList = new ArrayList<>();
        itemList.add("Item 1");
        itemList.add("Item 2");
        itemList.add("Item 3");
        itemList.add("Item 4");
        itemList.add("Item 5");
        itemList.add("Item 5");
        itemList.add("Item 6");
        itemList.add("Item 7");
        itemList.add("Item 8");
        itemList.add("Item 9");

        // Initialize CardAdapter and set it to the RecyclerView
        cardAdapter = new CardAdapter(itemList);
        recyclerView.setAdapter(cardAdapter);

        // Initialize SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission (e.g., filter RecyclerView)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search text change (e.g., update RecyclerView)
                return false;
            }
        });
    }

    // Override onNavigationItemSelected method from NavigationView.OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_renthistory) {
            // Start the RentHistoryActivity
            Intent rentHistoryIntent = new Intent(Dashboard.this, RentHistory.class);
            startActivity(rentHistoryIntent);
        } else if (menuItem.getItemId() == R.id.nav_viewprofile) {
            // Start the ProfileActivity
            Intent profileIntent = new Intent(Dashboard.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (menuItem.getItemId() == R.id.nav_notifications) {
            // Start the NotificationsActivity
            Intent notificationsIntent = new Intent(Dashboard.this, NotificationsActivity.class);
            startActivity(notificationsIntent);
        } else if (menuItem.getItemId() == R.id.nav_map) {
            // Start the MapActivity
            Intent mapIntent = new Intent(Dashboard.this, MapActivity.class);
            startActivity(mapIntent);
        } else if (menuItem.getItemId() == R.id.nav_upload) {
            // Start the UploadActivity (where user uploads the product details)
            Intent uploadIntent = new Intent(Dashboard.this, UploadActivity.class);
            startActivity(uploadIntent);
        } else if (menuItem.getItemId() == R.id.nav_logout) {
            // Logout logic here
            logoutUser();
        }

        // Close the navigation drawer after selecting an item
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logoutUser() {
        // Clear user session (SharedPreferences or Firebase, depending on your app's logic)

        // Redirect to Launch Activity
        Intent launchIntent = new Intent(Dashboard.this, Launch.class);  // Redirect to Launch.java
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack
        startActivity(launchIntent);

        // Optionally, finish the current activity so the user can't press back to return
        finish();
    }



    // Handle toolbar item clicks (e.g., hamburger icon)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Open the navigation drawer when the hamburger icon is clicked
            drawerLayout.openDrawer(navigationView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
