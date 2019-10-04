package com.example.hostelfinderandroidapp.model;

public class User {
    String userId, userName, phone, email, imageUrl, accountStatus, accountType;

    public User() {
    }

    public User(String userId, String userName, String phone, String email, String imageUrl, String accountStatus, String accountType) {

        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.accountStatus = accountStatus;
        this.accountType = accountType;

    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getAccountType() {
        return accountType;
    }
}
