package com.fmning.wpi_csa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.FeedListAdapter;
import com.fmning.wpi_csa.helpers.LoadingView;
import com.fmning.wpi_csa.http.objects.WCFeed;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedFragment extends Fragment {

    private FeedListAdapter tableViewAdapter;

    public FeedFragment(){}

    public static FeedFragment withFeed(WCFeed feed) {
        FeedFragment fragment = new FeedFragment();
        fragment.tableViewAdapter = new FeedListAdapter(feed);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView tableView = (RecyclerView) view.findViewById(R.id.feedList);
        LoadingView loadingView = (LoadingView) view.findViewById(R.id.lifeLoadingView);



        tableView.setAdapter(tableViewAdapter);

        return view;
    }
}
