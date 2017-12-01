package com.fmning.wpi_csa.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.SettingListAdapter;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.WCService;
import com.fmning.wpi_csa.http.WCUtils;
import com.fmning.wpi_csa.http.objects.WCUser;
import com.fmning.wpi_csa.http.WCUserManager;

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View settingView = inflater.inflate(R.layout.fragment_setting, container, false);

        RecyclerView tableView = (RecyclerView)settingView.findViewById(R.id.settingList);

        tableViewAdapter = new SettingListAdapter(getActivity(), new SettingListAdapter.SettingListListener() {
            @Override
            public void OnReconnectClick() {
                Utils.checkVerisonInfoAndLoginUser(getActivity(), true);
            }

            @Override
            public void OnLogInClick(final String username, final String password) {
                Utils.logMsg("log in " + username + "  "  + password);
                Utils.showLoadingIndicator(getActivity());
                WCUserManager.getSaltForUser(getActivity(), username, new WCUserManager.OnGetUserSaltListener() {
                    @Override
                    public void OnGetUserSaltDone(String error, String salt) {
                        if (error.equals("")) {
                            final String encryptedPassword = WCUtils.md5(password + salt);
                            WCUserManager.loginUser(getActivity(), username, encryptedPassword,
                                    new WCUserManager.OnLoginUserListener() {
                                @Override
                                public void OnLoginUserDone(String error, WCUser user) {
                                    if (error.equals("")) {
                                        WCService.currentUser = user;
                                        Utils.appMode = AppMode.LOGGED_ON;
                                        Utils.setParam(getActivity(), Utils.savedUsername, username);
                                        Utils.setParam(getActivity(), Utils.savedPassword, encryptedPassword);
                                        Utils.hideLoadingIndicator();
                                        tableViewAdapter.notifyDataSetChanged();
                                    } else {
                                        Utils.hideLoadingIndicator();
                                        Utils.processErrorMessage(getActivity(), error, true);
                                    }
                                }
                            });
                        } else {
                            Utils.hideLoadingIndicator();
                            Utils.processErrorMessage(getActivity(), error, true);
                        }
                    }
                });
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/wpi.csa")));
            }

            @Override
            public void OnInstagramClick() {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/wpicsa")));
            }

            @Override
            public void OnYouTubeClick() {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/CSAWPI")));
            }

            @Override
            public void OnChangePwdClick() {
                View changePwdView = inflater.inflate(R.layout.view_change_pwd, container, false);
                final EditText oldPwd = (EditText) changePwdView.findViewById(R.id.settingChangePwdOld);
                final EditText newPwd = (EditText) changePwdView.findViewById(R.id.settingChangePwdNew);
                final EditText confirmPwd = (EditText) changePwdView.findViewById(R.id.settingChangePwdConfirm);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false).setTitle(getActivity().getString(R.string.setting_change_pwd)).setMessage(null).setView(changePwdView)
                        .setPositiveButton(getActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String oldPass = oldPwd.getText().toString();
                                String newPass = newPwd.getText().toString();
                                String confirmNewPass = confirmPwd.getText().toString();
                                String error = "";

                                if (oldPass.trim().equals("")) {
                                    error = getActivity().getString(R.string.new_pwd_empty_error);
                                } else {
                                    error = Utils.checkPasswordStrength(getActivity(), newPass);
                                    if (error.equals("") && !newPass.equals(confirmNewPass)) {
                                        error = getActivity().getString(R.string.pwd_not_match_error);
                                    }
                                }

                                if (error.equals("")){

                                } else {
                                    Utils.showAlertMessage(getActivity(), error);
                                }
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.cancel), null).show();


//                final AlertDialog dialog = builder.create();
//                dialog.show();

                //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


                new android.os.Handler().postDelayed(new Runnable() {// TODO IS THIS NEEDE ON REAL DEVICE?
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                            imm.showSoftInput(oldPwd, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200);

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
