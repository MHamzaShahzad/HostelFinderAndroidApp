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
import android.widget.Toast;

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.adapters.AdapterOwnersList;
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
public class FragmentOwnersListAdmin extends Fragment {


    private static final String TAG = FragmentOwnersListAdmin.class.getName();
    Context context;
    View view;
    RecyclerView recycler_owners_list;
     AdapterOwnersList adapterOwnersList;
    TabLayout tabLayout;
    List<User> list, tempList;
    ValueEventListener valueEventListener;

    public FragmentOwnersListAdmin() {
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
            view = inflater.inflate(R.layout.fragment_owners_list_admin, container, false);


            recycler_owners_list = view.findViewById(R.id.recycler_owners_list);
            recycler_owners_list.setHasFixedSize(true);
            recycler_owners_list.setLayoutManager(new LinearLayoutManager(context));
            adapterOwnersList = new AdapterOwnersList(context, tempList);
            recycler_owners_list.setAdapter(adapterOwnersList);

            initOwnersListListener();

            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            Toast.makeText(context, "In-Active", Toast.LENGTH_LONG).show();
                            getInActiveOwners();
                            break;
                        case 1:
                            Toast.makeText(context, "Active", Toast.LENGTH_LONG).show();
                            getActiveOwners();
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

    private void initOwnersListListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> usersListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleUser : usersListSnapshot) {

                    try {
                        User user = singleUser.getValue(User.class);
                        if (user != null && user.getAccountType().equals(Constants.ACCOUNT_TYPE_HOSTEL_OWNER)) {
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
           getInActiveOwners();
       }
        if (tabLayout.getSelectedTabPosition() == 1){
            getActiveOwners();
        }
    }

    private void getInActiveOwners() {

        tempList.clear();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAccountStatus() != null && list.get(i).getAccountStatus().equals(Constants.ACCOUNT_STATUS_INACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterOwnersList.notifyDataSetChanged();

    }

    private void getActiveOwners() {

        tempList.clear();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAccountStatus() != null && list.get(i).getAccountStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterOwnersList.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            MyFirebaseDatabase.USER_REFERENCE.removeEventListener(valueEventListener);
    }
}
