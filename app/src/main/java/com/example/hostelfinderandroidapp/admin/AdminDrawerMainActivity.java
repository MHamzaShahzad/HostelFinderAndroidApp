package com.example.hostelfinderandroidapp.admin;

import android.content.Context;
import android.os.Bundle;

import com.example.hostelfinderandroidapp.CommonFunctionsClass;
import com.example.hostelfinderandroidapp.FragmentUpdateProfile;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.model.User;
import com.example.hostelfinderandroidapp.user.FragmentHostelsListForUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminDrawerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    public static ImageView userNavHeaderImage;
    public static TextView userNavHeaderName, headerPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_drawer_main);
        context = this;
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

        } else if (id == R.id.nav_logout) {

            MyFirebaseUser.SignOut(context);

        } else if (id == R.id.nav_share) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentUsersAndAdmins()).addToBackStack(null).commit();

        } else if (id == R.id.nav_send) {

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

}
