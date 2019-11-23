package com.example.hostelfinderandroidapp.user;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.adapters.AdapterBookings;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Booking;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentBookingsUser extends Fragment {

    private static final String TAG = FragmentBookingsUser.class.getName();
    private Context context;
    private View view;

    private TabLayout bookingsTabs;
    private RecyclerView recyclerMyBookings;
    private AdapterBookings adapterBookingsUser;
    private List<Booking> bookingList, bookingListTemp;

    private FirebaseUser firebaseUser;

    public FragmentBookingsUser() {
        // Required empty public constructor
        bookingList = new ArrayList<>();
        bookingListTemp = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        adapterBookingsUser = new AdapterBookings(context, bookingListTemp, false);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_bookings, container, false);


            bookingsTabs = view.findViewById(R.id.bookingsTabs);

            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_PENDING), true);
            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_ACCEPTED));
            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_REJECTED));
            setTabSelectedListener();

            recyclerMyBookings = view.findViewById(R.id.recyclerBookings);
            recyclerMyBookings.setLayoutManager(new LinearLayoutManager(context));
            recyclerMyBookings.setHasFixedSize(true);
            recyclerMyBookings.setAdapter(adapterBookingsUser);


            getMyBookings();
        }
        return view;
    }

    private void getMyBookings(){
        MyFirebaseDatabase.BOOKINGS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null){

                    for (DataSnapshot outersSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot innerSnapshot : outersSnapshot.getChildren()) {
                            if (innerSnapshot != null && innerSnapshot.getKey() != null && innerSnapshot.getKey().equals(firebaseUser.getUid()))
                                try {
                                    Booking booking = innerSnapshot.getValue(Booking.class);
                                    if (booking != null)
                                        bookingList.add(booking);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTabSelectedListener() {
        bookingsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBookingsBasedOnTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadBookingsBasedOnTabSelected(int position) {
        switch (position) {
            case 0:
                getBookings(Constants.BOOKING_STATUS_PENDING);
                break;
            case 1:
                getBookings(Constants.BOOKING_STATUS_ACCEPTED);
                break;
            case 2:
                getBookings(Constants.BOOKING_STATUS_REJECTED);
                break;
            default:
        }
    }

    private void getBookings(String status) {
        bookingListTemp.clear();

        for (final Booking booking : bookingList)
            if (booking != null && booking.getBookingStatus() != null && booking.getBookingStatus().equals(status))
                bookingListTemp.add(booking);

        Log.e(TAG, "getBookings: " + bookingListTemp.size() );
        adapterBookingsUser.notifyDataSetChanged();

    }

}
