package com.fmning.wpi_csa.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.WCService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fangming
 * On 12/28/2017.
 */

public class UserDetailListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private UserDetailListListener listener;
    private List<String> labelText;
    private List<String> placeHolderText;
    private List<String> userDetails;
    private List<String> userDetailsOriginal;
    private int offset = 3;

    private Uri avatarUri;

    public UserDetailListAdapter(Context context, UserDetailListListener listener) {
        this.context = context;
        this.listener = listener;
        labelText = Arrays.asList(context.getString(R.string.register_label_name),
                context.getString(R.string.register_label_birthday),
                context.getString(R.string.register_label_classof),
                context.getString(R.string.register_label_major));
        placeHolderText = Arrays.asList(context.getString(R.string.register_hint_name),
                context.getString(R.string.register_hint_birthday),
                context.getString(R.string.register_hint_classof),
                context.getString(R.string.register_hint_major));
        userDetails = Arrays.asList(WCService.currentUser.name, WCService.currentUser.birthday,
                WCService.currentUser.classOf, WCService.currentUser.major);
        userDetailsOriginal = new ArrayList<>(userDetails);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1) {
            return 1;
        } else if (position < 3) {
            return 2;
        } else if (position < 7) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_detail_avatar, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_separator, parent, false);
                return new ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_register_input, parent, false);
                return new ViewHolder(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_footer, parent, false);
                return new ViewHolder(view4);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final View cell = holder.itemView;

        if (position == 1) {
            if (avatarUri != null) {
                ((ImageView) cell.findViewById(R.id.userDetailAvatar)).setImageURI(avatarUri);
            } else if (WCService.currentUser.avatarId != -1) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.getImage(context, Utils.convertToWCImageId(WCService.currentUser.avatarId), new CacheManager.OnCacheGetImageListener() {
                            @Override
                            public void OnCacheGetImageDone(String error, final Bitmap image) {
                                ((ImageView) cell.findViewById(R.id.userDetailAvatar)).setImageBitmap(image);

                            }
                        });
                    }
                }, 200);

            } else {
                ((ImageView) cell.findViewById(R.id.userDetailAvatar))
                        .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
            }

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnAddAvatarClicked();
                }
            });
        } else if (position > 2 && position < 7) {
            final EditText editText = (EditText) cell.findViewById(R.id.registerValueField);
            editText.setHint(placeHolderText.get(position - offset));
            editText.setText(userDetails.get(position - offset));
            ((TextView) cell.findViewById(R.id.registerTitleField)).setText(labelText.get(position - offset));

            if (position == 3) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
                editText.setOnClickListener(null);
            } else if (position == 4) {
                editText.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month = String.format(Locale.getDefault(), "%02d", monthOfYear + 1);
                                String day = String.format(Locale.getDefault(), "%02d", dayOfMonth);
                                String date = String.format(context.getString(R.string.simple_date),
                                        month, day, Integer.toString(year).substring(2));
                                userDetails.set(1, date);
                                editText.setText(date);
                            }

                        };
                        Calendar myCalendar = Calendar.getInstance();
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            } else if (position == 5) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
                editText.setOnClickListener(null);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText.setFocusable(true);
                editText.setOnClickListener(null);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int pos = holder.getAdapterPosition();
                    userDetails.set(pos - offset, charSequence.toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public boolean isValueChanged() {
        for (int i = 0; i < userDetails.size(); i ++) {
            if (!userDetails.get(i).trim().equals(userDetailsOriginal.get(i).trim())) {
                Utils.logMsg("Diff! " + userDetails.get(i) + " " + userDetailsOriginal.get(i));
                return true;
            }
        }
        return avatarUri != null;
    }

    public void setAvatarUri(Uri uri) {
        avatarUri = uri;
    }

    public List<String> getUserDetails() {
        return userDetails;
    }

    public interface UserDetailListListener {
        void OnAddAvatarClicked();
    }
}
