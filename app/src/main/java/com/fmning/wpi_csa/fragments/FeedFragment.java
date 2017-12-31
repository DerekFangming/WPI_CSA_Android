package com.fmning.wpi_csa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.FeedListAdapter;
import com.fmning.wpi_csa.helpers.LoadingView;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.WCFeedManager;
import com.fmning.wpi_csa.http.objects.WCEvent;
import com.fmning.wpi_csa.http.objects.WCFeed;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedFragment extends Fragment {

    private static int feedId;

    private FeedListAdapter tableViewAdapter;

    public FeedFragment(){}

    public static FeedFragment withFeed(Context context, WCFeed feed) {
        FeedFragment fragment = new FeedFragment();
        feedId = feed.id;
        fragment.tableViewAdapter = new FeedListAdapter(context, feed);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView tableView = (RecyclerView) view.findViewById(R.id.feedList);
        final LoadingView loadingView = (LoadingView) view.findViewById(R.id.feedLoadingView);

        view.findViewById(R.id.feedBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        WCFeedManager.getFeed(getActivity(), feedId, new WCFeedManager.OnGetFeedListener() {
            @Override
            public void OnGetFeedDone(String error, WCFeed feed) {
                if (!error.equals("")) {
                    Utils.processErrorMessage(getActivity(), error, true);
                } else {
                    tableViewAdapter.setAndProcessFeed(feed);
                    tableViewAdapter.notifyDataSetChanged();
                    loadingView.setVisibility(View.GONE);
                }
            }
        });

        tableViewAdapter.setOnFeedListener(new FeedListAdapter.FeedListListener() {
            @Override
            public void addToCalendar(WCEvent event) {
                Utils.logMsg("calendar");
            }

            @Override
            public void payAndGetTicket(WCEvent event) {
                Utils.logMsg("ticket");
            }
        });
        tableView.setAdapter(tableViewAdapter);

        return view;
    }
}
