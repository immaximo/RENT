package com.example.mobilecomputing.Adapter;

public class CardItem {
    private String name;
    private String imageUrl;
    private String price;
    private String description;

    // Constructor without productId
    public CardItem(String name, String imageUrl, String price, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }

    // Getter methods (if needed)
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
}

