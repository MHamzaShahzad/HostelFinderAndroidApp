package com.example.hostelfinderandroidapp.model;

import java.io.Serializable;

public class Hostel implements Serializable {
    
    public static final String ID_STRING = "hostelId";
    public static final String NAME_STRING = "hostelName";
    public static final String AVAILABLE_ROOMS_STRING = "availableRooms";
    public static final String MAX_MEMBERS_STRING = "maxMembers";
    public static final String TOTAL_ROOMS_STRING = "totalRooms";
    public static final String COST_PER_PERSON_STRING = "costPerPerson";
    public static final String IS_INTERNET_AVAILABLE_STRING = "internetAvailable";
    public static final String IS_ELECTRICITY_BACKUP_AVAILABLE_STRING = "electricityBackup";
    public static final String IS_PARKING_AVAILABLE_STRING = "parking";
    public static final String DESCRIPTION_STRING = "description";
    public static final String PHONE_STRING = "phone";
    public static final String EMAIL_STRING = "email";
    public static final String OWNER_ID_STRING = "ownerId";
    public static final String TYPE_STRING = "type";
    public static final String IMAGE_URL_STRING = "imageUrl";
    public static final String LATITUDE_STRING = "lat";
    public static final String LONGITUDE_STRING = "lon";
    public static final String ADDRESS_STRING = "address";
    public static final String LOCALITY_STRING = "locality";
    public static final String STATUS_STRING = "status";
    public static final String DATE_STRING = "date";

    public String hostelId, hostelName, availableRooms, maxMembers, totalRooms, costPerPerson, internetAvailable, electricityBackup, parking, description, phone, email, ownerId, type, imageUrl, lat, lon, address, locality, status, date;

    public Hostel() {
    }

    public Hostel(String hostelId, String hostelName, String availableRooms, String maxMembers, String totalRooms, String costPerPerson, String internetAvailable, String electricityBackup, String parking, String description, String phone, String email, String ownerId, String type, String imageUrl, String lat, String lon, String address, String locality, String status, String date) {
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
        this.locality = locality;
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

    public String getLocality() {
        return locality;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

}
