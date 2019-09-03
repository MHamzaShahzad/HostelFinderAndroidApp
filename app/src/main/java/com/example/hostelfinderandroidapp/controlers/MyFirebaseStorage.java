package com.example.hostelfinderandroidapp.controlers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirebaseStorage {

    public static FirebaseStorage FIREBASE_STORAGE = FirebaseStorage.getInstance();
    public static StorageReference FIREBASE_STORAGE_ROOT_REFERENCE = FIREBASE_STORAGE.getReference();
    public static StorageReference HOSTELS_IMAGES_STORAGE_REFERENCE = FIREBASE_STORAGE_ROOT_REFERENCE.child("hostels_images/");
    public static StorageReference USERS_IMAGES_STORAGE_REFERENCE = FIREBASE_STORAGE_ROOT_REFERENCE.child("users_images/");

}
