package com.example.hostelfinderandroidapp.admin;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.adapters.AdapterOwnersList;
import com.example.hostelfinderandroidapp.adapters.AdapterUsersAndAdmins;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUsersAndAdmins extends Fragment {

    private static final String TAG = FragmentUsersAndAdmins.class.getName();

    View view;
    Context context;

    RecyclerView recycler_admins_users_list;
    AdapterUsersAndAdmins adapterUsersAndAdmins;
    TabLayout tabLayout;
    List<User> list, tempList;
    ValueEventListener valueEventListener;


    public FragmentUsersAndAdmins() {
        // Required empty public constructor
        list = new ArrayList<>();
        tempList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_users_and_admins, container, false);

            recycler_admins_users_list = view.findViewById(R.id.recycler_admins_users_list);
            recycler_admins_users_list.setHasFixedSize(true);
            recycler_admins_users_list.setLayoutManager(new LinearLayoutManager(context));
            adapterUsersAndAdmins = new AdapterUsersAndAdmins(context, tempList);
            recycler_admins_users_list.setAdapter(adapterUsersAndAdmins);

            initAdminsUsersListListener();

            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            Toast.makeText(context, "Users", Toast.LENGTH_LONG).show();
                            getUsers();
                            break;
                        case 1:
                            Toast.makeText(context, "Admins", Toast.LENGTH_LONG).show();
                            getAdmins();
                            break;
                        default:
                            Toast.makeText(context, "unKnown", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
        return view;
    }

    private void initAdminsUsersListListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> usersListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleUser : usersListSnapshot) {

                    try {
                        User user = singleUser.getValue(User.class);
                        if (user != null && !user.getAccountType().equals(Constants.ACCOUNT_TYPE_HOSTEL_OWNER)) {
                            list.add(user);
                            Log.e(TAG, "onDataChange: "+ user.getUserName() );
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                initTabsLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabase.USER_REFERENCE.addValueEventListener(valueEventListener);
    }

    private void initTabsLayout(){
        if (tabLayout.getSelectedTabPosition() == 0){
            getUsers();
        }
        if (tabLayout.getSelectedTabPosition() == 1){
            getAdmins();
        }
    }

    private void getUsers() {

        tempList.clear();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAccountType() != null && list.get(i).getAccountType().equals(Constants.ACCOUNT_TYPE_USER))
                    tempList.add(list.get(i));
            }

        }
        adapterUsersAndAdmins.notifyDataSetChanged();

    }

    private void getAdmins() {

        tempList.clear();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAccountType() != null && list.get(i).getAccountType().equals(Constants.ACCOUNT_TYPE_ADMIN))
                    tempList.add(list.get(i));
            }

        }
        adapterUsersAndAdmins.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            MyFirebaseDatabase.USER_REFERENCE.removeEventListener(valueEventListener);
    }

}
