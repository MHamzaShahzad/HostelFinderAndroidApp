package com.example.hostelfinderandroidapp.provider;


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
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBookingsProvider extends Fragment {

    private static final String TAG = FragmentBookingsProvider.class.getName();
    private Context context;
    private View view;

    private TabLayout bookingsTabs;
    private RecyclerView recyclerBookingsProvider;
    private AdapterBookings adapterBookings;
    private List<Booking> bookingList, bookingListTemp;

    private Hostel hostel;

    private ValueEventListener bookingRequestsValueEventListener;

    public FragmentBookingsProvider() {
        // Required empty public constructor
        bookingList = new ArrayList<>();
        bookingListTemp = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        adapterBookings = new AdapterBookings(context, bookingListTemp, true);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_bookings, container, false);

            bookingsTabs = view.findViewById(R.id.bookingsTabs);

            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_PENDING), true);
            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_ACCEPTED));
            bookingsTabs.addTab(bookingsTabs.newTab().setText(Constants.STRING_BOOKING_STATUS_REJECTED));
            setTabSelectedListener();

            recyclerBookingsProvider = view.findViewById(R.id.recyclerBookings);
            recyclerBookingsProvider.setHasFixedSize(true);
            recyclerBookingsProvider.setLayoutManager(new LinearLayoutManager(context));
            recyclerBookingsProvider.setAdapter(adapterBookings);

            loadData();
        }
        return view;
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

        Log.e(TAG, "getBookings: " + bookingListTemp.size());
        adapterBookings.notifyDataSetChanged();

    }

    private void loadData() {
        Bundle bundleArguments = getArguments();
        if (bundleArguments != null) {
            try {
                hostel = (Hostel) bundleArguments.getSerializable(Constants.HOSTEL_OBJECT);
                if (hostel != null) {
                    getBookingRequests(hostel.getHostelId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getBookingRequests(String hostelId) {
        bookingRequestsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshots) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            try {

                                Booking booking = snapshot.getValue(Booking.class);
                                if (booking != null) {
                                    Log.e(TAG, "onDataChange: " + snapshot);
                                    bookingList.add(booking);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    loadBookingsBasedOnTabSelected(bookingsTabs.getSelectedTabPosition());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.BOOKINGS_REFERENCE.child(hostelId).addValueEventListener(bookingRequestsValueEventListener);
    }

    private void removeBookingRequestsValueEventListener() {
        if (bookingRequestsValueEventListener != null) {
            MyFirebaseDatabase.BOOKINGS_REFERENCE.child(hostel.getHostelId()).removeEventListener(bookingRequestsValueEventListener);
        }
    }

    @Override
    public void onDestroy() {
        removeBookingRequestsValueEventListener();
        super.onDestroy();
    }
}
