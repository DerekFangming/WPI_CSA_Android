package com.fmning.wpi_csa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;

/**
 * Created by fangmingning
 * On 12/27/17.
 */

public class UserDetailFragment extends Fragment {

    public UserDetailFragment(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);

        return view;
    }
}
