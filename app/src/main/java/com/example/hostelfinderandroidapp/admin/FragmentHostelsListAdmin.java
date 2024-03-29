package com.example.hostelfinderandroidapp.admin;


import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.common.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.adapters.AdapterHostelsListAdmin;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
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
public class FragmentHostelsListAdmin extends Fragment {

    private static final String TAG = FragmentHostelsListAdmin.class.getName();
    Context context;
    View view;
    TabLayout tabLayout;
    RecyclerView recycler_hostels_list;
    List<Hostel> list, tempList;
    ValueEventListener valueEventListener;
    AdapterHostelsListAdmin adapterHostelsList;

    private ProgressBar progressBar;
    private FragmentInteractionListenerInterface mListener;

    public FragmentHostelsListAdmin() {
        // Required empty public constructor
        list = new ArrayList<>();
        tempList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (mListener != null) {
            mListener.onFragmentInteraction("Hostels");
        }
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hostels_list_admin, container, false);

            progressBar = view.findViewById(R.id.progressBar);

            recycler_hostels_list = view.findViewById(R.id.recycler_hostels_list);
            recycler_hostels_list.setHasFixedSize(true);
            recycler_hostels_list.setLayoutManager(new LinearLayoutManager(context));
            adapterHostelsList = new AdapterHostelsListAdmin(context, tempList);
            recycler_hostels_list.setAdapter(adapterHostelsList);


            initHostelsListListener();


            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            Toast.makeText(context, "In-Active", Toast.LENGTH_LONG).show();
                            getInActiveHostels();
                            break;
                        case 1:
                            Toast.makeText(context, "Active", Toast.LENGTH_LONG).show();
                            getActiveHostels();
                            break;
                        default:
                            Toast.makeText(context, "unKnown", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
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
                        if (hostel != null) {
                            list.add(hostel);
                            Log.e(TAG, "onDataChange: " + hostel.getHostelName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                initTabsLayout();

                CommonFunctionsClass.hideProgressBar(progressBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                CommonFunctionsClass.hideProgressBar(progressBar);
            }
        };
        MyFirebaseDatabase.HOSTELS_REFERENCE.addValueEventListener(valueEventListener);
    }

    private void initTabsLayout() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            getInActiveHostels();
        }
        if (tabLayout.getSelectedTabPosition() == 1) {
            getActiveHostels();
        }

        showHideNoItemFound();
    }

    private void getInActiveHostels() {

        tempList.clear();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus().equals(Constants.ACCOUNT_STATUS_INACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterHostelsList.notifyDataSetChanged();

    }

    private void getActiveHostels() {

        tempList.clear();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterHostelsList.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            MyFirebaseDatabase.HOSTELS_REFERENCE.removeEventListener(valueEventListener);
    }

    private void showHideNoItemFound(){
        if (tempList.size() == 0){
            CommonFunctionsClass.showNoItemFoundText(context, R.id.text_no_item_found);
        }else
            CommonFunctionsClass.hideNoItemFoundText(context, R.id.text_no_item_found);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onFragmentInteraction("Hostels");
        }
    }

}
