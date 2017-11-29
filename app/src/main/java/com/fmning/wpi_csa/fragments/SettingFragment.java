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
            tableViewAdapter.notifyDataSetChanged();
        }
    };

    SettingListAdapter tableViewAdapter;

    public SettingFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View settingView = (View)inflater.inflate(R.layout.fragment_setting, container, false);

        RecyclerView tableView = (RecyclerView)settingView.findViewById(R.id.settingList);

        tableViewAdapter = new SettingListAdapter(getActivity(), new SettingListAdapter.SettingListListener() {
            @Override
            public void OnReconnectClick() {
                Utils.logMsg("recon");
            }

            @Override
            public void OnLogInClick(String username, String password) {
                Utils.logMsg("log in " + username + "  "  + password);
            }

            @Override
            public void OnRegisterClick() {
                Utils.logMsg("register");
            }

            @Override
            public void OnUserDetailClick() {
                Utils.logMsg("go to user detail");
            }

            @Override
            public void OnFacebookClick() {
                Utils.logMsg("fb");
            }

            @Override
            public void OnInstagramClick() {
                Utils.logMsg("inst");
            }

            @Override
            public void OnYouTubeClick() {
                Utils.logMsg("you tube");
            }

            @Override
            public void OnChangePwdClick() {
                Utils.logMsg("change password");
            }

            @Override
            public void OnVerifyEmailClick() {
                Utils.logMsg("verify email");
            }

            @Override
            public void OnLogOutClick() {
                Utils.logMsg("log out");
            }
        });

        tableView.setAdapter(tableViewAdapter);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("reloadUserCell"));

        return settingView;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


}
