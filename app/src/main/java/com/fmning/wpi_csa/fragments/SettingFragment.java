package com.fmning.wpi_csa.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.SettingListAdapter;
import com.fmning.wpi_csa.helpers.Utils;

/**
 * Created by fangmingning
 * On 11/25/2017.
 */

public class SettingFragment extends Fragment {

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.logMsg("1LLLLLLLLLLLLLLLLLL");
        }
    };

    SettingListAdapter tableViewAdapter;

    public SettingFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView tableView = (RecyclerView)inflater.inflate(R.layout.fragment_setting, container, false);

        tableViewAdapter = new SettingListAdapter();

        tableView.setAdapter(tableViewAdapter);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("reloadUserCell"));

        return tableView;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


}
