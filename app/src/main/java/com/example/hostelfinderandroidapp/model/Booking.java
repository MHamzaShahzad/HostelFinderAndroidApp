package com.example.hostelfinderandroidapp.model;

import java.io.Serializable;

public class Booking implements Serializable {

    public static final String STRING_BOOKING_STATUS = "bookingStatus";

    String userId, bookingHostelId, bookingStatus, userName, userCNIC, userPhoneNumber, bookingMessage, bookFrom, bookTill,uploadedAt, instituteBelongsTo, userBelongsTo;

    public Booking() {
    }

    public Booking(String userId, String bookingHostelId, String bookingStatus, String userName, String userCNIC, String userPhoneNumber, String bookingMessage, String bookFrom, String bookTill, String uploadedAt, String instituteBelongsTo, String userBelongsTo) {
        this.userId = userId;
        this.userName = userName;
        this.bookingStatus = bookingStatus;
        this.userCNIC = userCNIC;
        this.userPhoneNumber = userPhoneNumber;
        this.bookingHostelId = bookingHostelId;
        this.bookingMessage = bookingMessage;
        this.bookFrom = bookFrom;
        this.bookTill = bookTill;
        this.instituteBelongsTo = instituteBelongsTo;
        this.userBelongsTo = userBelongsTo;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserCNIC() {
        return userCNIC;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getBookingHostelId() {
        return bookingHostelId;
    }

    public String getBookingMessage() {
        return bookingMessage;
    }

    public String getBookFrom() {
        return bookFrom;
    }

    public String getBookTill() {
        return bookTill;
    }

    public String getInstituteBelongsTo() {
        return instituteBelongsTo;
    }

    public String getUserBelongsTo() {
        return userBelongsTo;
    }

}
