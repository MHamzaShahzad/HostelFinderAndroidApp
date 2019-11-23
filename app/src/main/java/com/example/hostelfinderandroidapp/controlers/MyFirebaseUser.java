package com.example.hostelfinderandroidapp.controlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.common.MainActivity;
import com.example.hostelfinderandroidapp.admin.AdminDrawerMainActivity;
import com.example.hostelfinderandroidapp.model.User;
import com.example.hostelfinderandroidapp.provider.ProviderDrawerMainActivity;
import com.example.hostelfinderandroidapp.user.DrawerMainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseUser {

    private static String TAG = MyFirebaseUser.class.getName();

    private static ValueEventListener userValueEventListener;

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser mUser = mAuth.getCurrentUser();


    public static void initAuthUser() {
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
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e(TAG, "onComplete: TOPIC_UNSUBSCRIBED_SUCCESSFULLY!" );
                            }
                        });
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

                        if (user != null) {

                            switch (user.getAccountType()) {
                                case Constants.ACCOUNT_TYPE_USER:
                                    DrawerMainActivity.setNavigationHeader(user);
                                    break;
                                case Constants.ACCOUNT_TYPE_ADMIN:
                                    AdminDrawerMainActivity.setNavigationHeader(user);
                                    break;
                                case Constants.ACCOUNT_TYPE_HOSTEL_OWNER:
                                    ProviderDrawerMainActivity.setNavigationHeader(user);
                                    break;
                            }

                            MyPrefLocalStorage.saveCurrentUserData(context, user);
                            Log.e(TAG, "onDataChange: PREF_USER_TYPE : " + MyPrefLocalStorage.getCurrentUserData(context) );

                        }
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

    public static void removeUserValueEventListener(Context context) {

        Log.d(TAG, "removeUserValueEventListener: ");

        if (userValueEventListener != null)
            MyFirebaseDatabase.USER_REFERENCE.child(MyFirebaseUser.mUser.getUid()).removeEventListener(userValueEventListener);
        MyPrefLocalStorage.clearPreferences(context);

    }


}
