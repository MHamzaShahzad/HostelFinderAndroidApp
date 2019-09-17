package com.example.hostelfinderandroidapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseStorage;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.controlers.MyPrefLocalStorage;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBecomeHostelOwner extends Fragment {

    private static final String TAG = FragmentBecomeHostelOwner.class.getName();

    private static final int GALLERY_REQUEST = 1;
    private Context context;
    private View view;
    ImageView hostelImage;
    private TextView hostelAddress;
    private EditText ownerName, ownerPhoneNumber, ownerEmailAddress, ownerHostelName, numberOfRoomsAvailable, totalNumberOfRooms, maximumMembersPerRoom, hostelDescription, costPerMember;
    private RadioGroup radioGroupHostelFor;
    private RadioButton radioButtonHostelForBoys, radioButtonHostelForGirls;
    private CheckBox checkBoxIsInternetAvailable, checkBoxIsParkingAvailable, checkBoxIsElectricityBackupAvailable;
    private Button submitProviderHostelPost;

    private static BroadcastReceiver broadcastReceiver;
    String latitude, longitude, city;

    Uri filePath;

    ProgressDialog progressDialog;

    FirebaseUser firebaseUser;
    User databaseUser;

    User hostelOwner;
    Hostel hostel;

    public FragmentBecomeHostelOwner() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();

        firebaseUser = MyFirebaseUser.mUser;
        databaseUser = MyPrefLocalStorage.getCurrentUserData(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_become_hostel_owner, container, false);

            initLayoutWidgets(view);


            submitProviderHostelPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validatePostForm()) {

                        uploadImage();

                    }
                }
            });

            hostelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadImageFromGallery();
                }
            });

            getUserDefaultData();
            setHostelAddressListener();
            setLocationsReceiver();
        }
        return view;

    }

    private void setHostelAddressListener() {
        hostelAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new FragmentMap()).addToBackStack(null).commit();
            }
        });
    }

    private void getUserDefaultData() {
        if (databaseUser.getPhone() != null) {
            ownerPhoneNumber.setText(databaseUser.getPhone());
            ownerPhoneNumber.setEnabled(false);
        }
        if (databaseUser.getEmail() != null) {
            ownerEmailAddress.setText(databaseUser.getEmail());
            ownerEmailAddress.setEnabled(false);
        }

        if (databaseUser.getUserName() != null) {
            ownerName.setText(databaseUser.getUserName());
            ownerName.setEnabled(false);
        }

    }

    private void uploadImage() {

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

                                    uploadUserAndHostel(uri.toString());

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
        }
    }

    private void uploadUserAndHostel(String hostelImageUrl) {

        setHostelInstance(hostelImageUrl);
        setUserInstance();

        MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostel.getHostelId()).setValue(hostel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, "Hostel Uploaded", Toast.LENGTH_SHORT).show();


                MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).setValue(hostelOwner).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "User Uploaded", Toast.LENGTH_SHORT).show();
                        ((FragmentActivity) context).getSupportFragmentManager().popBackStack();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Creating Hostel Owner Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, "Hostel Uploading Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setHostelInstance(String hostelImageUrl) {
        Date date = new Date();
        String hostelId = UUID.randomUUID().toString();

        hostel = new Hostel(
                hostelId,
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
                firebaseUser.getUid(),
                hostelSelectedFor(),
                hostelImageUrl,
                latitude,
                longitude,
                hostelAddress.getText().toString(),
                city,
                Constants.HOSTEL_STATUS_INACTIVE,
                date.toLocaleString()
        );

    }

    private void setUserInstance() {
        hostelOwner = new User(
                firebaseUser.getUid(),
                ownerName.getText().toString(),
                ownerPhoneNumber.getText().toString(),
                ownerEmailAddress.getText().toString(),
                (databaseUser.getImageUrl() != null && !databaseUser.getImageUrl().equals("null")) ? databaseUser.getImageUrl() : String.valueOf(firebaseUser.getPhotoUrl()),
                (databaseUser.getAccountStatus() == null) ? Constants.ACCOUNT_STATUS_INACTIVE : databaseUser.getAccountStatus(),
                (databaseUser.getAccountType() == null) ? Constants.ACCOUNT_TYPE_HOSTEL_OWNER : databaseUser.getAccountType()
        );
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
        hostelAddress = (TextView) view.findViewById(R.id.hostelAddress);
        hostelDescription = (EditText) view.findViewById(R.id.hostelDescription);

        radioGroupHostelFor = (RadioGroup) view.findViewById(R.id.radioGroupHostelFor);
        radioButtonHostelForBoys = (RadioButton) view.findViewById(R.id.radioButtonHostelForBoys);
        radioButtonHostelForGirls = (RadioButton) view.findViewById(R.id.radioButtonHostelForGirls);
        submitProviderHostelPost = (Button) view.findViewById(R.id.submitProviderHostelPost);
        checkBoxIsElectricityBackupAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsElectricityBackupAvailable);
        checkBoxIsInternetAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsInternetAvailable);
        checkBoxIsParkingAvailable = (CheckBox) view.findViewById(R.id.checkBoxIsParkingAvailable);

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

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

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

    private void setLocationsReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hostelAddress.setText(intent.getStringExtra(Constants.LOCATION_ADDRESS));
                city = intent.getStringExtra(Constants.LOCATION_ADDRESS_CITY);
                latitude = intent.getStringExtra(Constants.LOCATION_LATITUDE);
                longitude = intent.getStringExtra(Constants.LOCATION_LONGITUDE);


                Log.e(TAG, "onReceive: DEPT_DATA \n" + intent.getStringExtra(Constants.LOCATION_ADDRESS)
                        + "\n" + intent.getStringExtra(Constants.LOCATION_ADDRESS_CITY)
                        + "\n" + intent.getStringExtra(Constants.LOCATION_LATITUDE)
                        + "\n" + intent.getStringExtra(Constants.LOCATION_LONGITUDE));

            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(Constants.LOCATION_RECEIVING_FILTER));
    }


}
