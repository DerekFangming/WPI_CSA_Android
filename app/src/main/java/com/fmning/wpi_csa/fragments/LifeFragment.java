package com.fmning.wpi_csa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.LifeListAdapter;
import com.fmning.wpi_csa.helpers.LoadingView;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCFeedManager;
import com.fmning.wpi_csa.webService.objects.WCFeed;

import java.util.List;


public class LifeFragment extends Fragment {
    SwipeRefreshLayout refreshControl;
    RecyclerView tableView;
    LoadingView loadingView;

    String checkPoint;
    boolean serverDownFlag = false;
    boolean reloadingFlag = false;
    boolean keepLoadingFlag = false;
    boolean stopLoadingFlag = false;

    int feedLoadLimit = 3;

    LifeListAdapter tableViewAdapter;

    public LifeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //CacheManager.localDirInitiateSetup(getActivity());

        /*============================== TESTING AREA STARTS ==============================*/

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Utils.appMode = AppMode.LOGGED_ON;
//                WCUser user = new WCUser(1, "fangming", "token");
//                user.name = "Fangming Ning";
//                user.emailConfirmed = true;
//                WCService.currentUser = user;
//                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("reloadUserCell"));
//            }
//        }, 2000);

        //String a = Utils.getParam(getActivity(), Utils.reportEmail);


        Utils.logMsg("bkpoint for testing area");
        /*============================== TESTING AREA ENDS ==============================*/

        View parentView = inflater.inflate(R.layout.fragment_life, container, false);
        refreshControl = (SwipeRefreshLayout)parentView.findViewById(R.id.swipeRefreshLayout);
        refreshControl.setEnabled(false);
        refreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(false);
            }
        });


        tableView = (RecyclerView) refreshControl.findViewById(R.id.lifeList);

        Context context = tableView.getContext();
        tableView.setLayoutManager(new LinearLayoutManager(context));


        tableViewAdapter = new LifeListAdapter(getActivity(), new LifeListAdapter.LifeListListener() {
            @Override
            public void OnFeedClick(WCFeed feed) {
                //Utils.logMsg(Integer.toString(index) + " is clicked");
                Fragment fragment = FeedFragment.withFeed(getActivity(), feed);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.lifeFragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void OnScrollToLastFeed() {
                if (!stopLoadingFlag) {
                    keepLoading();
                }
            }
        });

        tableView.setAdapter(tableViewAdapter);

        //Checking version info
        Utils.checkVerisonInfoAndLoginUser(getActivity(), false);

        //Setting up loading view
        loadingView = (LoadingView) refreshControl.findViewById(R.id.lifeLoadingView);
        loadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(true);
            }
        });
        loadingView.setVisibleChangeListener(new LoadingView.OnVisibleChangedListener() {
            @Override
            public void OnVisible(boolean visible) {
                if (visible) {
                    refreshControl.setEnabled(false);
                } else {
                    refreshControl.setEnabled(true);
                }
            }
        });

        //Requesting for feeds
        reloadingFlag = true;
        WCFeedManager.getRecentFeeds(getActivity(), feedLoadLimit, checkPoint, new WCFeedManager.OnGetRecentFeedListener() {
            @Override
            public void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint) {
                if (error.equals("")) {
                    LifeFragment.this.checkPoint = checkPoint;
                } else {
                    Utils.logMsg(error);// Do nothing if there are no feed
                }

                if (feedList.size() < feedLoadLimit) {
                    stopLoadingFlag = true;
                }
                reloadingFlag = false;
                tableViewAdapter.setFeedList(feedList, stopLoadingFlag);
                tableViewAdapter.notifyDataSetChanged();
                if (error.equals(getActivity().getString(R.string.server_down_error))) {
                    serverDownFlag = true;
                    loadingView.showServerDownView();
                } else {
                    loadingView.removeFromSuperview();
                }

            }
        });


        return parentView;
    }

    private void refresh(boolean refreshFromTappingServerDownScreen){
        Utils.logMsg("clicked refresh");
        if (reloadingFlag) {
            return;
        } else {
            stopLoadingFlag = false;
            reloadingFlag = true;
        }

        if (serverDownFlag && refreshFromTappingServerDownScreen) {// Tap on screen when it shows server down
            loadingView.removeServerDownView();
        }

        checkPoint = null;
        WCFeedManager.getRecentFeeds(getActivity(), feedLoadLimit, checkPoint, new WCFeedManager.OnGetRecentFeedListener() {
            @Override
            public void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint) {
                if (error.equals("")) {
                    LifeFragment.this.checkPoint = checkPoint;
                } else {
                    Utils.logMsg(error);
                }

                if (error.equals(getActivity().getString(R.string.server_down_error))) {
                    serverDownFlag = true;
                    loadingView.showServerDownView();
                    loadingView.addToSuperview();
                } else {
                    if (feedList.size() < feedLoadLimit) {
                        stopLoadingFlag = true;
                    }
                    loadingView.removeFromSuperview();
                    serverDownFlag = false;
                }
                reloadingFlag = false;
                tableViewAdapter.setFeedList(feedList, stopLoadingFlag);
                tableViewAdapter.notifyDataSetChanged();
                refreshControl.setRefreshing(false);
            }
        });
    }

    private void keepLoading() {
        if (keepLoadingFlag) {
            return;
        } else {
            keepLoadingFlag = true;
        }

        WCFeedManager.getRecentFeeds(getActivity(), feedLoadLimit, checkPoint, new WCFeedManager.OnGetRecentFeedListener() {
            @Override
            public void OnGetRecentFeedDone(String error, List<WCFeed> feedList, String checkPoint) {
                if (error.equals("")) {
                    LifeFragment.this.checkPoint = checkPoint;
                } else {
                    Utils.logMsg(error);
                }

                if (error.equals(getActivity().getString(R.string.server_down_error))) {
                    serverDownFlag = true;
                    loadingView.showServerDownView();
                    loadingView.addToSuperview();
                } else {
                    if (feedList.size() < feedLoadLimit) {
                        stopLoadingFlag = true;
                    }
                }
                keepLoadingFlag = false;
                tableViewAdapter.appendFeedList(feedList, stopLoadingFlag);
                tableViewAdapter.notifyDataSetChanged();
            }
        });
    }


}
