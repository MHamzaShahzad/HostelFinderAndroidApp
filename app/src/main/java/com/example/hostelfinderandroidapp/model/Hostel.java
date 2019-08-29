package com.example.hostelfinderandroidapp.model;

public class Hostel {

    private String hostelName, availableRooms, maxMembers, totalRooms,costPerPerson,internetAvailable,electricityBackup,parking,description, phoneNumber,email, ownerId, type, imageUrl, lat, lon, address;

    public Hostel() {
    }

    public Hostel(String hostelName, String availableRooms, String maxMembers, String totalRooms, String costPerPerson, String description, String phoneNumber, String email, String ownerId, String type, String imageUrl, String lat, String lon, String address) {
        this.hostelName = hostelName;
        this.availableRooms = availableRooms;
        this.maxMembers = maxMembers;
        this.totalRooms = totalRooms;
        this.costPerPerson = costPerPerson;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.ownerId = ownerId;
        this.type = type;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public String getHostelName() {
        return hostelName;
    }

    public String getAvailableRooms() {
        return availableRooms;
    }

    public String getMaxMembers() {
        return maxMembers;
    }

    public String getTotalRooms() {
        return totalRooms;
    }

    public String getCostPerPerson() {
        return costPerPerson;
    }

    public String getInternetAvailable() {
        return internetAvailable;
    }

    public String getElectricityBackup() {
        return electricityBackup;
    }

    public String getParking() {
        return parking;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }
}
