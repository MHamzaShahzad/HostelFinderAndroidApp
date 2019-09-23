package com.example.hostelfinderandroidapp.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.model.Hostel;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFilterHostelsList extends DialogFragment implements View.OnClickListener {

    Context context;
    View view;

    private HashMap<String, String> map;
    private Intent bradCastIntent;

    private Button btnReset, btnSearch;

    public FragmentFilterHostelsList() {
        // Required empty public constructor
    }

    public FragmentFilterHostelsList getInstance(){
        return new FragmentFilterHostelsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        map = new HashMap<>();
        bradCastIntent = new Intent(Constants.HOSTEL_INTENT_FILTER);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_filter_hostels_list, container, false);
            initLayoutWidgets();



        }
        return view;
    }

    private void sendFiltersMapBroadcast(){


        map.put(Hostel.COST_PER_PERSON_STRING, "2000");
        map.put(Hostel.MAX_MEMBERS_STRING, "4");
        map.put(Hostel.LOCALITY_STRING, "Faisalabad");
        map.put(Hostel.ADDRESS_STRING, "Gulberg");
        map.put(Hostel.TYPE_STRING, Constants.HOSTEL_FOR_BOYS);
        map.put(Hostel.IS_ELECTRICITY_BACKUP_AVAILABLE_STRING, Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE);
        map.put(Hostel.IS_INTERNET_AVAILABLE_STRING, Constants.HOSTEL_INTERNET_AVAILABLE);
        map.put(Hostel.IS_PARKING_AVAILABLE_STRING, Constants.HOSTEL_PARKING_AVAILABLE);


        bradCastIntent.putExtra(Constants.HOSTEL_FILTER_MAP, map);
        context.sendBroadcast(bradCastIntent);
    }

    private void initLayoutWidgets(){
        btnReset = view.findViewById(R.id.btnReset);
        btnSearch = view.findViewById(R.id.btnSearch);



        setCustomClickListeners();
    }

    private void setCustomClickListeners(){
        btnReset.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnReset:

                break;

            case R.id.btnSearch:
                sendFiltersMapBroadcast();
                break;

        }
    }
}
