package com.example.hostelfinderandroidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.hostelfinderandroidapp.model.Hostel;

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

    public static void setBtn_view_on_map(Context context, final Hostel hostel){
        if (hostel.getLat() != null && hostel.getLat().length() > 0 && hostel.getLon() != null && hostel.getLon().length() > 0 ){
            Bundle bundle = new Bundle();
            FragmentMap map = new FragmentMap();
            bundle.putBoolean(Constants.HOSTEL_LOCATION_MAP, true);
            bundle.putDouble(Constants.LOCATION_LATITUDE, Double.parseDouble(hostel.getLat()));
            bundle.putDouble(Constants.LOCATION_LONGITUDE, Double.parseDouble(hostel.getLon()));
            map.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, map).addToBackStack(null).commit();
        }
    }


}
