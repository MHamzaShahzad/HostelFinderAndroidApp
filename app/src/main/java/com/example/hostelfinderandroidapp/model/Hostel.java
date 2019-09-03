package com.example.hostelfinderandroidapp.model;

import java.io.Serializable;

public class Hostel implements Serializable {

    private String hostelId, hostelName, availableRooms, maxMembers, totalRooms, costPerPerson, internetAvailable, electricityBackup, parking, description, phone, email, ownerId, type, imageUrl, lat, lon, address, status, date;

    public Hostel() {
    }

    public Hostel(String hostelId, String hostelName, String availableRooms, String maxMembers, String totalRooms, String costPerPerson, String internetAvailable, String electricityBackup, String parking, String description, String phone, String email, String ownerId, String type, String imageUrl, String lat, String lon, String address, String status, String date) {
        this.hostelId = hostelId;
        this.hostelName = hostelName;
        this.availableRooms = availableRooms;
        this.maxMembers = maxMembers;
        this.totalRooms = totalRooms;
        this.costPerPerson = costPerPerson;
        this.internetAvailable = internetAvailable;
        this.electricityBackup = electricityBackup;
        this.parking = parking;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.ownerId = ownerId;
        this.type = type;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.status = status;
        this.date = date;
    }

    public String getHostelId() {
        return hostelId;
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

    public String getPhone() {
        return phone;
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

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

}
