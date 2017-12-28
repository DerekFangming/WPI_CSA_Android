package com.fmning.wpi_csa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        Button backButton = (Button) view.findViewById(R.id.userDetailBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
