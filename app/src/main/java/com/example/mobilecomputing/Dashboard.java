package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.example.mobilecomputing.Activity.UploadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.SearchView;
import com.example.mobilecomputing.Adapter.CardAdapter;
import com.example.mobilecomputing.Adapter.CardItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private List<CardItem> itemList;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 items per row

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");

        // Initialize the list and adapter
        itemList = new ArrayList<>();
        cardAdapter = new CardAdapter(itemList);
        recyclerView.setAdapter(cardAdapter);


        // Load products from Firebase
        loadProductsFromFirebase();

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });
    }

    private void filterItems(String query) {
        List<CardItem> filteredList = new ArrayList<>();
        for (CardItem item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        cardAdapter.updateItems(filteredList);
    }


    private void loadProductsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
                    if (productId == null) {
                        continue;
                    }

                    String name = productSnapshot.child("name").getValue(String.class);
                    String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                    String price = productSnapshot.child("price").getValue(String.class);
                    String description = productSnapshot.child("description").getValue(String.class);

                    if (name != null && imageUrl != null && price != null && description != null) {
                        itemList.add(new CardItem(name, imageUrl, price, description));
                    }
                }

                cardAdapter.updateItems(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
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
        FirebaseAuth.getInstance().signOut();
        Intent launchIntent = new Intent(Dashboard.this, Launch.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(navigationView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}