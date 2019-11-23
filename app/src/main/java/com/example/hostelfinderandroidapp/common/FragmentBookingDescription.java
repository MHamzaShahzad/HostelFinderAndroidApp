package com.example.hostelfinderandroidapp.common;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.SendPushNotificationFirebase;
import com.example.hostelfinderandroidapp.model.Booking;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class FragmentBookingDescription extends Fragment {

    private static final String TAG = FragmentBookingDescription.class.getName();
    private Context context;
    private View view;

    private LinearLayout layout_accept_reject_booking_req, layout_edit_remove_booking_req;
    private ImageView hostelUserImage;
    private TextView hostelNamePlace, userNamePlace, phoneNumberPlace, belongsToPlace, institutePlace,
            bookFromPlace, bookTillPlace, requestedAtPlace, userCNICPlace, emailPlace, hostelAddressPlace,
            hostelCityPlace, bookingDescriptionPlace, btn_view_on_map;
    private Button btnAcceptRequest, btnRejectRequest, btnEditRequest, btnRemoveRequest;

    private FirebaseUser firebaseUser;
    private Booking booking;

    private boolean isProvider;

    public FragmentBookingDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_booking_description, container, false);

            initLayoutWidgets();
            loadData();
        }
        return view;
    }

    private void initLayoutWidgets() {

        layout_accept_reject_booking_req = view.findViewById(R.id.layout_accept_reject_booking_req);
        layout_edit_remove_booking_req = view.findViewById(R.id.layout_edit_remove_booking_req);

        hostelUserImage = view.findViewById(R.id.hostelUserImage);

        hostelNamePlace = view.findViewById(R.id.hostelNamePlace);
        userNamePlace = view.findViewById(R.id.userNamePlace);
        phoneNumberPlace = view.findViewById(R.id.phoneNumberPlace);
        belongsToPlace = view.findViewById(R.id.belongsToPlace);
        institutePlace = view.findViewById(R.id.institutePlace);
        bookFromPlace = view.findViewById(R.id.bookFromPlace);
        bookTillPlace = view.findViewById(R.id.bookTillPlace);
        requestedAtPlace = view.findViewById(R.id.requestedAtPlace);
        userCNICPlace = view.findViewById(R.id.userCNICPlace);
        emailPlace = view.findViewById(R.id.emailPlace);
        hostelAddressPlace = view.findViewById(R.id.hostelAddressPlace);
        hostelCityPlace = view.findViewById(R.id.hostelCityPlace);
        bookingDescriptionPlace = view.findViewById(R.id.bookingDescriptionPlace);
        btn_view_on_map = view.findViewById(R.id.btn_view_on_map);

        btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest);
        btnRejectRequest = view.findViewById(R.id.btnRejectRequest);
        btnEditRequest = view.findViewById(R.id.btnEditRequest);
        btnRemoveRequest = view.findViewById(R.id.btnRemoveRequest);
    }

    private void loadData() {

        Bundle bundleArguments = getArguments();
        if (bundleArguments != null) {

            try {

                isProvider = bundleArguments.getBoolean(Constants.STRING_IS_PROVIDER);
                booking = (Booking) bundleArguments.getSerializable(Constants.BOOKING_OBJECT);
                if (booking != null) {

                    userNamePlace.setText(booking.getUserName());
                    institutePlace.setText(booking.getInstituteBelongsTo());
                    bookFromPlace.setText(booking.getBookFrom());
                    bookTillPlace.setText(booking.getBookTill());
                    requestedAtPlace.setText(booking.getUploadedAt());
                    userCNICPlace.setText(booking.getUserCNIC());
                    bookingDescriptionPlace.setText(booking.getBookingMessage());
                    belongsToPlace.setText(CommonFunctionsClass.getUserBelongsTo(booking.getUserBelongsTo()));

                    getHostelDetails(booking.getBookingHostelId());
                    getUserDetails(booking.getUserId());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void getHostelDetails(final String hostelId) {
        MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        final Hostel hostel = dataSnapshot.getValue(Hostel.class);

                        if (hostel != null) {

                            if (isProvider) {

                                if (hostel.getOwnerId().equals(firebaseUser.getUid())) {
                                    layout_accept_reject_booking_req.setVisibility(View.VISIBLE);
                                    if (!booking.getBookingStatus().equals(Constants.BOOKING_STATUS_ACCEPTED)) {
                                        btnAcceptRequest.setEnabled(false);
                                        btnRejectRequest.setEnabled(false);

                                    }
                                    btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            changeBookingStatus(Constants.BOOKING_STATUS_ACCEPTED);
                                        }
                                    });

                                    btnRejectRequest.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            changeBookingStatus(Constants.BOOKING_STATUS_REJECTED);
                                        }
                                    });
                                }

                            } else {
                                Picasso.get().load(hostel.getImageUrl())
                                        .error(R.drawable.placeholder_photos)
                                        .placeholder(R.drawable.placeholder_photos)
                                        .centerInside().fit()
                                        .into(hostelUserImage);
                                phoneNumberPlace.setText(hostel.getPhone());
                                emailPlace.setText(hostel.getEmail());
                            }

                            hostelNamePlace.setText(hostel.getHostelName());
                            hostelAddressPlace.setText(hostel.getAddress());
                            hostelCityPlace.setText(hostel.getLocality());

                            btn_view_on_map.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommonFunctionsClass.setBtn_view_on_map(context, hostel);
                                }
                            });


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeBookingStatus(final String status) {
        MyFirebaseDatabase.BOOKINGS_REFERENCE
                .child(booking.getBookingHostelId())
                .child(booking.getUserId())
                .child(Booking.STRING_BOOKING_STATUS).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    SendPushNotificationFirebase.buildAndSendNotification(
                            context,
                            booking.getUserId(),
                            "Booking Requested " + CommonFunctionsClass.getBookingStatusString(status),
                            "Your booking request has been " + CommonFunctionsClass.getBookingStatusString(status)
                    );
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();

                }
            }
        });
    }

    private void getUserDetails(final String userId) {

        MyFirebaseDatabase.USER_REFERENCE.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {

                            if (!isProvider) {

                                if (booking.getUserId().equals(firebaseUser.getUid())) {
                                    if (booking.getBookingStatus().equals(Constants.BOOKING_STATUS_PENDING))
                                        layout_edit_remove_booking_req.setVisibility(View.VISIBLE);

                                    btnRemoveRequest.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            MyFirebaseDatabase.BOOKINGS_REFERENCE.child(booking.getBookingHostelId()).child(booking.getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Booking Have been removed!", Toast.LENGTH_LONG).show();
                                                        ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            } else {
                                Picasso.get().load(user.getImageUrl())
                                        .error(R.drawable.user_avatar)
                                        .placeholder(R.drawable.placeholder_photos)
                                        .centerInside().fit()
                                        .into(hostelUserImage);
                                phoneNumberPlace.setText(user.getPhone());
                                emailPlace.setText(user.getEmail());
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
