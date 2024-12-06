package com.example.mobilecomputing.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Activity.ItemDetailsActivity;
import com.example.mobilecomputing.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> items;
    private Context context;

    // Constructor
    public CardAdapter(Context context, List<CardItem> items) {
        if (items != null) {
            this.items = items;
        } else {
            throw new IllegalArgumentException("Item list cannot be null");
        }
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem product = items.get(position);

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText("Price: ₱" + product.getPrice());
        holder.descriptionTextView.setText(product.getDescription());

        Glide.with(holder.imageView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.blue)
                .error(R.drawable.uploadimg)
                .into(holder.imageView);

        // Set click listener to pass product details to ItemDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("imageUrl", product.getImageUrl());
            intent.putExtra("productId", product.getProductId()); // Add productId
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Method to update items
    public void updateItems(List<CardItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

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
