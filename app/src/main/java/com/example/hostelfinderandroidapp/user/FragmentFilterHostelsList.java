package com.example.hostelfinderandroidapp.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    private RadioGroup radioGroupHostelFor;
    private RadioButton radioButtonHostelForBoys, radioButtonHostelForGirls;
    private EditText cityName, placeName, maxPrice, maxMembers;
    private CheckBox checkBoxIsInternetAvailable, checkBoxIsParkingAvailable, checkBoxIsElectricityBackupAvailable;

    public FragmentFilterHostelsList() {
        // Required empty public constructor
    }

    public FragmentFilterHostelsList getInstance() {
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

    private void sendFiltersMapBroadcast() {

        if (maxPrice.length() > 0)
            map.put(Hostel.COST_PER_PERSON_STRING, maxPrice.getText().toString());
        if (maxMembers.length() > 0)
            map.put(Hostel.MAX_MEMBERS_STRING, maxMembers.getText().toString());
        if (cityName.length() > 0)
            map.put(Hostel.LOCALITY_STRING, cityName.getText().toString());
        if (placeName.length() > 0)
            map.put(Hostel.ADDRESS_STRING, placeName.getText().toString());
        if (radioButtonHostelForBoys.isChecked())
            map.put(Hostel.TYPE_STRING, Constants.HOSTEL_FOR_BOYS);
        if (radioButtonHostelForGirls.isChecked())
            map.put(Hostel.TYPE_STRING, Constants.HOSTEL_FOR_GIRLS);
        if (checkBoxIsElectricityBackupAvailable.isChecked())
            map.put(Hostel.IS_ELECTRICITY_BACKUP_AVAILABLE_STRING, Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE);
        if (checkBoxIsInternetAvailable.isChecked())
            map.put(Hostel.IS_INTERNET_AVAILABLE_STRING, Constants.HOSTEL_INTERNET_AVAILABLE);
        if (checkBoxIsParkingAvailable.isChecked())
            map.put(Hostel.IS_PARKING_AVAILABLE_STRING, Constants.HOSTEL_PARKING_AVAILABLE);


        bradCastIntent.putExtra(Constants.HOSTEL_FILTER_MAP, map);
        context.sendBroadcast(bradCastIntent);
        ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
    }

    private void initLayoutWidgets() {
        btnReset = view.findViewById(R.id.btnReset);
        btnSearch = view.findViewById(R.id.btnSearch);

        radioGroupHostelFor = view.findViewById(R.id.radioGroupHostelFor);
        radioButtonHostelForBoys = view.findViewById(R.id.radioButtonHostelForBoys);
        radioButtonHostelForGirls = view.findViewById(R.id.radioButtonHostelForGirls);

        cityName = view.findViewById(R.id.cityName);
        placeName = view.findViewById(R.id.placeName);
        maxPrice = view.findViewById(R.id.maxPrice);
        maxMembers = view.findViewById(R.id.maxMembers);

        checkBoxIsInternetAvailable = view.findViewById(R.id.checkBoxIsInternetAvailable);
        checkBoxIsParkingAvailable = view.findViewById(R.id.checkBoxIsParkingAvailable);
        checkBoxIsElectricityBackupAvailable = view.findViewById(R.id.checkBoxIsElectricityBackupAvailable);

        setCustomClickListeners();

    }

    private void setCustomClickListeners() {
        btnReset.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnReset:
                setBtnReset();
                break;

            case R.id.btnSearch:
                sendFiltersMapBroadcast();
                break;

        }
    }

    private void setBtnReset(){

        cityName.setText("");
        placeName.setText("");
        maxMembers.setText("");
        maxPrice.setText("");

        checkBoxIsParkingAvailable.setChecked(false);
        checkBoxIsInternetAvailable.setChecked(false);
        checkBoxIsElectricityBackupAvailable.setChecked(false);

        radioButtonHostelForBoys.setChecked(false);
        radioButtonHostelForGirls.setChecked(false);

    }
}
