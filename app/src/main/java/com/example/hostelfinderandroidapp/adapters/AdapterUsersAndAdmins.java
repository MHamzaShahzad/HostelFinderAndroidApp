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

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsersAndAdmins extends RecyclerView.Adapter<AdapterUsersAndAdmins.Holder> {

    Context context;
    List<User> list;

    private static final String ACCOUNT_TYPE_USER_TEXT = "Make User";
    private static final String ACCOUNT_TYPE_ADMIN_TEXT = "Make Admin";

    public AdapterUsersAndAdmins(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_users_admins, null);
        return new AdapterUsersAndAdmins.Holder(layout);
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
        holder.email.setText(user.getEmail());
        holder.name.setText(user.getUserName());
        holder.phoneNumber.setText(user.getPhone());
        if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_USER)){
            holder.btnToggleAdminUser.setText(ACCOUNT_TYPE_ADMIN_TEXT);
        }
        if (user.getAccountType().equals(Constants.ACCOUNT_TYPE_ADMIN)){
            holder.btnToggleAdminUser.setText(ACCOUNT_TYPE_USER_TEXT);
        }

        holder.btnToggleAdminUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User clickedUser = list.get(holder.getAdapterPosition());

                if (clickedUser.getAccountType().equals(Constants.ACCOUNT_TYPE_USER)){
                    changeAccountType(clickedUser, Constants.ACCOUNT_TYPE_ADMIN);
                }
                if (clickedUser.getAccountType().equals(Constants.ACCOUNT_TYPE_ADMIN)){
                    changeAccountType(clickedUser, Constants.ACCOUNT_TYPE_USER);
                }

            }
        });

        if (user.getUserId().equals(MyFirebaseUser.mUser.getUid()))
            holder.btnToggleAdminUser.setVisibility(View.GONE);
        else
            holder.btnToggleAdminUser.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name, email, phoneNumber;
        Button btnToggleAdminUser;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);

            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);

            btnToggleAdminUser = itemView.findViewById(R.id.btnToggleAdminUser);

        }
    }

    private void changeAccountType(User user, String type){

        MyFirebaseDatabase.USER_REFERENCE.child(user.getUserId()).child("accountType").setValue(type);

    }

}
