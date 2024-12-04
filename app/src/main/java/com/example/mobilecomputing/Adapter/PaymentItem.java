package com.example.mobilecomputing.Adapter;

public class PaymentItem {
    private String name;
    private double price; // Changed to double for calculations
    private String imageUrl;
    private int quantity;
    private double totalPrice;

    public PaymentItem(String name, double price, String imageUrl, int quantity) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerUnit() {
        return price;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
