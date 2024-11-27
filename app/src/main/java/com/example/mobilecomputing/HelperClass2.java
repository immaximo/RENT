package com.example.mobilecomputing;

public class HelperClass2 {
    public HelperClass2() {
    }

    public HelperClass2(String full, String userBio, String userAddress, String userNumber, String userPhoto) {
        this.full = full;
        this.userBio = userBio;
        this.userAddress = userAddress;
        this.userNumber = userNumber;
        this.userPhoto = userPhoto;
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

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    // For Profile
    private String full;
    private String userBio;
    private String userAddress;
    private String userNumber;
    private String userPhoto;

}
