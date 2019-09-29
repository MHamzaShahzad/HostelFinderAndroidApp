package com.example.hostelfinderandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getName();

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        MyFirebaseUser.initAndSetUserValueEventListener(getApplicationContext());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        MyFirebaseUser.removeUserValueEventListener(getApplicationContext());
        super.onDestroy();
    }
}
