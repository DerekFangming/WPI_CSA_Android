package com.fmning.wpi_csa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.LifeListAdapter;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.ArrayList;

//import com.loopj.android.http.


public class LifeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public LifeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LifeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LifeFragment newInstance(String param1, String param2) {
        Log.i("d", "initttttttttttt" + param1);
        LifeFragment fragment = new LifeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.mParam1 = param1;
        return fragment;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/
    ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_life, container, false);

        Context context = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        //adding data to arraylist
        ArrayList feedItemList = new ArrayList<WCFeed>();
        for (int i = 0; i < 20; i++) {
            WCFeed getterSetter = new WCFeed();
            feedItemList.add(getterSetter);
        }


        recyclerView.setAdapter(new LifeListAdapter(context, feedItemList));

        return recyclerView;
    }


}
