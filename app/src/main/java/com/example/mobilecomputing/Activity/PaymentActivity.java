package com.example.mobilecomputing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilecomputing.Adapter.PaymentAdapter;
import com.example.mobilecomputing.Adapter.PaymentItem;
import com.example.mobilecomputing.R;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private PaymentAdapter cartAdapter;
    private List<PaymentItem> paymentItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);

        cartRecyclerView = findViewById(R.id.cartView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = getIntent().getStringExtra("name");
                String price = getIntent().getStringExtra("price");
                String imageUrl = getIntent().getStringExtra("imageUrl");


                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", name);
                returnIntent.putExtra("price", price);
                returnIntent.putExtra("imageUrl", imageUrl);

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        paymentItemList = new ArrayList<>();
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        if (name != null && price != null && imageUrl != null) {
            PaymentItem item = new PaymentItem(name, price, imageUrl);
            paymentItemList.add(item);
        }

        cartAdapter = new PaymentAdapter(this, paymentItemList);
        cartRecyclerView.setAdapter(cartAdapter);
    }
}
