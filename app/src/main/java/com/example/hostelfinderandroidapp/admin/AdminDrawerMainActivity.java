package com.example.hostelfinderandroidapp.admin;

import android.content.Context;
import android.os.Bundle;

import com.example.hostelfinderandroidapp.communicate.AboutUsFragment;
import com.example.hostelfinderandroidapp.communicate.ContactUsFragment;
import com.example.hostelfinderandroidapp.common.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.common.FragmentUpdateProfile;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.model.User;
import com.example.hostelfinderandroidapp.user.FragmentBookingsUser;
import com.example.hostelfinderandroidapp.user.FragmentHostelsListForUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminDrawerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListenerInterface {

    private static final String TAG = AdminDrawerMainActivity.class.getName();
    private Context context;

    public static ImageView userNavHeaderImage;
    public static TextView userNavHeaderName, headerPhoneNumber;

    private FirebaseUser firebaseUser;
    private ValueEventListener userValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_drawer_main);
        context = this;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            MyFirebaseUser.SignOut(context);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initHeaderWidgets(navigationView.getHeaderView(0));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentHostelsListForUser()).commit();
        initCheckUserAccount();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.admin_drawer_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        clearFragmentBackStack();

        if (id == R.id.nav_home) {
            // Handle the camera action

        } else if (id == R.id.nav_hostels) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentHostelsListAdmin()).addToBackStack(null).commit();

        } else if (id == R.id.nav_owners) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentOwnersListAdmin()).addToBackStack(null).commit();

        } else if (id == R.id.nav_users_admins_list) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentUsersAndAdmins()).addToBackStack(null).commit();

        } else if (id == R.id.nav_my_bookings) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentBookingsUser()).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {

            MyFirebaseUser.SignOut(context);

        }  else if (id == R.id.nav_contact_us) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new ContactUsFragment()).addToBackStack(null).commit();


        } else if (id == R.id.nav_about_us) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new AboutUsFragment()).addToBackStack(null).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clearFragmentBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }


    private void initHeaderWidgets(View view) {
        if (view != null) {
            userNavHeaderImage = view.findViewById(R.id.headerImageView);
            userNavHeaderName = view.findViewById(R.id.headerUserName);
            headerPhoneNumber = view.findViewById(R.id.headerPhoneNumber);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentUpdateProfile.newInstance().show(getSupportFragmentManager(), "Update Profile");
                }
            });
        }
    }

    public static void setNavigationHeader(User user) {
        if (user != null) {
            if (user.getImageUrl() != null)
                try {
                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.user_avatar).fit().into(userNavHeaderImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            userNavHeaderName.setText(user.getUserName());
            headerPhoneNumber.setText((user.getPhone() == null) ? user.getEmail() : user.getPhone());
        }
    }

    private void initCheckUserAccount() {
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getValue());
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {

                            //If account de-activated
                            if (user.getAccountStatus().equals(Constants.ACCOUNT_STATUS_INACTIVE)) {
                                Log.e(TAG, "onDataChange: STATUS_INACTIVE");
                                CommonFunctionsClass.showDialogAndSignOut(context, "Your account has been de-activated by admin.");
                            }

                            //If account type changes
                            if (user.getAccountStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE) && (user.getAccountType().equals(Constants.ACCOUNT_TYPE_USER) || user.getAccountType().equals(Constants.ACCOUNT_TYPE_HOSTEL_OWNER))) {
                                CommonFunctionsClass.showDialogAndSignOut(context, "Your account type have been changed, click continue to re-authenticate!");
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
        };
        MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).addValueEventListener(userValueEventListener);
    }

    private void removeUserValueEventListener() {
        MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).removeEventListener(userValueEventListener);
    }

    @Override
    public void onFragmentInteraction(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeUserValueEventListener();
    }

}
