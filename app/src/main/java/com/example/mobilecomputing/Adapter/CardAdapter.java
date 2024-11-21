package com.example.mobilecomputing.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Correct import for Glide
import com.example.mobilecomputing.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> items;

    // Constructor
    public CardAdapter(List<CardItem> items) {
        if (items != null) {
            this.items = items;
        } else {
            throw new IllegalArgumentException("Item list cannot be null");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current item
        CardItem item = items.get(position);

        // Bind data to the TextView
        holder.textView.setText(item.getText());

        // Load image into the ImageView using Glide
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.blue) // Optional placeholder image
                .error(R.drawable.uploadimg)             // Optional error image
                .into(holder.imageView);
    }

    // Update the list with new items and refresh the RecyclerView
    public void updateItems(List<CardItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.card_text);   // Replace with actual ID
            imageView = itemView.findViewById(R.id.card_image); // Replace with actual ID
        }
    }
}
