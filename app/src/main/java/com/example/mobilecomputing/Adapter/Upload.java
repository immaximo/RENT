package com.example.mobilecomputing.Adapter;

public class Upload {
    private String name;
    private String imageUrl;

    public Upload() {
        // Required for Firebase
    }

    public Upload(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
