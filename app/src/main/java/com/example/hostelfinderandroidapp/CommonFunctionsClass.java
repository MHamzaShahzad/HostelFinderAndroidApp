package com.example.hostelfinderandroidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.firestore.admin.v1beta1.Progress;

public class CommonFunctionsClass {

    private static final String TAG = CommonFunctionsClass.class.getName();

    public static void setSend_sms_to_owner(Context context, String phoneNumber) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", "Body of Message");
        ((Activity) context).startActivity(smsIntent);
    }

    public static void setCall_to_owner(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        // Send phone number to intent as data
        intent.setData(Uri.parse("tel:" + phoneNumber));
        // Start the dialer app activity with number
        ((Activity) context).startActivity(intent);
    }

    public static void setBtn_view_on_map(Context context, final Hostel hostel) {
        if (hostel.getLat() != null && hostel.getLat().length() > 0 && hostel.getLon() != null && hostel.getLon().length() > 0) {
            Bundle bundle = new Bundle();
            FragmentMap map = new FragmentMap();
            bundle.putBoolean(Constants.HOSTEL_LOCATION_MAP, true);
            bundle.putDouble(Constants.LOCATION_LATITUDE, Double.parseDouble(hostel.getLat()));
            bundle.putDouble(Constants.LOCATION_LONGITUDE, Double.parseDouble(hostel.getLon()));
            map.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, map).addToBackStack(null).commit();
        }else
            Toast.makeText(context, "No Location Found for Map!",Toast.LENGTH_LONG).show();
    }

    public static void hideProgressBar(ProgressBar progressBar) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    public static void stopSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    public static void startSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
    }

    public static void showCustomDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
            }
        });
        builder.create().show();
    }

    public static void setTextToNoItemFound(Context context, int id, String text){
        try {
            TextView textView = (TextView) ((FragmentActivity) context).findViewById(id);
            textView.setText(text);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showNoItemFoundText(Context context, int id){
        Log.e(TAG, "showNoItemFoundText: "+ id );
        try {
            ((FragmentActivity) context).findViewById(id).setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideNoItemFoundText(Context context, int id){
        Log.e(TAG, "hideNoItemFoundText: "+ id );
        try {
            ((FragmentActivity) context).findViewById(id).setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
