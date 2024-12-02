package com.example.mobilecomputing.Adapter;

public class CardItem {
    private String name;
    private String imageUrl;
    private String price;
    private String description;
    private String productId; // Added productId field

    // Constructor
    public CardItem(String name, String imageUrl, String price, String description, String productId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
        this.productId = productId; // Initialize productId
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getProductId() {
        return productId;
    }
}
