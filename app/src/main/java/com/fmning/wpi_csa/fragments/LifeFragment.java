package com.fmning.wpi_csa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.LifeListAdapter;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.ArrayList;


public class LifeFragment extends Fragment {

    ArrayList feedItemList;
    LifeListAdapter adapter;

    public LifeFragment() {
        // Required empty public constructor
    }



    public static LifeFragment newInstance(String param1, String param2) {
        LifeFragment fragment = new LifeFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.mParam1 = param1;*/
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)inflater.inflate(R.layout.fragment_life, container, false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.logMsg("Refreshed!");
            }
        });

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.life_list);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        feedItemList = new ArrayList<WCFeed>();
        /*for (int i = 0; i < 1; i++) {
            WCFeed feed = new WCFeed();
            feedItemList.add(feed);
        }*/
        adapter = new LifeListAdapter(feedItemList, new OnFeedClickListener() {
            @Override
            public void OnFeedClick(int index) {
                Utils.logMsg(Integer.toString(index) + " is clicked");
            }
        });

        recyclerView.setAdapter(adapter);

        /*new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    WCFeed getterSetter = new WCFeed();
                    feedItemList.add(getterSetter);
                }

                adapter.notifyDataSetChanged();
            }
        }, 5000);*/

        //CacheManager.localDirInitiateSetup(getActivity());

        //Database.test1(getActivity());

        /*CacheManager.getImage("WCImage_20", getActivity(), new CacheManager.OnCacheGetImageDoneListener() {
            @Override
            public void OnCacheGetImageDone(String error, Bitmap image) {
                if (error.equals("")){
                    Utils.logMsg("ok");
                } else {
                    Utils.logMsg(error);
                }
            }
        });*/

        /*Utils.logMsg("onCreateView");
        WCFeedManager.getRecentFeeds(getActivity(), 3, null, new WCFeedManager.OnGetRecentFeedListener() {
            @Override
            public void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint) {
                Utils.logMsg("1");
            }
        });*/


        return recyclerView;
    }


    public interface OnFeedClickListener {
        void OnFeedClick(int index);
    }
}
