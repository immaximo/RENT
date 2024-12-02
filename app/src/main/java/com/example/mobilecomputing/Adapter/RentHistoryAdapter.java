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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rent_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RentHistory.RentHistoryItem item = rentHistoryList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("Price: " + item.getPrice()); // Display only name and price
    }

    @Override
    public int getItemCount() {
        return rentHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.rent_price);
        }
    }
}
