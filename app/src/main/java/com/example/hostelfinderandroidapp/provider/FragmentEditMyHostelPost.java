package com.example.hostelfinderandroidapp.provider;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseStorage;
import com.example.hostelfinderandroidapp.controlers.MyPrefLocalStorage;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class FragmentEditMyHostelPost extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentEditMyHostelPost.class.getName();
    private static final int GALLERY_REQUEST = 1;
    private Context context;
    private View view;
    ImageView hostelImage;
    private EditText ownerName, ownerPhoneNumber, ownerEmailAddress, ownerHostelName, numberOfRoomsAvailable, totalNumberOfRooms, maximumMembersPerRoom, hostelAddress, hostelDescription, costPerMember;
    private RadioGroup radioGroupHostelFor;
    private RadioButton radioButtonHostelForBoys, radioButtonHostelForGirls;
    private CheckBox checkBoxIsInternetAvailable, checkBoxIsParkingAvailable, checkBoxIsElectricityBackupAvailable;
    private Button updateProviderHostelPost;

    private Hostel argumentHostel;

    ProgressDialog progressDialog;

    Uri filePath;

    User databaseUser;
    private FragmentInteractionListenerInterface mListener;


    public FragmentEditMyHostelPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        if (mListener != null) {
            mListener.onFragmentInteraction("Edit Hostel");
        }
        databaseUser = MyPrefLocalStorage.getCurrentUserData(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_edit_my_hostel_post, container, false);
            initLayoutWidgets(view);
            setClickListeners();
            getHostelFromArgument();
        }
        return view;
    }

    private void initLayoutWidgets(View view) {
        hostelImage = (ImageView) view.findViewById(R.id.hostelImage);

        ownerName = (EditText) view.findViewById(R.id.ownerName);
        ownerPhoneNumber = (EditText) view.findViewById(R.id.ownerPhoneNumber);
        ownerEmailAddress = (EditText) view.findViewById(R.id.ownerEmailAddress);
        ownerHostelName = (EditText) view.findViewById(R.id.ownerHostelName);
        numberOfRoomsAvailable = (EditText) view.findViewById(R.id.numberOfRoomsAvailable);
        maximumMembersPerRoom = (EditText) view.findViewById(R.id.maximumMembersPerRoom);
        totalNumberOfRooms = (EditText) view.findViewById(R.id.totalNumberOfRooms);
        costPerMember = (EditText) view.findViewById(R.id.costPerMember);
        hostelAddress = (EditText) view.findViewById(R.id.hostelAddress);
        hostelDescription = (EditText) view.findViewById(R.id.hostelDescription);

        radioGroupHostelFor = (RadioGroup) view.findViewById(R.id.radioGroupHostelFor);
        radioButtonHostelForBoys = (RadioButton) view.findViewById(R.id.radioButtonHostelForBoys);
        radioButtonHostelForGirls = (RadioButton) view.findViewById(R.id.radioButtonHostelForGirls);
        updateProviderHostelPost = (Button) view.findViewById(R.id.updateProviderHostelPost);
        checkBoxIsElectricityBackupAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsElectricityBackupAvailable);
        checkBoxIsInternetAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsInternetAvailable);
        checkBoxIsParkingAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsParkingAvailable);

    }

    private void setClickListeners(){
        hostelImage.setOnClickListener(this);
        updateProviderHostelPost.setOnClickListener(this);
    }

    private Boolean validatePostForm() {
        if (ownerName.length() == 0) {
            ownerName.setError("Field is required!");
            return false;
        }
        if (ownerHostelName.length() == 0) {
            ownerHostelName.setError("Field is required!");
            return false;
        }
        if (ownerPhoneNumber.length() == 0) {
            ownerPhoneNumber.setError("Field is required!");
            return false;
        }
        if (ownerEmailAddress.length() > 0 && !isEmailValid(ownerEmailAddress.getText().toString())) {
            ownerEmailAddress.setError("Invalid Email");
            return false;
        }
        if (numberOfRoomsAvailable.length() == 0) {
            numberOfRoomsAvailable.setError("Field is required!");
            return false;
        }
        if (totalNumberOfRooms.length() == 0) {
            totalNumberOfRooms.setError("Field is required!");
            return false;
        }
        if (maximumMembersPerRoom.length() == 0) {
            maximumMembersPerRoom.setError("Field is required!");
            return false;
        }
        if (hostelAddress.length() == 0) {
            hostelAddress.setError("Field is required!");
            return false;

        }
        if (hostelDescription.length() == 0) {
            hostelDescription.setError("Field is required!");
            return false;

        }

        if (!isHostelForSelected()) {
            Toast.makeText(context, "Pleas select hostel for Boys or Girls", Toast.LENGTH_LONG).show();
        }


        return true;


    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private boolean isHostelForSelected() {
        return radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForGirls.getId() || radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForBoys.getId();
    }

    private String hostelSelectedFor() {
        if (radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForGirls.getId())
            return Constants.HOSTEL_FOR_GIRLS;
        if (radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForBoys.getId())
            return Constants.HOSTEL_FOR_BOYS;
        return "2";
    }

    private String getInternetAvailabilityStatus() {
        if (checkBoxIsInternetAvailable.isChecked())
            return Constants.HOSTEL_INTERNET_AVAILABLE;
        else
            return Constants.HOSTEL_INTERNET_NOT_AVAILABLE;
    }

    private String getElectricityBackupAvailabilityStatus() {
        if (checkBoxIsElectricityBackupAvailable.isChecked())
            return Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE;
        else
            return Constants.HOSTEL_ELECTRICITY_BACKUP_NOT_AVAILABLE;
    }

    private String getParkingAvailabilityStatus() {
        if (checkBoxIsParkingAvailable.isChecked())
            return Constants.HOSTEL_PARKING_AVAILABLE;
        else
            return Constants.HOSTEL_PARKING_NOT_AVAILABLE;
    }


    private void getHostelFromArgument(){
        if (getArguments() != null && getArguments().getSerializable(Constants.EDIT_MY_HOSTEL_BUNDLE_NAME) != null ){
            try{
               argumentHostel = (Hostel) getArguments().getSerializable(Constants.EDIT_MY_HOSTEL_BUNDLE_NAME);
               if (argumentHostel != null && argumentHostel.getHostelId() != null){

                   setDefaultFormData(argumentHostel);

               }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setDefaultFormData(Hostel hostel) {

        if (hostel.getImageUrl() != null){
            try{
                Picasso.get().load(hostel.getImageUrl()).placeholder(R.drawable.placeholder_photos).fit().into(hostelImage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //set data to edit text
        ownerName.setText(databaseUser.getUserName());
        ownerHostelName.setText(hostel.getHostelName());
        numberOfRoomsAvailable.setText(hostel.getAvailableRooms());
        totalNumberOfRooms.setText(hostel.getTotalRooms());
        maximumMembersPerRoom.setText(hostel.getMaxMembers());
        costPerMember.setText(hostel.getCostPerPerson());
        hostelAddress.setText(hostel.getAddress());
        hostelDescription.setText(hostel.getDescription());

        ownerEmailAddress.setText(hostel.getEmail());
        ownerPhoneNumber.setText(hostel.getPhone());

        if ( databaseUser.getEmail() != null )
            ownerEmailAddress.setEnabled(false);

        if (databaseUser.getPhone() != null)
            ownerPhoneNumber.setEnabled(false);

        //if (hostel.getPhone().equals())

        //set data to radio buttons
        switch (hostel.getType()){
            case Constants.HOSTEL_FOR_BOYS:
                radioButtonHostelForBoys.setChecked(true);
                break;
            case Constants.HOSTEL_FOR_GIRLS:
                radioButtonHostelForGirls.setChecked(true);
                break;
        }

        //set check boxes
        if (hostel.getInternetAvailable().equals(Constants.HOSTEL_INTERNET_AVAILABLE))
            checkBoxIsInternetAvailable.setChecked(true);
        if (hostel.getParking().equals(Constants.HOSTEL_PARKING_AVAILABLE))
            checkBoxIsParkingAvailable.setChecked(true);
        if (hostel.getElectricityBackup().equals(Constants.HOSTEL_ELECTRICITY_BACKUP_AVAILABLE))
            checkBoxIsElectricityBackupAvailable.setChecked(true);

    }

    private void loadImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                hostelImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == hostelImage.getId()){

            loadImageFromGallery();

        }else if (view.getId() == updateProviderHostelPost.getId()){

            uploadImageAndEditPost();

        }else {
            Log.e(TAG, "onClick: NO_LISTENER_SET" );
        }
    }

    private void uploadImageAndEditPost() {

        if (filePath != null) {

            progressDialog.show();

            final String imageId = UUID.randomUUID().toString();

            MyFirebaseStorage.HOSTELS_IMAGES_STORAGE_REFERENCE.child(imageId).putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    updateHostelData(uri.toString());

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Can't load image url " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Image Uploading Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }else {
            updateHostelData(argumentHostel.getImageUrl());
        }
    }

    private void updateHostelData(String hostelImageUrl) {

        Hostel editedHostel = getEditedHostelInstance(argumentHostel ,hostelImageUrl);

        MyFirebaseDatabase.HOSTELS_REFERENCE.child(editedHostel.getHostelId()).setValue(editedHostel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, "Hostel Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Hostel Uploading Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Hostel getEditedHostelInstance(Hostel argumentHostel, String hostelImageUrl) {
        Date date = new Date();

        return new Hostel(
                argumentHostel.getHostelId(),
                ownerHostelName.getText().toString(),
                numberOfRoomsAvailable.getText().toString(),
                maximumMembersPerRoom.getText().toString(),
                totalNumberOfRooms.getText().toString(),
                costPerMember.getText().toString(),
                getInternetAvailabilityStatus(),
                getElectricityBackupAvailabilityStatus(),
                getParkingAvailabilityStatus(),
                hostelDescription.getText().toString(),
                ownerPhoneNumber.getText().toString(),
                ownerEmailAddress.getText().toString(),
                argumentHostel.getOwnerId(),
                hostelSelectedFor(),
                hostelImageUrl,
                "",
                "",
                hostelAddress.getText().toString(),
                "",
                argumentHostel.getStatus(),
                date.toLocaleString()
        );
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
            mListener.onFragmentInteraction("Edit Hostel");
        }
    }
}
