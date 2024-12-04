package com.example.mobilecomputing.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.CartViewHolder> {

    private static final String TAG = "PaymentAdapter";
    private Context context;
    private List<PaymentItem> paymentItemList;
    private QuantityChangeListener quantityChangeListener;

    // Interface for QuantityChangeListener
    public interface QuantityChangeListener {
        void onQuantityChanged(int position, int newQuantity);
    }

    // Constructor with listener
    public PaymentAdapter(Context context, List<PaymentItem> cartItemList, QuantityChangeListener listener) {
        this.context = context;
        this.paymentItemList = cartItemList;
        this.quantityChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_cart_view, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        PaymentItem item = paymentItemList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.priceTextView.setText("Price: ₱" + String.format("%.2f", item.getPrice()));
        holder.itemTotalTextView.setText(": ₱" + String.format("%.2f", item.getTotalPrice()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.uploadimg)
                .into(holder.itemImageView);

        holder.increaseButton.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            int newQuantity = currentQuantity + 1;

            // Update the item with the new quantity and total price
            item.setQuantity(newQuantity);
            item.setTotalPrice(item.getPrice() * newQuantity);

            // Notify the adapter to refresh the item view
            notifyItemChanged(position);
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged(position, newQuantity);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;

                // Update the item with the new quantity and total price
                item.setQuantity(newQuantity);
                item.setTotalPrice(item.getPrice() * newQuantity);

                // Notify the adapter to refresh the item view
                notifyItemChanged(position);
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(position, newQuantity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, priceTextView, quantityTextView, itemTotalTextView;
        public ImageView itemImageView;
        public AppCompatButton increaseButton, decreaseButton;

        public CartViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.item_name);
            priceTextView = itemView.findViewById(R.id.item_price);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            itemImageView = itemView.findViewById(R.id.item_image);
            increaseButton = itemView.findViewById(R.id.quantity_increase);
            decreaseButton = itemView.findViewById(R.id.quantity_decrease);
            itemTotalTextView = itemView.findViewById(R.id.item_total);
        }
    }
}
