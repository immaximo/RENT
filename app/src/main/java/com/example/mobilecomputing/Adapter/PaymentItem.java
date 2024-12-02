package com.example.mobilecomputing.Adapter;

public class PaymentItem {
    private String name;
    private String price;
    private String imageUrl;

    public PaymentItem(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
