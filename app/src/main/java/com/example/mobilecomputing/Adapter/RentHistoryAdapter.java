package com.example.mobilecomputing.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilecomputing.Activity.RentHistory;
import com.example.mobilecomputing.R;
import java.util.List;

public class RentHistoryAdapter extends RecyclerView.Adapter<RentHistoryAdapter.ViewHolder> {

    private List<RentHistory.RentHistoryItem> rentHistoryList;

    public RentHistoryAdapter(List<RentHistory.RentHistoryItem> rentHistoryList) {
        this.rentHistoryList = rentHistoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rent_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind data to the view holder
        RentHistory.RentHistoryItem item = rentHistoryList.get(position);
        holder.itemName.setText(item.getItemName()); // Set item name
        holder.totalPrice.setText("Total Price: " + item.getPrice()); // Set total price
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return rentHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;    // TextView for displaying item name
        TextView totalPrice;  // TextView for displaying total price

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the TextViews from the item layout
            itemName = itemView.findViewById(R.id.item_name);
            totalPrice = itemView.findViewById(R.id.rent_price);
        }
    }
}