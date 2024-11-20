package com.example.mobilecomputing.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecomputing.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<String> items;

    // Constructor
    public CardAdapter(List<String> items) {
        if (items != null) {
            this.items = items;
        } else {
            throw new IllegalArgumentException("Item list cannot be null");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (Ensure you have this layout created)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the TextView
        String item = items.get(position);
        holder.textView.setText(item);
    }

    public void updateItems(List<String> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // Return the size of the item list
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        // ViewHolder constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Make sure your card_item.xml has the TextView with the correct ID
            textView = itemView.findViewById(R.id.card_text);  // Replace with actual ID if necessary
        }
    }
}