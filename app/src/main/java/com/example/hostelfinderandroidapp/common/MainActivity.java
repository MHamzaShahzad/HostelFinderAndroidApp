package com.example.hostelfinderandroidapp.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.admin.AdminDrawerMainActivity;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseUser;
import com.example.hostelfinderandroidapp.controlers.MyServicesControllerClass;
import com.example.hostelfinderandroidapp.model.User;
import com.example.hostelfinderandroidapp.provider.ProviderDrawerMainActivity;
import com.example.hostelfinderandroidapp.user.DrawerMainActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView animText;
    ImageView splashImage;
    Animation fromBottom, fromTop;
    private static final String TAG = MainActivity.class.getName();
    private Context context;
    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        animText = findViewById(R.id.txt_splash);
        splashImage = findViewById(R.id.splash_logo);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        animText.setAnimation(fromBottom);
        splashImage.setAnimation(fromTop);

        if (isAlreadySignedIn())
            checkUserTypeFromDBToSignIn();
        else {
            initProviders();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSignInOptions();
                }
            }, 3000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "onActivityResult: VALID_REQUEST_CODE");
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.e(TAG, "onActivityResult: VALID_RESULT_CODE");
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    MyFirebaseDatabase.USER_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {

                                final User newUser = new User(
                                        user.getUid(),
                                        user.getDisplayName(),
                                        user.getPhoneNumber(),
                                        user.getEmail(),
                                        String.valueOf(user.getPhotoUrl()),
                                        Constants.ACCOUNT_STATUS_ACTIVE,
                                        Constants.ACCOUNT_TYPE_USER

                                );
                                MyFirebaseDatabase.USER_REFERENCE.child(user.getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Your account have been created successfully!", Toast.LENGTH_LONG).show();
                                        goToHomeAccordingToUserType(newUser);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Your account can not be created!" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                //Toast.makeText(context, "Successfully logged In!", Toast.LENGTH_LONG).show();
                                try {
                                    User myUser = dataSnapshot.getValue(User.class);
                                    if (myUser != null)

                                        goToHomeAccordingToUserType(myUser);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, "The process could't be completed!", Toast.LENGTH_LONG).show();
                        }
                    });
                    MyFirebaseUser.initAuthUser();
                }
            } else {
                if (response != null && response.getError() != null) {
                    Log.e(TAG, "onActivityResult: Error" + response.getError().getErrorCode());
                    Toast.makeText(MainActivity.this, "Error : " + response.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initProviders() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );
    }

    private void showSignInOptions() {

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.MyTheme)
                        .build(),
                RC_SIGN_IN);

    }

    private boolean isAlreadySignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private void startUserHomeActivity() {
        startActivity(new Intent(MainActivity.this, DrawerMainActivity.class));
        finish();
    }

    private void startAdminHomeActivity() {
        startActivity(new Intent(MainActivity.this, AdminDrawerMainActivity.class));
        finish();
    }

    private void startProviderHomeActivity() {
        startActivity(new Intent(MainActivity.this, ProviderDrawerMainActivity.class));
        finish();
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void goToHomeAccordingToUserType(User user) {
        if (user.getAccountStatus() != null && user.getAccountStatus().equals(Constants.ACCOUNT_STATUS_ACTIVE)) {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e(TAG, "onComplete: TOPIC_SUBSCRIBED_SUCCESSFULLY!" );
                }
            });
            switch (user.getAccountType()) {
                case Constants.ACCOUNT_TYPE_USER:
                    startUserHomeActivity();
                    break;
                case Constants.ACCOUNT_TYPE_ADMIN:
                    startAdminHomeActivity();
                    break;
                case Constants.ACCOUNT_TYPE_HOSTEL_OWNER:
                    startProviderHomeActivity();
                    break;
            }
            MyServicesControllerClass.startCustomBackgroundService(context.getApplicationContext());
        }
        else {
            Toast.makeText(context, "This account is't active now!", Toast.LENGTH_SHORT).show();
            MyFirebaseUser.SignOut(context);
        }

    }

    private void checkUserTypeFromDBToSignIn() {
        MyFirebaseDatabase.USER_REFERENCE.child(MyFirebaseUser.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.e(TAG, "onDataChange: USER");
                    try {
                        goToHomeAccordingToUserType(dataSnapshot.getValue(User.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
