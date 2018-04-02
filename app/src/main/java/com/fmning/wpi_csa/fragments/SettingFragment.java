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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.activities.RegisterActivity;
import com.fmning.wpi_csa.adapters.SettingListAdapter;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCService;
import com.fmning.wpi_csa.webService.WCUserManager;
import com.fmning.wpi_csa.webService.objects.WCUser;

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
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                if (username.trim().equals("")) {
                    Utils.showAlertMessage(getActivity(), getString(R.string.login_empty_username));
                    return;
                }
                if (password.trim().equals("")) {
                    Utils.showAlertMessage(getActivity(), getString(R.string.login_empty_password));
                    return;
                }


                Utils.showLoadingIndicator(getActivity());
                WCUserManager.loginUser(getActivity(), username, password, new WCUserManager.OnLoginUserListener() {
                    @Override
                    public void OnLoginUserDone(String error, WCUser user) {
                        if (error.equals("")) {
                            Utils.appMode = AppMode.LOGGED_ON;
                            Utils.hideLoadingIndicator();
                            tableViewAdapter.notifyDataSetChanged();
                        } else {
                            Utils.hideLoadingIndicator();
                            Utils.processErrorMessage(getActivity(), error, true);
                        }
                    }
                });

            }

            @Override
            public void OnRegisterClick() {

                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

            @Override
            public void OnForgetClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter your username (email)");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                input.setPadding(Utils.padding15, 0, Utils.padding15, Utils.padding15);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = input.getText().toString().trim();
                        if(email.equals("")) {
                            Utils.showAlertMessage(getActivity(), getString(R.string.report_email_hint));
                        } else {
                            Utils.showLoadingIndicator(getActivity());
                            WCUserManager.resetPassword(getActivity(), email, new WCUserManager.OnResetPasswordListener() {
                                @Override
                                public void OnResetPasswordDone(String error) {
                                    Utils.hideLoadingIndicator();
                                    if (error.equals("")) {
                                        Utils.showAlertMessage(getActivity(), String.format(getString(R.string.pwd_reset_done_message), email));
                                    } else {
                                        Utils.processErrorMessage(getActivity(), error, true);
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                new android.os.Handler().postDelayed(new Runnable() {// TODO IS THIS NEEDE ON REAL DEVICE?
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200);
            }

            @Override
            public void OnUserDetailClick() {
                Fragment fragment = new UserDetailFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.settingFragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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
                                final String oldPass = oldPwd.getText().toString();
                                final String newPass = newPwd.getText().toString();
                                String confirmNewPass = confirmPwd.getText().toString();
                                String error;

                                if (oldPass.trim().equals("")) {
                                    error = getActivity().getString(R.string.new_pwd_empty_error);
                                } else {
                                    error = Utils.checkPasswordStrength(newPass);
                                    if (error.equals("") && !newPass.equals(confirmNewPass)) {
                                        error = getActivity().getString(R.string.pwd_not_match_error);
                                    }
                                }

                                if (error.equals("")){
                                    String username = WCService.currentUser.username;
                                    Utils.showLoadingIndicator(getActivity());
                                    WCUserManager.changePassword(getActivity(), oldPass, newPass, new WCUserManager.OnChangePasswordListener() {
                                        @Override
                                        public void OnChangePasswordDone(String error) {
                                            if (error.equals("")) {
                                                Utils.hideLoadingIndicator();
                                                Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.done));
                                            } else {
                                                Utils.hideLoadingIndicator();
                                                Utils.processErrorMessage(getActivity(), error, true);
                                            }
                                        }
                                    });

                                } else {
                                    Utils.showAlertMessage(getActivity(), error);
                                }
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.cancel), null).show();


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
                WCUserManager.sendEmailConfirmation(getActivity(), new WCUserManager.OnSendEmailConfirmationListener() {
                    @Override
                    public void OnSendEmailConfirmationDone(String error) {
                        if (error.equals("")) {
                            Utils.showAlertMessage(getActivity(), getActivity().getString(R.string.setting_email_confirm_msg1)
                                    + WCService.currentUser.username + getActivity().getString(R.string.setting_email_confirm_msg2));
                        } else {
                            Utils.processErrorMessage(getActivity(), error, true);
                        }
                    }
                });
            }

            @Override
            public void OnLogOutClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false).setTitle(getActivity().getString(R.string.setting_logout_title))
                        .setMessage(getActivity().getString(R.string.setting_logout_message))
                        .setPositiveButton(getActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                WCService.currentUser = null;
                                Utils.appMode = AppMode.LOGIN;
                                Utils.deleteParam(Utils.savedAccessToken);
                                tableViewAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.cancel), null).show();
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
