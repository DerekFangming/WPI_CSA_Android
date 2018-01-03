package com.fmning.wpi_csa.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.RegisterListAdapter;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCService;
import com.fmning.wpi_csa.webService.WCUserManager;
import com.fmning.wpi_csa.webService.WCUtils;
import com.fmning.wpi_csa.webService.objects.WCUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by fangmingning
 * On 12/4/17.
 */

public class RegisterActivity extends AppCompatActivity {

    RegisterListAdapter tableViewAdapter;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        RecyclerView tableView = (RecyclerView) findViewById(R.id.registerList);

        tableViewAdapter = new RegisterListAdapter(this, new RegisterListAdapter.RegisterListListener() {
            @Override
            public void OnAddAvatarClicked() {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }

            @Override
            public void OnRegisterClicked(final String username, final String name, final String password, String confirm,
                                          final String birthday, final String classOf, final String major, final Uri avatar) {

                if (username ==  null || username.trim().equals("") || !Utils.isEmailAddress(username.trim())) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.username_format_error));
                    return;
                }

                if (name == null || name.trim().equals("")) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.name_empty_error));
                    return;
                }

                if (name.trim().length() > 20) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.name_too_long_error));
                    return;
                }

                if (password == null) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.password_empty_error));
                    return;
                }

                String pwdStrengthCheck = Utils.checkPasswordStrength(password);
                if (!pwdStrengthCheck.equals("")) {
                    Utils.showAlertMessage(RegisterActivity.this, pwdStrengthCheck);
                    return;
                }

                if (confirm == null || !confirm.equals(password)) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.pwd_not_match_error));
                    return;
                }

                if (classOf != null) {
                    try {
                        int year = Integer.parseInt(classOf);
                        if (String.valueOf(year).length() != 4) {
                            Utils.showAlertMessage(RegisterActivity.this, getString(R.string.year_format_error));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Utils.showAlertMessage(RegisterActivity.this, getString(R.string.year_format_error));
                        return;
                    }
                }

                if (major != null && major.length() > 10) {
                    Utils.showAlertMessage(RegisterActivity.this, getString(R.string.major_too_long_error));
                    return;
                }

                Utils.showLoadingIndicator(RegisterActivity.this);
                WCUserManager.regesterSalt(RegisterActivity.this, username.trim(), new WCUserManager.OnRegisterSaltListener() {
                    @Override
                    public void OnRegisterSaltDone(String error, final String salt) {
                        if (error.equals("")) {
                            WCUserManager.register(RegisterActivity.this, username.trim(),
                                WCUtils.md5(password + salt), new WCUserManager.OnRegisterListener() {
                                    @Override
                                    public void OnRegisterDone(String error, final WCUser user) {
                                        if (error.equals("")) {
                                            WCService.currentUser = user;
                                            Utils.appMode = AppMode.LOGGED_ON;

                                            String base64 = null;
                                            if (avatar != null) {
                                                try {
                                                    selectedImage = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), avatar);
                                                    //int compressRate = Utils.compressRateForSize(selectedImage, 250);
                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    //TODO: Find a better way to compress. Currently default to 80
                                                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                                                    base64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                                                } catch (IOException e) {
                                                    Utils.logMsg(e.getMessage());//TODO: Do something here?
                                                }
                                            }

                                            WCUserManager.saveCurrentUserDetails(RegisterActivity.this, name.trim(),
                                                birthday, classOf, major, base64, new WCUserManager.OnSaveUserDetailsListener() {
                                                    @Override
                                                    public void OnSaveUserDetailsDone(String error, int imageId) {
                                                        if (error.equals("")) {
                                                            WCService.currentUser.name = name.trim();
                                                            if (birthday != null) {
                                                                WCService.currentUser.birthday = birthday;
                                                            }
                                                            if (classOf != null) {
                                                                WCService.currentUser.classOf = classOf;
                                                            }
                                                            if (major != null) {
                                                                WCService.currentUser.major = major;
                                                            }
                                                            if (imageId != -1) {
                                                                WCService.currentUser.avatarId = imageId;
                                                                CacheManager.saveImageToLocal(RegisterActivity.this, selectedImage, imageId);
                                                            }
                                                            Utils.setParam(Utils.savedUsername, username.trim());
                                                            Utils.setParam(Utils.savedPassword, WCUtils.md5(password + salt));

                                                            LocalBroadcastManager.getInstance(RegisterActivity.this)
                                                                    .sendBroadcast(new Intent("reloadUserCell"));
                                                            Utils.hideLoadingIndicator();
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                            builder.setTitle(null)
                                                                    .setCancelable(false)
                                                                    .setMessage(String.format(RegisterActivity.this
                                                                            .getString(R.string.register_done_message), user.username))
                                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .show();


                                                        } else {
                                                            LocalBroadcastManager.getInstance(RegisterActivity.this)
                                                                    .sendBroadcast(new Intent("reloadUserCell"));
                                                            Utils.hideLoadingIndicator();
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                            builder.setTitle(null)
                                                                    .setCancelable(false)
                                                                    .setMessage(String.format(RegisterActivity.this
                                                                            .getString(R.string.register_detail_fail_error), error))
                                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    }
                                                });
                                        } else {
                                            Utils.hideLoadingIndicator();
                                            Utils.processErrorMessage(RegisterActivity.this, error, true);
                                        }
                                    }
                                });
                        } else {
                            Utils.hideLoadingIndicator();
                            Utils.processErrorMessage(RegisterActivity.this, error, true);
                        }
                    }
                });

            }
        });

        tableView.setAdapter(tableViewAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if(resultCode == RESULT_OK){
            Uri selectedImage = imageReturnedIntent.getData();
            tableViewAdapter.setAvatarUri(selectedImage);
            tableViewAdapter.notifyItemChanged(1);
        }
    }

}
