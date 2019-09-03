package com.example.hostelfinderandroidapp.user;


import android.content.Context;
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
import com.example.hostelfinderandroidapp.adapters.AdapterHostelsList;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentActiveHostelsList extends Fragment {

    private static final String TAG = FragmentActiveHostelsList.class.getName();
    Context context;
    View view;

    RecyclerView recycler_active_hostels_list;
    AdapterHostelsList adapterHostelsList;
    List<Hostel> list;
    ValueEventListener valueEventListener;

    public FragmentActiveHostelsList() {
        // Required empty public constructor
        list = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_fragment_active_hostels_list, container, false);

            recycler_active_hostels_list = view.findViewById(R.id.recycler_active_hostels_list);
            recycler_active_hostels_list.setHasFixedSize(true);
            recycler_active_hostels_list.setLayoutManager(new LinearLayoutManager(context));
            adapterHostelsList = new AdapterHostelsList(context, list);
            recycler_active_hostels_list.setAdapter(adapterHostelsList);

            initHostelsListListener();

        }
        return view;
    }

    private void initHostelsListListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> hostelsListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleHostel : hostelsListSnapshot) {

                    try {
                        Hostel hostel = singleHostel.getValue(Hostel.class);
                        if (hostel != null && hostel.getStatus().equals(Constants.HOSTEL_STATUS_ACTIVE)) {
                            list.add(hostel);
                            Log.e(TAG, "onDataChange: " + hostel.getHostelName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                adapterHostelsList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.HOSTELS_REFERENCE.addValueEventListener(valueEventListener);
    }


}
