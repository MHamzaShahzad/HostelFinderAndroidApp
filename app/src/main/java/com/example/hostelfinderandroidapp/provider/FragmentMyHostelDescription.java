package com.example.hostelfinderandroidapp.provider;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.hostelfinderandroidapp.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.FragmentBecomeHostelOwner;
import com.example.hostelfinderandroidapp.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

public class FragmentMyHostelDescription extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentMyHostelDescription.class.getName();
    Context context;
    View view;
    Bundle bundleArgument;
    Hostel hostel;
    Button btnEditHostel, btnRemoveHostel;
    ImageView hostelImage;
    TextView hostelAvailableForPlace, hostelInternetAvailablePlace, hostelParkingAvailablePlace, hostelElectricityBackupAvailablePlace, hostelCityPlace, hostelUpdatedAtPlace, btn_view_on_map, hostelNamePlace, hostelAddressPlace, hostelAvailableRoomsPlace, hostelCostPerMemberPlace, hostelMaxMembersPerRoomPlace, hostelOwnerEmailPlace, hostelDescriptionPlace;
    LinearLayout layout_edit_remove_hostel;

    private FragmentInteractionListenerInterface mListener;

    public FragmentMyHostelDescription() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (mListener != null) {
            mListener.onFragmentInteraction("Description");
        }

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_hostel_description, container, false);

            btn_view_on_map = view.findViewById(R.id.btn_view_on_map);
            hostelImage = view.findViewById(R.id.hostelImage);
            hostelNamePlace = view.findViewById(R.id.hostelNamePlace);
            hostelAddressPlace = view.findViewById(R.id.hostelAddressPlace);
            hostelAvailableRoomsPlace = view.findViewById(R.id.hostelAvailableRoomsPlace);
            hostelCostPerMemberPlace = view.findViewById(R.id.hostelCostPerMemberPlace);
            hostelMaxMembersPerRoomPlace = view.findViewById(R.id.hostelMaxMembersPerRoomPlace);
            hostelOwnerEmailPlace = view.findViewById(R.id.hostelOwnerEmailPlace);
            hostelDescriptionPlace = view.findViewById(R.id.hostelDescriptionPlace);

            hostelCityPlace = view.findViewById(R.id.hostelCityPlace);
            hostelAvailableForPlace = view.findViewById(R.id.hostelAvailableForPlace);
            hostelInternetAvailablePlace = view.findViewById(R.id.hostelInternetAvailablePlace);
            hostelParkingAvailablePlace = view.findViewById(R.id.hostelParkingAvailablePlace);
            hostelElectricityBackupAvailablePlace = view.findViewById(R.id.hostelElectricityBackupAvailablePlace);
            hostelUpdatedAtPlace = view.findViewById(R.id.hostelUpdatedAtPlace);

            layout_edit_remove_hostel = view.findViewById(R.id.layout_edit_remove_hostel);

            btnRemoveHostel = view.findViewById(R.id.btnRemoveHostel);
            btnEditHostel = view.findViewById(R.id.btnEditHostel);

            btnRemoveHostel.setOnClickListener(this);
            btnEditHostel.setOnClickListener(this);


            bundleArgument = getArguments();
            if (bundleArgument != null && bundleArgument.getSerializable(Constants.HOSTEL_DESCRIPTION_NAME) != null) {
                hostel = (Hostel) bundleArgument.getSerializable(Constants.HOSTEL_DESCRIPTION_NAME);
                if (hostel != null) {
                    if (hostel.getImageUrl() != null)
                        try {
                            Picasso.get().load(hostel.getImageUrl()).placeholder(R.drawable.placeholder_photos).fit().into(hostelImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    hostelAddressPlace.setText(hostel.getAddress());
                    hostelAvailableRoomsPlace.setText(hostel.getAvailableRooms());
                    hostelCostPerMemberPlace.setText(hostel.getCostPerPerson());
                    hostelNamePlace.setText(hostel.getHostelName());
                    hostelMaxMembersPerRoomPlace.setText(hostel.getMaxMembers());
                    hostelDescriptionPlace.setText(hostel.getDescription());

                    hostelCityPlace.setText(hostel.getLocality());
                    hostelOwnerEmailPlace.setText(hostel.getEmail());
                    hostelUpdatedAtPlace.setText(hostel.getDate());
                    hostelInternetAvailablePlace.setText((hostel.getInternetAvailable().equals(Constants.HOSTEL_INTERNET_AVAILABLE)) ? "Yes" : "No");
                    hostelParkingAvailablePlace.setText((hostel.getParking().equals(Constants.HOSTEL_PARKING_AVAILABLE)) ? "Yes" : "No");
                    hostelElectricityBackupAvailablePlace.setText((hostel.getElectricityBackup().equals(Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE)) ? "Yes" : "No");

                    switch (hostel.getType()) {
                        case Constants.HOSTEL_FOR_BOYS:
                            hostelAvailableForPlace.setText("Boys");
                            break;
                        case Constants.HOSTEL_FOR_GIRLS:
                            hostelAvailableForPlace.setText("Girls");
                            break;

                    }

                    btn_view_on_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonFunctionsClass.setBtn_view_on_map(context, hostel);
                        }
                    });

                }
            }

        }
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnEditHostel:
                editHostel();
                break;
            case R.id.btnRemoveHostel:
                removeMyHostel();
                break;

            default:

        }
    }

    private void editHostel(){
        if (hostel != null){
            FragmentEditMyHostelPost editMyHostelPost = new FragmentEditMyHostelPost();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EDIT_MY_HOSTEL_BUNDLE_NAME, hostel);
            editMyHostelPost.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, editMyHostelPost).addToBackStack(null).commit();
        }
    }

    private void removeMyHostel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Are you sure ? This will remove your hostel permanently!");
        builder.setPositiveButton("Yes, Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (hostel != null)
                    MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostel.getHostelId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                        }
                    });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
            mListener.onFragmentInteraction("Description");
        }
    }

}
