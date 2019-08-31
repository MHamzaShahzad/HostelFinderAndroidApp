package com.example.hostelfinderandroidapp;


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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private static final int GALLERY_REQUEST = 1 ;
    private Context context;
    private View view;
    ImageView hostelImage;
    private EditText ownerName, ownerPhoneNumber, ownerEmailAddress, ownerHostelName, numberOfRoomsAvailable, totalNumberOfRooms, maximumMembersPerRoom, hostelAddress, hostelDescription, costPerMember;
    private RadioGroup radioGroupHostelFor;
    private RadioButton radioButtonHostelForBoys, radioButtonHostelForGirls;
    private CheckBox checkBoxIsInternetAvailable, checkBoxIsParkingAvailable, checkBoxIsElectricityBackupAvailable;
    private Button submitProviderHostelPost;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri filePath;

    public FragmentBecomeHostelOwner() {
        // Required empty public constructor

    }

    public FragmentBecomeHostelOwner(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_become_hostel_owner, container, false);

            initLayoutWidgets(view);


            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("images/");

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

        }
        return view;

    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final String hostelId =  UUID.randomUUID().toString();

            //StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            storageReference.child(hostelId).putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();

                           Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                           task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {

                                       uploadUserAndHostel(uri.toString(), hostelId);

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(context, "Can't load image url "+e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Image Uploading Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void uploadUserAndHostel(String hostelImageUrl, String hostelId){


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Date date = new Date();

        Hostel hostel = new Hostel(
                hostelId,
                ownerHostelName.getText().toString(),
                numberOfRoomsAvailable.getText().toString(),
                maximumMembersPerRoom.getText().toString(),
                totalNumberOfRooms.getText().toString(),
                costPerMember.getText().toString(),
                "1",
                "1",
                "1",
                hostelDescription.getText().toString(),
                ownerPhoneNumber.getText().toString(),
                ownerEmailAddress.getText().toString(),
                user.getUid(),
                String.valueOf(hostelSelectedFor()),
                hostelImageUrl,
                "",
                "",
                hostelAddress.getText().toString(),
                Constants.HOSTEL_STATUS_INACTIVE,
                date.toLocaleString()
        );

        final User hostelOwner = new User(
                user.getUid(),
                ownerName.getText().toString(),
                ownerPhoneNumber.getText().toString(),
                ownerEmailAddress.getText().toString(),
                String.valueOf(user.getPhotoUrl()),
                Constants.ACCOUNT_STATUS_INACTIVE,
                Constants.ACCOUNT_TYPE_HOSTEL_OWNER,
                hostelId
        );


        MyFirebaseDatabase.HOSTELS_REFERENCE.child(hostelId).setValue(hostel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, "Hostel Uploaded", Toast.LENGTH_SHORT).show();


                MyFirebaseDatabase.USER_REFERENCE.child(user.getUid()).setValue(hostelOwner).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "User Uploaded", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Creating Hostel Owner Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Hostel Uploading Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
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

        if (!isHostelForSelected()){
            Toast.makeText(context, "Pleas select hostel for Boys or Girls", Toast.LENGTH_LONG).show();
        }


        return true;


    }

    private boolean isHostelForSelected(){
        return radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForGirls.getId() || radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForBoys.getId();
    }
    private int hostelSelectedFor(){
        if (radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForGirls.getId())
            return 0;
        if (radioGroupHostelFor.getCheckedRadioButtonId() == radioButtonHostelForBoys.getId())
            return 1;
        return  2 ;
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                hostelImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
