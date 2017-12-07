package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.WCService;
import com.fmning.wpi_csa.http.objects.WCUser;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by fangmingning
 * On 11/27/17.
 */

public class SettingListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private SettingListListener listener;
    private float dpRatio;

    public SettingListAdapter(Context context, SettingListListener listener){
        this.context = context;
        this.listener = listener;
        dpRatio = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 6; //separator cell;
        } else if (position == 1) {
            if (Utils.appMode == AppMode.OFFLINE) {
                return 1;
            } else if (Utils.appMode == AppMode.LOGIN) {
                return 2;
            } else {
                return 3;
            }
        } else if (position == 2) {
            return 6;
        } else if (position < 6) {
            return 4;// link cell
        } else if (position == 6) {
            return 6;
        } else if (position == 7) {
            return 5; //button cell
        } else {
            if (WCService.currentUser != null && WCService.currentUser.emailConfirmed){
                if (position == 8) {
                    return 6;
                } else if (position == 10){
                    return 7;
                }  else {
                    return 5;//log out button
                }
            } else {
                if (position == 8 || position == 10) {
                    return 5;//confirm and log outbutton
                } else if (position == 11){
                    return 7;
                } else {
                    return 6;
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting_offline, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting_login, parent, false);
                return new ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting_user, parent, false);
                return new ViewHolder(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting_link, parent, false);
                return new ViewHolder(view4);
            case 5:
                View view5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_button, parent, false);
                return new ViewHolder(view5);
            case 6:
                View view6 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_separator, parent, false);
                return new ViewHolder(view6);
            case 7:
                View view7 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_footer, parent, false);
                return new ViewHolder(view7);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        View cell = holder.itemView;
        if (position == 1) {
            if (Utils.appMode == AppMode.OFFLINE) {
                Button reconButton = (Button) cell.findViewById(R.id.settingReconnectButton);
                reconButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnReconnectClick();
                    }
                });
            } else if (Utils.appMode == AppMode.LOGIN) {

                final EditText usernameField = (EditText) cell.findViewById(R.id.settingUsernameField);
                final EditText passwordField = (EditText) cell.findViewById(R.id.settingPasswordField);
                passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String username = usernameField.getText().toString();
                            String password = passwordField.getText().toString();
                            listener.OnLogInClick(username, password);

                            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

                            if (imm != null && imm.isAcceptingText()) {
                                imm.hideSoftInputFromWindow(passwordField.getWindowToken(), 0);
                            }

                            return true;
                        }
                        return false;
                    }
                });

                Button registerButton = (Button) cell.findViewById(R.id.settingRegisterButton);
                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnRegisterClick();
                    }
                });

                Button loginButton = (Button) cell.findViewById(R.id.settingLoginButton);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = usernameField.getText().toString();
                        String password = passwordField.getText().toString();
                        listener.OnLogInClick(username, password);
                    }
                });
            } else {
                WCUser user = WCService.currentUser;
                ((TextView) cell.findViewById(R.id.settingUserNameText)).setText(user.name);
                ((TextView) cell.findViewById(R.id.settingUserEmaillText)).setText(user.username);
                TextView verifiedView = (TextView) cell.findViewById(R.id.settingUserEmailVerifiedText);
                int imageSize = (int)(dpRatio * 15);
                if (user.emailConfirmed) {
                    verifiedView.setText(context.getString(R.string.setting_email_verified));
                    Drawable img = ContextCompat.getDrawable(context, R.drawable.verified);
                    img.setBounds(0, 0, imageSize, imageSize);
                    verifiedView.setCompoundDrawables(img, null, null, null);
                } else {
                    verifiedView.setText(context.getString(R.string.setting_email_not_verified));
                    Drawable img = ContextCompat.getDrawable(context, R.drawable.not_verified);
                    img.setBounds(0, 0, imageSize, imageSize);
                    verifiedView.setCompoundDrawables(img, null, null, null);
                }

                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnUserDetailClick();
                    }
                });
            }
        } else if (position == 3) {
            ((ImageView) cell.findViewById(R.id.settingLinkIcon))
                    .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook));
            ((TextView) cell.findViewById(R.id.settingLinkText)).setText(context.getString(R.string.setting_facebook));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.settingLinkBotLine).getLayoutParams();
            layoutParams.setMarginStart((int)(15 * dpRatio));

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnFacebookClick();
                }
            });
        } else if (position == 4) {
            ((ImageView) cell.findViewById(R.id.settingLinkIcon))
                    .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.instagram));
            ((TextView) cell.findViewById(R.id.settingLinkText)).setText(context.getString(R.string.setting_instagram));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.settingLinkBotLine).getLayoutParams();
            layoutParams.setMarginStart((int)(15 * dpRatio));

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnInstagramClick();
                }
            });
        } else if (position == 5) {
            ((ImageView) cell.findViewById(R.id.settingLinkIcon))
                    .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.youtube));
            ((TextView) cell.findViewById(R.id.settingLinkText)).setText(context.getString(R.string.setting_youtube));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.settingLinkBotLine).getLayoutParams();
            layoutParams.setMarginStart(0);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnYouTubeClick();
                }
            });
        } else if (position == 7) {
            ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.setting_change_pwd));

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnChangePwdClick();
                }
            });
        } else {
            if (WCService.currentUser != null && WCService.currentUser.emailConfirmed){
                if (position == 9) {
                    ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.setting_log_out));

                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.OnLogOutClick();
                        }
                    });
                }
            } else {
                if (position == 8) {
                    ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.setting_verify_email));

                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.OnVerifyEmailClick();
                        }
                    });
                } else if (position == 10) {
                    ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.setting_log_out));

                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.OnLogOutClick();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (Utils.appMode == AppMode.LOGGED_ON) {
            WCUser user = WCService.currentUser;
            if (user != null && !user.emailConfirmed) {
                return 12;// 1 sep cell, 1 user cell, 1 sep cell, 3 link cells, 1 sep cell, 2 password and email cells, 1 sep cell, 1 logout cell, 1 footer cell
            } else {
                return 11;// 1 sep cell, 1 user cell, 1 sep cell, 3 link cells, 1 sep cell, 1 password cell, 1 sep cell, 1 logout cell, 1 footer cell
            }
        } else {
            return 6;// 1 sep cell, 1 user cell, 1 sep cell, 3 link cells
        }
    }

    public interface SettingListListener {
        void OnReconnectClick();
        void OnLogInClick(String username, String password);
        void OnRegisterClick();
        void OnUserDetailClick();
        void OnFacebookClick();
        void OnInstagramClick();
        void OnYouTubeClick();
        void OnChangePwdClick();
        void OnVerifyEmailClick();
        void OnLogOutClick();
    }
}
