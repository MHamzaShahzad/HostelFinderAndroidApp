package com.example.hostelfinderandroidapp.controlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hostelfinderandroidapp.MainActivity;
import com.example.hostelfinderandroidapp.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MyFirebaseUser {

    private static String TAG = MyFirebaseUser.class.getName();

    private static ValueEventListener userValueEventListener;

    public static FirebaseAuth mAuth;
    public static FirebaseUser mUser;

    public MyFirebaseUser() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static void SignOut(final Context context) {
        MyServicesControllerClass.stopCustomBackgroundService(context.getApplicationContext());
        AuthUI.getInstance()
                .signOut(context.getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }
                });
    }

    public static void initAndSetUserValueEventListener(final Context context) {
        Log.e(TAG, "initAndSetUserValueEventListener: " + MyFirebaseUser.mUser.getUid());

        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot);
                if (dataSnapshot.getValue() != null)
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && MyPrefLocalStorage.getCurrentUserData(context).getAccountType() != null) {
                            if (!user.getAccountType().equals(MyPrefLocalStorage.getCurrentUserData(context).getAccountType())) {
                                //MyFirebaseUser.SignOut(context);
                            }
                        }
                        MyPrefLocalStorage.saveCurrentUserData(context, user);
                        Log.e(TAG, "onDataChange: PREF_USER_TYPE : " + MyPrefLocalStorage.getCurrentUserData(context).getAccountType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabase.USER_REFERENCE.child(MyFirebaseUser.mUser.getUid()).addValueEventListener(userValueEventListener);
    }

    public static void removeUserValueEventListener() {

        Log.d(TAG, "removeUserValueEventListener: ");

        if (userValueEventListener != null)
            MyFirebaseDatabase.USER_REFERENCE.child(MyFirebaseUser.mUser.getUid()).removeEventListener(userValueEventListener);
        MyPrefLocalStorage.clearPreferences();
    }


}
