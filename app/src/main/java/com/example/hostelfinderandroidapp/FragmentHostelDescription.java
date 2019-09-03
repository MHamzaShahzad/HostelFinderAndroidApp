package com.example.hostelfinderandroidapp;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.controlers.MyPrefLocalStorage;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHostelDescription extends Fragment {


    private static final String TAG = FragmentHostelDescription.class.getName();
    Context context;
    View view;
    Bundle bundleArgument;
    Button btnActiveInActiveHostel, send_sms_to_owner, call_to_owner, btnEditHostel, btnRemoveHostel;
    Hostel hostel;
    ImageView hostelImage;
    TextView hostelNamePlace, hostelAddressPlace, hostelAvailableRoomsPlace, hostelCostPerMemberPlace, hostelMaxMembersPerRoomPlace, hostelOwnerEmailPlace, hostelDescriptionPlace;
    LinearLayout layout_call_sms_hostel, layout_edit_remove_hostel;

    private static final String HOSTEL_BUTTON_TEXT_ACTIVE = "Active";
    private static final String HOSTEL_BUTTON_TEXT_INACTIVE = "In-Active";


    public FragmentHostelDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hostel_description, container, false);

            hostelImage = view.findViewById(R.id.hostelImage);
            hostelNamePlace = view.findViewById(R.id.hostelNamePlace);
            hostelAddressPlace = view.findViewById(R.id.hostelAddressPlace);
            hostelAvailableRoomsPlace = view.findViewById(R.id.hostelAvailableRoomsPlace);
            hostelCostPerMemberPlace = view.findViewById(R.id.hostelCostPerMemberPlace);
            hostelMaxMembersPerRoomPlace = view.findViewById(R.id.hostelMaxMembersPerRoomPlace);
            hostelOwnerEmailPlace = view.findViewById(R.id.hostelOwnerEmailPlace);
            hostelDescriptionPlace = view.findViewById(R.id.hostelDescriptionPlace);
            btnActiveInActiveHostel = view.findViewById(R.id.btnActiveInActiveHostel);

            layout_call_sms_hostel = view.findViewById(R.id.layout_call_sms_hostel);
            layout_edit_remove_hostel = view.findViewById(R.id.layout_edit_remove_hostel);

            send_sms_to_owner = view.findViewById(R.id.send_sms_to_owner);
            call_to_owner = view.findViewById(R.id.call_to_owner);
            btnEditHostel = view.findViewById(R.id.btnEditHostel);
            btnRemoveHostel = view.findViewById(R.id.btnRemoveHostel);

            checkIfAdmin();

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


                    setTextToButton();
                }
            }


            btnActiveInActiveHostel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (hostel != null){
                        if (hostel.getStatus().equals(Constants.HOSTEL_STATUS_INACTIVE)){
                            changeHostelStatus(Constants.HOSTEL_STATUS_ACTIVE);
                        }
                        if (hostel.getStatus().equals(Constants.HOSTEL_STATUS_ACTIVE)){
                            changeHostelStatus(Constants.HOSTEL_STATUS_INACTIVE);
                        }

                    }

                }
            });

        }
        return view;
    }

    private void checkIfAdmin(){
        if (MyPrefLocalStorage.getCurrentUserData(context).getAccountType().equals(Constants.ACCOUNT_TYPE_ADMIN)) {
            btnActiveInActiveHostel.setVisibility(View.VISIBLE);
            Log.e(TAG, "checkIfAdmin: "+ MyPrefLocalStorage.getCurrentUserData(context).getAccountType() );

        }
    }

    private void setTextToButton(){
        if (hostel.getStatus().equals(Constants.HOSTEL_STATUS_INACTIVE)){
            btnActiveInActiveHostel.setText(HOSTEL_BUTTON_TEXT_ACTIVE);

        }
        if (hostel.getStatus().equals(Constants.HOSTEL_STATUS_ACTIVE)){
            btnActiveInActiveHostel.setText(HOSTEL_BUTTON_TEXT_INACTIVE);

        }
    }

    private void changeHostelStatus(String hostelStatus) {

        MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostel.getHostelId()).child("status").setValue(hostelStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Status Changed", Toast.LENGTH_SHORT).show();
                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
