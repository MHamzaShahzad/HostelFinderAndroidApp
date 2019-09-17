package com.example.hostelfinderandroidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CommonFunctionsClass {

    public static void setSend_sms_to_owner(Context context, String phoneNumber){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body","Body of Message");
        ((Activity) context).startActivity(smsIntent);
    }

    public static void setCall_to_owner(Context context, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        // Send phone number to intent as data
        intent.setData(Uri.parse("tel:" + phoneNumber));
        // Start the dialer app activity with number
        ((Activity)context).startActivity(intent);
    }

}
