package com.example.hostelfinderandroidapp.common;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private static final String TAG = FragmentMap.class.getName();

    private Context context;
    private View view;

    private TextView text_location_address;
    private ImageView btn_submit_location;
    RelativeLayout layout_address_hostel;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Marker mCurrentLocationMarker;
    private MarkerOptions mCurrentLocationMarkerOptions;

    private static final int REQUEST_CODE = 101;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private String cityName;
    private Bundle incomingArguments;

    private FragmentInteractionListenerInterface mListener;

    public FragmentMap() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null) {
            mListener.onFragmentInteraction("Hostel Location");
        }

        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_map, container, false);
            layout_address_hostel = view.findViewById(R.id.layout_address_hostel);
            text_location_address = view.findViewById(R.id.text_location_address);
            btn_submit_location = view.findViewById(R.id.btn_submit_location);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            incomingArguments = getArguments();

            initMapFragment();

        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (incomingArguments != null && incomingArguments.getBoolean(Constants.HOSTEL_LOCATION_MAP)) {
            LatLng latLng = new LatLng(incomingArguments.getDouble(Constants.LOCATION_LATITUDE), incomingArguments.getDouble(Constants.LOCATION_LONGITUDE));

            mMap.addMarker(
                    new MarkerOptions()
                            .position(
                                    latLng
                            ).title(
                            getCompleteAddressString(context, incomingArguments.getDouble(Constants.LOCATION_LATITUDE), incomingArguments.getDouble(Constants.LOCATION_LONGITUDE))
                    )
            ).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            layout_address_hostel.setVisibility(View.GONE);

        }else {
            fetchLastLocation();
            setLocationCallback();
            setLocationRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
        }
    }

    private void initMapFragment() {
        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void fetchLastLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(context, currentLocation.getLatitude()
                            + "" + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();


                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    mCurrentLocationMarkerOptions = new MarkerOptions().position(latLng).title("Naveed is Here");
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mCurrentLocationMarker = mMap.addMarker(mCurrentLocationMarkerOptions);

                    startLocationUpdates();
                    setBtn_submit_location();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void setLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.e(TAG, "onLocationResult: " + location.toString());

                    currentLocation = location;

                    text_location_address.setText(getCompleteAddressString(context, currentLocation.getLatitude(), currentLocation.getLongitude()));

                    if (mCurrentLocationMarker != null)
                        mCurrentLocationMarker.remove();

                    mCurrentLocationMarkerOptions = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Naveed is Here");
                    mCurrentLocationMarker = mMap.addMarker(mCurrentLocationMarkerOptions);

                }
            }
        };
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (locationCallback != null)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                cityName = returnedAddress.getLocality();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("@LocationAddress", "My Current loction address" + strReturnedAddress.toString());
            } else {
                Log.e("@AddressNotFound", "My Current loction address No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@ErrinInAAddress", "My Current loction address Canont get Address!");
        }
        return strAdd;
    }

    private void setBtn_submit_location() {
        btn_submit_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Constants.LOCATION_RECEIVING_FILTER);
                intent.putExtra(Constants.LOCATION_ADDRESS_CITY, cityName);
                intent.putExtra(Constants.LOCATION_ADDRESS, text_location_address.getText().toString().trim());
                intent.putExtra(Constants.LOCATION_LATITUDE, String.valueOf(currentLocation.getLatitude()));
                intent.putExtra(Constants.LOCATION_LONGITUDE, String.valueOf(currentLocation.getLongitude()));
                context.sendBroadcast(intent);
                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onFragmentInteraction("Hostel Location");
        }
    }

}
