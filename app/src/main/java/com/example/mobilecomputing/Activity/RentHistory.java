package com.example.mobilecomputing.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecomputing.Adapter.RentHistoryAdapter;
import com.example.mobilecomputing.R;
import java.util.ArrayList;
import java.util.List;

public class RentHistory extends AppCompatActivity {

    private RecyclerView rentHistoryRecyclerView;
    private RentHistoryAdapter rentHistoryAdapter;
    private List<RentHistoryItem> rentHistoryList;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_history);

        // Initialize RecyclerView and data list
        rentHistoryRecyclerView = findViewById(R.id.rent_history_recycler_view);
        rentHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentHistoryList = new ArrayList<>();
        loadRentHistoryData();

        // Set up the adapter
        rentHistoryAdapter = new RentHistoryAdapter(rentHistoryList);
        rentHistoryRecyclerView.setAdapter(rentHistoryAdapter);

        // Set up the back button listener
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Handles the back navigation
            }
        });
    }

    private void loadRentHistoryData() {
        // Sample data - replace this with actual data from your database or API
        rentHistoryList.add(new RentHistoryItem("Laptop", "2024-01-10", "2024-01-15"));
        rentHistoryList.add(new RentHistoryItem("Projector", "2024-02-01", "2024-02-05"));
        rentHistoryList.add(new RentHistoryItem("Camera", "2024-03-10", "2024-03-15"));
    }

    // RentHistoryItem class to represent each rental record
    public static class RentHistoryItem {
        String itemName;
        String rentStartDate;
        String rentEndDate;

        public RentHistoryItem(String itemName, String rentStartDate, String rentEndDate) {
            this.itemName = itemName;
            this.rentStartDate = rentStartDate;
            this.rentEndDate = rentEndDate;
        }

        public String getItemName() {
            return itemName;
        }

        public String getRentStartDate() {
            return rentStartDate;
        }

        public String getRentEndDate() {
            return rentEndDate;
        }
    }
}
