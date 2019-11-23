package com.example.hostelfinderandroidapp.user;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;
import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.controlers.MyFirebaseDatabase;
import com.example.hostelfinderandroidapp.model.Booking;
import com.example.hostelfinderandroidapp.model.Hostel;
import com.example.hostelfinderandroidapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBookingForHostelRooms extends Fragment {

    private static final String TAG = FragmentBookingForHostelRooms.class.getName();
    private Context context;
    private View view;

    private EditText userName, userMobileNumber, userCNIC, userBookingMessage, userBelongsInstitute,
            dateBookFrom, dateBookTill;
    private RadioButton userCatStudent, userCatJobHolder, userCatOthers, radioButtonHostelForBoys, radioButtonHostelForGirls;
    private ImageView image_view_book_from_pick, image_view_book_till_pick;
    private Button submitHostelBooking;

    private static final String myFormat = "dd MMMM yyyy"; //In which you need put here
    private Calendar myCalendar;

    private FirebaseUser firebaseUser;
    private Hostel hostel;
    private FragmentInteractionListenerInterface mListener;

    public FragmentBookingForHostelRooms() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction("Book Hostel");
        context = container.getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_booking_for_hostel_rooms, container, false);

            initLayoutWidgets();
            initDateTimeTexts();
            getHostelData();
            getProfileData();
            setBtnSubmitBookingReq();
        }
        return view;
    }

    private void getHostelData() {
        Bundle bundleArguments = getArguments();
        if (bundleArguments != null) {
            try {

                hostel = (Hostel) bundleArguments.getSerializable(Constants.HOSTEL_OBJECT);
                if (hostel != null) {

                    switch (hostel.getType()) {
                        case Constants.HOSTEL_FOR_BOYS:
                            radioButtonHostelForBoys.setChecked(true);
                            break;
                        case Constants.HOSTEL_FOR_GIRLS:
                            radioButtonHostelForGirls.setChecked(true);
                            break;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getProfileData() {
        MyFirebaseDatabase.USER_REFERENCE.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            userName.setText(user.getUserName());
                            userMobileNumber.setText(user.getPhone());
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

    private void initLayoutWidgets() {

        userName = view.findViewById(R.id.userName);
        userMobileNumber = view.findViewById(R.id.userMobileNumber);
        userCNIC = view.findViewById(R.id.userCNIC);
        userBookingMessage = view.findViewById(R.id.userBookingMessage);
        userBelongsInstitute = view.findViewById(R.id.userBelongsInstitute);

        dateBookFrom = view.findViewById(R.id.dateBookFrom);
        dateBookTill = view.findViewById(R.id.dateBookTill);

        userCatStudent = view.findViewById(R.id.userCatStudent);
        userCatJobHolder = view.findViewById(R.id.userCatJobHolder);
        userCatOthers = view.findViewById(R.id.userCatOthers);

        radioButtonHostelForBoys = view.findViewById(R.id.radioButtonHostelForBoys);
        radioButtonHostelForGirls = view.findViewById(R.id.radioButtonHostelForGirls);

        image_view_book_from_pick = view.findViewById(R.id.image_view_book_from_pick);
        image_view_book_till_pick = view.findViewById(R.id.image_view_book_till_pick);

        submitHostelBooking = view.findViewById(R.id.submitHostelBooking);

    }

    private void setBtnSubmitBookingReq() {
        submitHostelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid())
                    uploadBookingRequestToDatabase();
            }
        });
    }

    private void initDateTimeTexts() {
        myCalendar = Calendar.getInstance();
        textDateFromListener();
        textDateToListener();
    }

    public void textDateFromListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom();
            }

        };


        image_view_book_from_pick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void textDateToListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelTo();
            }

        };


        image_view_book_till_pick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelFrom() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateBookFrom.setText(sdf.format(myCalendar.getTime()));
        dateBookFrom.requestFocus();
        dateBookFrom.setFocusable(true);
        dateBookFrom.setSelection(dateBookFrom.getText().toString().length());
    }

    private void updateLabelTo() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateBookTill.setText(sdf.format(myCalendar.getTime()));
        dateBookTill.requestFocus();
        dateBookTill.setFocusable(true);
        dateBookTill.setSelection(dateBookTill.getText().toString().length());
    }

    private String getSelectedUserCat() {
        if (userCatStudent.isChecked())
            return Constants.USER_CAT_STUDENT;
        if (userCatJobHolder.isChecked())
            return Constants.USER_CAT_JOB_HOLDER;
        if (userCatOthers.isChecked())
            return Constants.USER_CAT_OTHERS;
        return null;
    }

    private Booking buildBookingInstance() {
        return new Booking(
                firebaseUser.getUid(),
                hostel.getHostelId(),
                Constants.BOOKING_STATUS_PENDING,
                userName.getText().toString().trim(),
                userCNIC.getText().toString().trim(),
                userMobileNumber.getText().toString().trim(),
                userBookingMessage.getText().toString().trim(),
                dateBookFrom.getText().toString().trim(),
                dateBookTill.getText().toString().trim(),
                new SimpleDateFormat(myFormat, Locale.US).format(myCalendar.getTime()),
                userBelongsInstitute.getText().toString().trim(),
                getSelectedUserCat()
        );
    }

    private void uploadBookingRequestToDatabase() {
        MyFirebaseDatabase.BOOKINGS_REFERENCE.child(hostel.getHostelId()).child(firebaseUser.getUid()).setValue(buildBookingInstance()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Booking Request Sent Successfully!", Toast.LENGTH_LONG).show();
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                } else
                    Toast.makeText(context, "Booking Request Sending Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isFormValid() {
        boolean result = true;
        if (userName.length() == 0) {
            result = false;
            userName.setError("Field is required!");
        }
        if (userMobileNumber.length() == 0) {
            result = false;
            userMobileNumber.setError("Field is required!");
        }
        if (userCNIC.length() == 0) {
            result = false;
            userCNIC.setError("Field is required!");
        }
        if (userBookingMessage.length() == 0) {
            result = false;
            userBookingMessage.setError("Field is required!");
        }
        if (userBelongsInstitute.length() == 0) {
            result = false;
            userBelongsInstitute.setError("Field is required!");
        }
        if (dateBookFrom.length() == 0) {
            result = false;
            dateBookFrom.setError("Field is required!");
        }
        if (dateBookTill.length() == 0) {
            result = false;
            dateBookTill.setError("Field is required!");
        }
        if (getSelectedUserCat() == null) {
            result = false;
            Toast.makeText(context, "Select User Category!", Toast.LENGTH_LONG).show();
        }
        return result;
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
            mListener.onFragmentInteraction("Book Hostel");
        }
    }

}
