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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hostelfinderandroidapp.CommonFunctionsClass;
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

public class FragmentHostelsListForUser extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FragmentHostelsListForUser.class.getName();
    private Context context;
    private View view;

    private Button btnShowFilters;
    private RecyclerView recycler_active_hostels_list;
    private AdapterHostelsListForUsers adapterHostelsListForUser;
    private List<Hostel> list;

    private static ValueEventListener valueEventListener;
    private static HashMap<String, String> mapFilter;
    private BroadcastReceiver filtersReceiver;

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

            btnShowFilters = view.findViewById(R.id.btnShowFilters);
            recycler_active_hostels_list = view.findViewById(R.id.recycler_active_hostels_list);
            recycler_active_hostels_list.setHasFixedSize(true);
            recycler_active_hostels_list.setLayoutManager(new LinearLayoutManager(context));
            adapterHostelsListForUser = new AdapterHostelsListForUsers(context, list);
            recycler_active_hostels_list.setAdapter(adapterHostelsListForUser);

            setBtnShowFilters();
            initSwipeRefreshLayout();
            initFiltersReceiver();
        }
        return view;
    }

    private void initFiltersReceiver() {
        filtersReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    mapFilter = (HashMap<String, String>) intent.getSerializableExtra(Constants.HOSTEL_FILTER_MAP);
                    removeHostelValueEventListener();
                    initHostelsListListener(mapFilter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        context.registerReceiver(filtersReceiver, new IntentFilter(Constants.HOSTEL_INTENT_FILTER));
    }

    private void initHostelsListListener(final HashMap<String, String> map) {


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

                                    switch (entry.getKey()) {

                                        case Hostel.MAX_MEMBERS_STRING:

                                            try {
                                                if (Integer.parseInt(hostel.getMaxMembers()) > Integer.parseInt(entry.getValue())) {
                                                    matches = false;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;

                                        case Hostel.COST_PER_PERSON_STRING:

                                            try {
                                                if (Integer.parseInt(hostel.getCostPerPerson()) > Integer.parseInt(entry.getValue())) {
                                                    matches = false;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case Hostel.ADDRESS_STRING:

                                            if (!hostel.getAddress().toLowerCase().contains(entry.getValue().toLowerCase()) ) {
                                                matches = false;
                                            }
                                            break;

                                        default:

                                            if (!entry.getValue().equalsIgnoreCase(String.valueOf(hostel.getClass().getDeclaredField(entry.getKey()).get(hostel)))) {
                                                matches = false;
                                                break;
                                            }
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

                CommonFunctionsClass.stopSwipeRefreshLayout(mSwipeRefreshLayout);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                CommonFunctionsClass.stopSwipeRefreshLayout(mSwipeRefreshLayout);
                CommonFunctionsClass.showCustomDialog(context, databaseError.getMessage());

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

    private void initSwipeRefreshLayout() {
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                CommonFunctionsClass.startSwipeRefreshLayout(mSwipeRefreshLayout);

                // Fetching data from server
                initHostelsListListener(mapFilter);
            }
        });
    }

    private void setBtnShowFilters() {
        btnShowFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentFilterHostelsList()).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onRefresh() {
        removeHostelValueEventListener();
        initHostelsListListener(mapFilter);
    }

}
