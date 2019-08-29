package com.example.hostelfinderandroidapp.model;

public class User {
    String userName,phone,email,imageUrl,accountStatus,accountType,hostelId;

    public User() {
    }

    public User(String userName, String phone, String email, String imageUrl, String accountStatus, String accountType, String hostelId) {

        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.accountStatus = accountStatus;
        this.accountType = accountType;
        this.hostelId = hostelId;

    }

    public User(String userName, String phone, String email, String imageUrl, String accountType) {

        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.accountType = accountType;

    }
}
