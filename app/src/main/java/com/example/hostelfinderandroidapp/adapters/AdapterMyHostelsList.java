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

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.FragmentHostelDescription;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.provider.FragmentMyHostelDescription;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMyHostelsList extends RecyclerView.Adapter<AdapterMyHostelsList.Holder> {


    Context context;
    List<Hostel> list;
    private static Bundle bundle;

    public AdapterMyHostelsList(Context context, List<Hostel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_hostel, null);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        Hostel hostel = list.get(position);
        if (hostel.getImageUrl() != null)
            try {
                Picasso.get().load(hostel.getImageUrl()).placeholder(R.drawable.placeholder_photos).fit().into(holder.hostelImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        holder.hostelAddressPlace.setText(hostel.getAddress());
        holder.hostelAvailableRoomsPlace.setText(hostel.getAvailableRooms());
        holder.hostelCostPerMemberPlace.setText(hostel.getCostPerPerson());
        holder.hostelNamePlace.setText(hostel.getHostelName());
        holder.hostelMaxMembersPerRoomPlace.setText(hostel.getMaxMembers());

        holder.cardRecyclerHostelsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentMyHostelDescription myHostelDescription = new FragmentMyHostelDescription();
                bundle = new Bundle();
                bundle.putSerializable(Constants.HOSTEL_DESCRIPTION_NAME, list.get(holder.getAdapterPosition()));
                myHostelDescription.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, myHostelDescription).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardRecyclerHostelsList;
        ImageView hostelImage;
        TextView hostelNamePlace, hostelAddressPlace, hostelAvailableRoomsPlace, hostelCostPerMemberPlace, hostelMaxMembersPerRoomPlace;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cardRecyclerHostelsList = itemView.findViewById(R.id.cardRecyclerHostelsList);
            hostelImage = itemView.findViewById(R.id.hostelImage);
            hostelNamePlace = itemView.findViewById(R.id.hostelNamePlace);
            hostelAddressPlace = itemView.findViewById(R.id.hostelAddressPlace);
            hostelAvailableRoomsPlace = itemView.findViewById(R.id.hostelAvailableRoomsPlace);
            hostelCostPerMemberPlace = itemView.findViewById(R.id.hostelCostPerMemberPlace);
            hostelMaxMembersPerRoomPlace = itemView.findViewById(R.id.hostelMaxMembersPerRoomPlace);

        }
    }
}
