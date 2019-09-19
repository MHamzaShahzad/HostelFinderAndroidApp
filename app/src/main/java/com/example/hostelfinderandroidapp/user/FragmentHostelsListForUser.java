package com.example.hostelfinderandroidapp.user;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.adapters.AdapterHostelsListForUsers;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHostelsListForUser extends Fragment {

    private static final String TAG = FragmentHostelsListForUser.class.getName();
    Context context;
    View view;

    RecyclerView recycler_active_hostels_list;
    AdapterHostelsListForUsers adapterHostelsListForUser;
    List<Hostel> list;

    private static ValueEventListener valueEventListener;
    private static HashMap<String, String> mapFilter;
    private BroadcastReceiver filtersReceiver;

    public FragmentHostelsListForUser() {
        // Required empty public constructor
        list = new ArrayList<>();
        mapFilter = new HashMap<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hostels_list_for_user, container, false);

            recycler_active_hostels_list = view.findViewById(R.id.recycler_active_hostels_list);
            recycler_active_hostels_list.setHasFixedSize(true);
            recycler_active_hostels_list.setLayoutManager(new LinearLayoutManager(context));
            adapterHostelsListForUser = new AdapterHostelsListForUsers(context, list);
            recycler_active_hostels_list.setAdapter(adapterHostelsListForUser);

            initHostelsListListener(mapFilter);
            initFiltersReceiver();
        }
        return view;
    }

    private void initFiltersReceiver() {
        filtersReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                removeHostelValueEventListener();
                initHostelsListListener(mapFilter);
            }
        };
        context.registerReceiver(filtersReceiver, new IntentFilter(Constants.HOSTEL_INTENT_FILTER));
    }

    private void initHostelsListListener(final HashMap<String, String> map) {

        /*final HashMap<String, String> map = new HashMap<>();
        map.put(Hostel.LOCALITY_STRING, "Faisalabad");
        map.put(Hostel.IS_ELECTRICITY_BACKUP_AVAILABLE_STRING, Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE);
        map.put(Hostel.IS_INTERNET_AVAILABLE_STRING, Constants.HOSTEL_INTERNET_AVAILABLE);
        map.put(Hostel.IS_PARKING_AVAILABLE_STRING, Constants.HOSTEL_PARKING_AVAILABLE);*/

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> hostelsListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleHostel : hostelsListSnapshot) {

                    try {
                        Hostel hostel = singleHostel.getValue(Hostel.class);
                        if (hostel != null && hostel.getStatus().equals(Constants.HOSTEL_STATUS_ACTIVE)) {

                            boolean matches = true;

                            if (map.size() > 0) {
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    Log.e(TAG, "onDataChange: " + hostel.getClass().getDeclaredField(entry.getKey()).get(hostel));

                                    switch (entry.getKey()){
                                        case Hostel.MAX_MEMBERS_STRING:
                                            break;
                                        case Hostel.COST_PER_PERSON_STRING:
                                            break;
                                        case Hostel.DATE_STRING:
                                            break;

                                    }

                                    if (!entry.getValue().equalsIgnoreCase(String.valueOf(hostel.getClass().getDeclaredField(entry.getKey()).get(hostel)))) {
                                        matches = false;
                                        break;
                                    }
                                }
                                if (matches)
                                    list.add(hostel);
                            } else
                                list.add(hostel);
                            Log.e(TAG, "onDataChange: " + hostel.getHostelName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                adapterHostelsListForUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabase.HOSTELS_REFERENCE.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeHostelValueEventListener();

        if (filtersReceiver != null)
            context.unregisterReceiver(filtersReceiver);

    }

    private void removeHostelValueEventListener() {
        if (valueEventListener != null)
            MyFirebaseDatabase.HOSTELS_REFERENCE.removeEventListener(valueEventListener);
    }

}
