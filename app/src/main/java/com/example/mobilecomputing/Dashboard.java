package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
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
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CardAdapter cardAdapter;
    private List<CardItem> itemList;

    private DatabaseReference databaseReference;
    private DatabaseReference userRef;

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
        cardAdapter = new CardAdapter(this, itemList);
        recyclerView.setAdapter(cardAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            retrieveUserAddressByEmail(email);
        }

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

    private void retrieveUserAddressByEmail(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users");

        // Using email as the key to retrieve user address
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the user address from the snapshot
                        String userAddress = snapshot.child("profile").child("userAddress").getValue(String.class);
                        String username = snapshot.child("username").getValue(String.class);

                        if (username != null) {
                            // Update the tv_user_name TextView in the navigation header
                            View headerView = navigationView.getHeaderView(0);
                            TextView userNameTextView = headerView.findViewById(R.id.tv_user_name);
                            userNameTextView.setText(username);
                        } else {
                            Toast.makeText(Dashboard.this, "Username not found.", Toast.LENGTH_SHORT).show();
                        }

                        if (userAddress != null) {
                            // Load products based on the user's address
                            loadProductsByUserAddress(userAddress);
                        } else {
                            Toast.makeText(Dashboard.this, "User address not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Dashboard.this, "User not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Dashboard.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByUserAddress(String userAddress) {
        if (userAddress == null) {
            Log.e("Dashboard", "User address is null. Cannot load products.");
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mobilecomputing-f9ac0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference productRef = database.getReference("products");

        Log.d("Dashboard", "Loading products with address: " + userAddress);

        // Query the products by userAddress
        productRef.orderByChild("userAddress").equalTo(userAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();

                // Find the TextView that shows the message
                TextView noItemsMessage = findViewById(R.id.no_items_message);

                if (!dataSnapshot.exists()) {
                    // If no products are found, show the "no items" message
                    noItemsMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);  // Hide the RecyclerView
                    return;
                }

                // Hide the "no items" message if products are found
                noItemsMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);  // Show the RecyclerView

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productId = snapshot.getKey();  // Get the productId from the snapshot key
                    String name = snapshot.child("name").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);

                    if (name != null && imageUrl != null && price != null && description != null && productId != null) {
                        // Now adding the products to the list with the correct number of parameters
                        itemList.add(new CardItem(name, imageUrl, price, description, productId));
                    }
                }

                cardAdapter.updateItems(itemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Dashboard", "Failed to load products for address: " + userAddress);
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
