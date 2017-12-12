package com.fmning.wpi_csa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.SGListAdapter;


public class SGFragment extends Fragment {


    public SGFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sg, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.SGList);
        recyclerView.setAdapter(new SGListAdapter(getActivity()));

        final ImageView coverImage = (ImageView) view.findViewById(R.id.SGCoverImage);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coverImage.setVisibility(View.GONE);
            }
        });




        return view;
    }

}
