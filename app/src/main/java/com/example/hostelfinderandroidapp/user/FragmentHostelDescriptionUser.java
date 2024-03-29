package com.example.hostelfinderandroidapp.user;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hostelfinderandroidapp.common.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FragmentHostelDescriptionUser extends Fragment {

    private static final String TAG = FragmentHostelDescriptionUser.class.getName();
    Context context;
    View view;
    Bundle bundleArgument;
    Button send_sms_to_owner, call_to_owner;
    Hostel hostel;
    ImageView hostelImage;
    TextView hostelAvailableForPlace, hostelInternetAvailablePlace, hostelParkingAvailablePlace,
            hostelElectricityBackupAvailablePlace, hostelCityPlace, hostelUpdatedAtPlace, btn_view_on_map,
            hostelNamePlace, hostelAddressPlace, hostelAvailableRoomsPlace, hostelCostPerMemberPlace,
            hostelMaxMembersPerRoomPlace, hostelOwnerEmailPlace, hostelDescriptionPlace, btn_book;
    LinearLayout layout_call_sms_hostel, layout_edit_remove_hostel;

    private FirebaseUser firebaseUser;

    private FragmentInteractionListenerInterface mListener;

    public FragmentHostelDescriptionUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        context = container.getContext();
        if (mListener != null) {
            mListener.onFragmentInteraction("Description");
        }
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hostel_description_user, container, false);

            btn_book = view.findViewById(R.id.btn_book);
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

            layout_call_sms_hostel = view.findViewById(R.id.layout_call_sms_hostel);
            layout_edit_remove_hostel = view.findViewById(R.id.layout_edit_remove_hostel);

            send_sms_to_owner = view.findViewById(R.id.send_sms_to_owner);
            call_to_owner = view.findViewById(R.id.call_to_owner);

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


                    send_sms_to_owner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonFunctionsClass.setSend_sms_to_owner(context, hostel.getPhone());

                        }
                    });
                    call_to_owner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonFunctionsClass.setCall_to_owner(context, hostel.getPhone());

                        }
                    });
                    btn_view_on_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonFunctionsClass.setBtn_view_on_map(context, hostel);
                        }
                    });

                    if (hostel.getOwnerId().equals(firebaseUser.getUid()))
                        btn_book.setVisibility(View.INVISIBLE);
                    else
                        btn_book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FragmentBookingForHostelRooms bookingForHostelRooms = new FragmentBookingForHostelRooms();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.HOSTEL_OBJECT, hostel);
                                bookingForHostelRooms.setArguments(bundle);
                                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, bookingForHostelRooms).addToBackStack(null).commit();
                            }
                        });
                }
            }
        }
        return view;
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
