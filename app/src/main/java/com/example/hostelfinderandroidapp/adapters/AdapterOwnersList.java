package com.example.hostelfinderandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.SendPushNotificationFirebase;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterOwnersList extends RecyclerView.Adapter<AdapterOwnersList.Holder> {

    Context context;
    List<User> list;

    private static final String USERS_LIST_BUTTON_TEXT_ACTIVE = "Active";
    private static final String USERS_LIST_BUTTON_TEXT_INACTIVE = "In-Active";


    public AdapterOwnersList(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_owners_list, null);
        return new AdapterOwnersList.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final User user = list.get(position);
        if (user.getImageUrl() != null)
            try {
                Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.user_avatar).fit().into(holder.profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        holder.ownerEmail.setText(user.getEmail());
        holder.ownerName.setText(user.getUserName());
        holder.ownerPhoneNumber.setText(user.getPhone());
        if (user.getAccountStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE)) {
            holder.btnActiveInActiveOwner.setText(USERS_LIST_BUTTON_TEXT_INACTIVE);
        }
        if (user.getAccountStatus().equals(Constants.ACCOUNT_STATUS_INACTIVE)) {
            holder.btnActiveInActiveOwner.setText(USERS_LIST_BUTTON_TEXT_ACTIVE);
        }

        holder.btnActiveInActiveOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User clickedUser = list.get(holder.getAdapterPosition());

                if (clickedUser.getAccountStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE)) {
                    changeOwnerAccountStatus(clickedUser, Constants.ACCOUNT_STATUS_INACTIVE);
                    changeOwnerHostelsStatus(user);
                }
                if (clickedUser.getAccountStatus().equals(Constants.ACCOUNT_STATUS_INACTIVE)) {
                    changeOwnerAccountStatus(clickedUser, Constants.ACCOUNT_STATUS_ACTIVE);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView ownerName, ownerEmail, ownerPhoneNumber;
        Button btnActiveInActiveOwner;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);

            ownerName = itemView.findViewById(R.id.ownerName);
            ownerEmail = itemView.findViewById(R.id.ownerEmail);
            ownerPhoneNumber = itemView.findViewById(R.id.ownerPhoneNumber);

            btnActiveInActiveOwner = itemView.findViewById(R.id.btnActiveInActiveOwner);


        }
    }

    private void changeOwnerAccountStatus(final User user, String status) {

        MyFirebaseDatabase.USER_REFERENCE.child(user.getUserId()).child("accountStatus").setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SendPushNotificationFirebase.buildAndSendNotification(context,
                        user.getUserId(),
                        "Account Status",
                        "Your account status has been changed by admin!"
                );
            }
        });

    }

    private void changeOwnerHostelsStatus(final User user) {

        MyFirebaseDatabase.HOSTELS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    Iterable<DataSnapshot> hostelListSnapshot = dataSnapshot.getChildren();
                    for (DataSnapshot hostelSnapshot : hostelListSnapshot) {

                        final Hostel hostel = hostelSnapshot.getValue(Hostel.class);
                        if (hostel != null && hostel.getOwnerId().equals(user.getUserId()) && hostel.getStatus().equals(Constants.HOSTEL_STATUS_ACTIVE))
                            MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostel.getHostelId()).child("status").setValue(Constants.HOSTEL_STATUS_INACTIVE).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SendPushNotificationFirebase.buildAndSendNotification(context,
                                            user.getUserId(),
                                            hostel.getHostelName() + " Inactivated",
                                            hostel.getHostelName() + " has been inactivated by admin!"
                                    );
                                }
                            });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
