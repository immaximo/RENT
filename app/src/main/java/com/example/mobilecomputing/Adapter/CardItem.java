package com.example.mobilecomputing.Adapter;

public class CardItem {
    private String text;
    private int imageUrl;

    public CardItem(String text, String s, int imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public int getImageUrl() {
        return imageUrl;
    }
}
