package com.example.hostelfinderandroidapp.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackgroundJobService extends JobService {

    private static final String TAG = BackgroundJobService.class.getName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.d(TAG, "onStartJob: " );
        MyFirebaseUser.initAndSetUserValueEventListener(getApplicationContext());

        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: " );
        MyFirebaseUser.removeUserValueEventListener(getApplicationContext());
        return false;
    }

}
