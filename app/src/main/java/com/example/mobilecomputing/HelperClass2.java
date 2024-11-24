package com.example.mobilecomputing;

public class HelperClass2 {
    public HelperClass2() {
    }

    public HelperClass2(String full, String userBio, String userAddress) {
        this.full = full;
        this.userBio = userBio;
        this.userAddress = userAddress;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    // For Profile
    private String full;
    private String userBio;
    private String userAddress;

}
