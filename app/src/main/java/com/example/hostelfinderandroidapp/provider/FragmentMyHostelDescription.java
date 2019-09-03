package com.example.hostelfinderandroidapp.provider;


import android.content.Context;
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

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.FragmentBecomeHostelOwner;
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
    TextView hostelNamePlace, hostelAddressPlace, hostelAvailableRoomsPlace, hostelCostPerMemberPlace, hostelMaxMembersPerRoomPlace, hostelOwnerEmailPlace, hostelDescriptionPlace;
    LinearLayout layout_edit_remove_hostel;

    public FragmentMyHostelDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_hostel_description, container, false);

            hostelImage = view.findViewById(R.id.hostelImage);
            hostelNamePlace = view.findViewById(R.id.hostelNamePlace);
            hostelAddressPlace = view.findViewById(R.id.hostelAddressPlace);
            hostelAvailableRoomsPlace = view.findViewById(R.id.hostelAvailableRoomsPlace);
            hostelCostPerMemberPlace = view.findViewById(R.id.hostelCostPerMemberPlace);
            hostelMaxMembersPerRoomPlace = view.findViewById(R.id.hostelMaxMembersPerRoomPlace);
            hostelOwnerEmailPlace = view.findViewById(R.id.hostelOwnerEmailPlace);
            hostelDescriptionPlace = view.findViewById(R.id.hostelDescriptionPlace);

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
            FragmentBecomeHostelOwner becomeHostelOwner = new FragmentBecomeHostelOwner();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EDIT_MY_HOSTEL_BUNDLE_NAME, hostel);
            becomeHostelOwner.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, becomeHostelOwner).addToBackStack(null).commit();
        }
    }

    private void removeMyHostel() {
        if (hostel != null)
            MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostel.getHostelId()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                }
            });
    }

}