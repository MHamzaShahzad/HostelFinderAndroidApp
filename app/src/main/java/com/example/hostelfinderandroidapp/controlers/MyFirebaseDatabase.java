package com.example.hostelfinderandroidapp.controlers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabase {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static final DatabaseReference USER_REFERENCE = database.getReference("Users");
    public static final DatabaseReference HOSTELS_REFERENCE = database.getReference("Hostels");


}
