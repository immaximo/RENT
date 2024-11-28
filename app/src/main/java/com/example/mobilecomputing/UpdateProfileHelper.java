package com.example.mobilecomputing;

public class UpdateProfileHelper {
    String full;
    String userBio;
    String userPhoto;

    public UpdateProfileHelper(String full, String userBio, String userPhoto) {
        this.full = full;
        this.userBio = userBio;
        this.userPhoto = userPhoto;
    }

    public String getUpdatedFullName() {
        return full;
    }

    public void setUpdatedFullName(String full) {
        this.full = full;
    }

    public String getUpdatedAboutMe() {
        return userBio;
    }

    public void setUpdatedAboutMe(String userBio) {
        this.userBio = userBio;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public UpdateProfileHelper() {
    }
}
