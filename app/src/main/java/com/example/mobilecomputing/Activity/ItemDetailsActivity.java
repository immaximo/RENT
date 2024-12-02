package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.mobilecomputing.Dashboard;
import com.example.mobilecomputing.R;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private ImageView itemImageView;
    private Button confirmButton;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        nameTextView = findViewById(R.id.item_name);
        priceTextView = findViewById(R.id.item_price);
        descriptionTextView = findViewById(R.id.item_description);
        itemImageView = findViewById(R.id.item_image);
        confirmButton = findViewById(R.id.confirm_button);
        backArrow = findViewById(R.id.back_arrow);

        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        nameTextView.setText(name);
        priceTextView.setText("Price: $" + price);
        descriptionTextView.setText(description);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.uploadimg)
                .into(itemImageView);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, PaymentActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("price", price);
                intent.putExtra("description", description);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");


        if (price == null || price.isEmpty()) {
            price = "Price not available";
        }

        nameTextView.setText(name);
        priceTextView.setText("Price: $" + price);
        descriptionTextView.setText(description);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.uploadimg)
                .into(itemImageView);
    }
}
