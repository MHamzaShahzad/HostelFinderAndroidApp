package com.example.hostelfinderandroidapp.controlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hostelfinderandroidapp.model.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPushNotificationFirebase {

    private static final String TAG = SendPushNotificationFirebase.class.getName();

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" + "AAAATHdhPuQ:APA91bFQZTe0qpVP_tWNKrHDR9X4dKjDmAb50fVE1pEcZ1JIrl5O2rLtC7WJE_XXxB7vJRM3wzkLh506PkQJzq4ZuPQSp8B5McFGr_NQMdVNOW9oCxoZAtXGvoZKn49tnDnvqSV0IujE";
    private static final String contentType = "application/json";

    public static void buildAndSendNotification(Context context, String TOPIC_SEND_TO, String notificationTitle, String notificationMsg){
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", notificationTitle);
            notificationBody.put("message", notificationMsg);

            notification.put("to", "/topics/" + TOPIC_SEND_TO);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(context, notification);
    }

    private static void sendNotification(final Context context, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
