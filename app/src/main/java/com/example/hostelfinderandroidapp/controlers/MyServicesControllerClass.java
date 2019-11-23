package com.example.hostelfinderandroidapp.controlers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.hostelfinderandroidapp.services.BackgroundJobService;
import com.example.hostelfinderandroidapp.services.BackgroundService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class MyServicesControllerClass {

    private static JobScheduler jobScheduler;

    public static void startCustomBackgroundService(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {

            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);

            ComponentName componentName = new ComponentName(context,
                    BackgroundJobService.class);

            JobInfo jobInfoObj = new JobInfo.Builder(1, componentName)
                    .setOverrideDeadline(0)
                    .setBackoffCriteria(1000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setPersisted(true)
                    .build();

            jobScheduler.schedule(jobInfoObj);

        } else {
            context.startService(new Intent(context, BackgroundService.class));
        }
    }

    public static void stopCustomBackgroundService(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (jobScheduler != null)
                jobScheduler.cancelAll();
        } else {
            context.stopService(new Intent(context, BackgroundService.class));
        }
    }

}
