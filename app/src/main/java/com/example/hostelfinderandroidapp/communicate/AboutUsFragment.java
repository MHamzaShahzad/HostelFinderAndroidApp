package com.example.hostelfinderandroidapp.communicate;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hostelfinderandroidapp.R;
import com.example.hostelfinderandroidapp.common.Constants;
import com.example.hostelfinderandroidapp.interfaces.FragmentInteractionListenerInterface;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    private FragmentInteractionListenerInterface mListener;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(Constants.TITLE_ABOUT_US);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement FragmentInteractionListenerInterface.");
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
        if (mListener != null)
            mListener.onFragmentInteraction(Constants.TITLE_ABOUT_US);
    }

}
