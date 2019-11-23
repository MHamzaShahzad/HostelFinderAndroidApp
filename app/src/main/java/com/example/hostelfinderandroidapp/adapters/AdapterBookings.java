package com.example.hostelfinderandroidapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelfinderandroidapp.common.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.common.FragmentBookingDescription;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Booking;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterBookings extends RecyclerView.Adapter<AdapterBookings.Holder> {

    private static final String TAG = AdapterBookings.class.getName();
    private Context context;
    private List<Booking> bookingList;
    private boolean isProvider;

    public AdapterBookings(Context context, List<Booking> bookingList, boolean isProvider) {
        this.context = context;
        this.bookingList = bookingList;
        this.isProvider = isProvider;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bookings_card_design, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final Booking booking = bookingList.get(holder.getAdapterPosition());

        holder.textUserBelongsTo.setText(CommonFunctionsClass.getUserBelongsTo(booking.getUserBelongsTo()));
        holder.textUserName.setText(booking.getUserName());
        holder.textRequestedAt.setText(booking.getUploadedAt());

        MyFirebaseDatabase.HOSTELS_REFERENCE.child(booking.getBookingHostelId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        Hostel hostel = dataSnapshot.getValue(Hostel.class);
                        if (hostel != null) {

                            if (!isProvider)
                                Picasso.get().load(hostel.getImageUrl())
                                        .error(R.drawable.placeholder_photos)
                                        .placeholder(R.drawable.placeholder_photos)
                                        .centerInside().fit()
                                        .into(holder.imageUserOrOwner);

                            holder.textHostelName.setText(hostel.getHostelName());
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

        if (isProvider)
            MyFirebaseDatabase.USER_REFERENCE.child(booking.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                        try {

                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                Picasso.get().load(user.getImageUrl())
                                        .error(R.drawable.user_avatar)
                                        .placeholder(R.drawable.placeholder_photos)
                                        .centerInside().fit()
                                        .into(holder.imageUserOrOwner);
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

        holder.cardBookingRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentBookingDescription fragmentBookingDescription = new FragmentBookingDescription();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BOOKING_OBJECT, booking);
                bundle.putBoolean(Constants.STRING_IS_PROVIDER, isProvider);
                fragmentBookingDescription.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_home, fragmentBookingDescription).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private CardView cardBookingRecycler;
        private ImageView imageUserOrOwner;
        private TextView textHostelName, textUserName, textRequestedAt, textUserBelongsTo;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardBookingRecycler = itemView.findViewById(R.id.cardBookingRecycler);
            imageUserOrOwner = itemView.findViewById(R.id.imageUserOrOwner);
            textHostelName = itemView.findViewById(R.id.textHostelName);
            textUserName = itemView.findViewById(R.id.textUserName);
            textRequestedAt = itemView.findViewById(R.id.textRequestedAt);
            textUserBelongsTo = itemView.findViewById(R.id.textUserBelongsTo);

        }
    }
}
