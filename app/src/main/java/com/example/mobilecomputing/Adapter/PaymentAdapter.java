package com.example.mobilecomputing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilecomputing.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.CartViewHolder> {

    private Context context;
    private List<PaymentItem> paymentItemList;

    public PaymentAdapter(Context context, List<PaymentItem> cartItemList) {
        this.context = context;
        this.paymentItemList = cartItemList;
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
        holder.priceTextView.setText("Price: $" + item.getPrice());

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.uploadimg)
                .into(holder.itemImageView);
    }

    @Override
    public int getItemCount() {
        return paymentItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, priceTextView;
        public ImageView itemImageView;

        public CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            priceTextView = itemView.findViewById(R.id.item_price);
            itemImageView = itemView.findViewById(R.id.item_image);
        }
    }
}
