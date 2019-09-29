package com.example.hostelfinderandroidapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseStorage;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class FragmentUpdateProfile extends DialogFragment implements View.OnClickListener {

    private static final String TAG = FragmentUpdateProfile.class.getName();
    private Context context;

    private static ProgressDialog progressDialog;
    private static final int GALLERY_REQUEST = 1;
    private Uri filePath;
    private FirebaseUser firebaseUser;
    private User oldDatabaseUser;

    private CircleImageView profileImage;
    private ImageButton btnUpdateImage;
    private Button btnUpdateProfile;
    private TextInputEditText userNameUpdate, userPhoneUpdate, userEmailUpdate;


    public FragmentUpdateProfile() {
        // Required empty public constructor
    }

    public static FragmentUpdateProfile newInstance() {
        Log.d(TAG, "newInstance: ");
        return new FragmentUpdateProfile();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        context = getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.fragment_update_profile, null);
        initDialogWidgets(view);
        builder.setView(view);

        setDefaultData();


        return builder.create();
    }

    private void initDialogWidgets(View view) {
        userNameUpdate = (TextInputEditText) view.findViewById(R.id.userNameUpdate);
        userPhoneUpdate = (TextInputEditText) view.findViewById(R.id.userPhoneUpdate);
        userEmailUpdate = (TextInputEditText) view.findViewById(R.id.userEmailUpdate);

        profileImage = (CircleImageView) view.findViewById(R.id.profileImageView);
        btnUpdateImage = (ImageButton) view.findViewById(R.id.btnUpdateImage);
        btnUpdateProfile = (Button) view.findViewById(R.id.btnUpdateProfile);

        btnUpdateImage.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);

        initProgressDialog();
    }

    private void setDefaultData() {
        if (firebaseUser != null)
            MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                oldDatabaseUser = user;
                                if (user.getImageUrl() != null) {
                                    Picasso.get().load(user.getImageUrl()).error(R.drawable.user_avatar).placeholder(R.drawable.user_avatar).fit().into(profileImage);
                                }
                                userNameUpdate.setText(user.getUserName());
                                userPhoneUpdate.setText(user.getPhone());
                                userEmailUpdate.setText(user.getEmail());

                                if (user.getPhone() != null)
                                    userPhoneUpdate.setEnabled(false);
                                if (user.getEmail() != null)
                                    userEmailUpdate.setEnabled(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    private User buildUserInstance(String imageUrl){

        return new User(
                firebaseUser.getUid(),
                userNameUpdate.getText().toString(),
                userPhoneUpdate.getText().toString(),
                userEmailUpdate.getText().toString(),
                (imageUrl != null) ? imageUrl : oldDatabaseUser.getImageUrl(),
                oldDatabaseUser.getAccountStatus(),
                oldDatabaseUser.getAccountType()

        );



    }

    private void uploadImage() {
        if (filePath != null) {

            progressDialog.show();

            MyFirebaseStorage.USERS_IMAGES_STORAGE_REFERENCE.child(filePath.getLastPathSegment() + ".jpg").putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();

                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUserInFirebaseDatabase(buildUserInstance(uri.toString()));
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
                            dismissProgressDialog();
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

    private void updateUserInFirebaseDatabase(User newUser){
        MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissProgressDialog();
                FragmentUpdateProfile.this.dismiss();
                Toast.makeText(context, "Your account have been updated successfully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();
                Toast.makeText(context, "Your account can not be updated! \n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dismissProgressDialog(){
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    private void initProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpdateImage:
                loadImageFromGallery();
                break;
            case R.id.btnUpdateProfile:
                /*String TAG = "xx_xx_provider_info";
                if (firebaseUser != null)
                    for (UserInfo userInfo : firebaseUser.getProviderData())
                        Log.d(TAG, "onClick: " + userInfo.getProviderId());*/

                if (filePath != null){
                    uploadImage();
                }else
                    updateUserInFirebaseDatabase(buildUserInstance(null));

                break;

        }
    }
}
