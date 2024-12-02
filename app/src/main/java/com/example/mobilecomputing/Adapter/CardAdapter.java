package com.example.mobilecomputing.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        // Get the current product
        CardItem product = items.get(position);

        // Bind data to TextViews
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText("Price: $" + product.getPrice());
        holder.descriptionTextView.setText(product.getDescription());

        // Load image into the ImageView using Glide
        Glide.with(holder.imageView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.blue)  // Placeholder image
                .error(R.drawable.uploadimg)  // Error image
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Update the list with new items and refresh the RecyclerView
    public void updateItems(List<CardItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView descriptionTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.card_text);
            priceTextView = itemView.findViewById(R.id.card_price);
            descriptionTextView = itemView.findViewById(R.id.card_description);
            imageView = itemView.findViewById(R.id.card_image);
        }
    }
}
