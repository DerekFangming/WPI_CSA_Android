package com.fmning.wpi_csa.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
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
import com.fmning.wpi_csa.helpers.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fangming
 * On 12/4/2017.
 */

public class RegisterListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private RegisterListListener listener;
    private List<String> labelText;
    private List<String> placeHolderText;

    private String username;
    private String name;
    private String password;
    private String confirm;
    private String birthday;
    private String classOf;
    private String major;

    private Uri avatarUri;

    public RegisterListAdapter(Context context, RegisterListListener listener) {
        this.context = context;
        this.listener = listener;
        labelText = Arrays.asList(context.getString(R.string.register_label_username),
                context.getString(R.string.register_label_name), context.getString(R.string.register_label_password),
                context.getString(R.string.register_label_confirm), context.getString(R.string.register_label_birthday),
                context.getString(R.string.register_label_classof), context.getString(R.string.register_label_major));
        placeHolderText = Arrays.asList(context.getString(R.string.register_hint_username),
                context.getString(R.string.register_hint_name), context.getString(R.string.register_hint_password),
                context.getString(R.string.register_hint_confirm), context.getString(R.string.register_hint_birthday),
                context.getString(R.string.register_hint_classof), context.getString(R.string.register_hint_major));
    }

    public void setAvatarUri(Uri uri) {
        avatarUri = uri;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 2 || position == 7 || position == 11) {
            return 1;//headers
        } else if (position == 1 ) {
            return 2;//avatar selector
        } else if (position == 12) {
            return 3;//button
        } else if (position == 13) {
            return 4;//footer
        } else {
            return 5;//input
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_separator, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_register_avatar, parent, false);
                return new ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_button, parent, false);
                return new ViewHolder(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_footer, parent, false);
                return new ViewHolder(view4);
            case 5:
                View view5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_register_input, parent, false);
                return new ViewHolder(view5);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        View cell = holder.itemView;
        Utils.logMsg("position " + Integer.toString(position) + " holder " + Integer.toString(holder.getAdapterPosition()));
        if (position == 0) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_avatar));
        } else if (position == 1) {
            if (avatarUri != null) {
                ((ImageView) cell.findViewById(R.id.registerAvatar)).setImageURI(avatarUri);
            }
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnAddAvatarClicked();
                }
            });
        } else if (position == 2) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_required));
        } else if (position < 7) {
            int offset = 3;
            EditText editText = (EditText) cell.findViewById(R.id.registerValueField);
            editText.setHint(placeHolderText.get(position - offset));
            ((TextView) cell.findViewById(R.id.registerTitleField)).setText(labelText.get(position - offset));

            if (position == 3) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
            } else if (position == 4) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
            } else if (position == 5) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText.setFocusable(true);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int pos = holder.getAdapterPosition();
                    if (pos == 3) {
                        username = charSequence.toString();
                    } else if (pos == 4) {
                        name = charSequence.toString();
                    } else if (pos == 5) {
                        password = charSequence.toString();
                    } else{
                        confirm = charSequence.toString();
                    }
                }
            });
        } else if (position == 7) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_optional));
        } else if (position < 11) {
            int offset = 8 - 4;
            final EditText editText = (EditText) cell.findViewById(R.id.registerValueField);
            editText.setHint(placeHolderText.get(position - offset));
            ((TextView) cell.findViewById(R.id.registerTitleField)).setText(labelText.get(position - offset));

            if (position == 8) {
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
                                birthday = date;
                                editText.setText(date);
                            }

                        };
                        Calendar myCalendar = Calendar.getInstance();
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            } else if (position == 9) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(true);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                editText.setFocusable(true);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int pos = holder.getAdapterPosition();
                    if (pos == 9) {
                        classOf = charSequence.toString();
                    } else if (pos == 10) {
                        major = charSequence.toString();
                    }
                }
            });
        } else if (position == 11) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText("");
        } else if (position == 12) {
            ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.register_activity));
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnRegisterClicked(username, name, password, confirm, birthday, classOf, major, avatarUri);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 14;
    }

    public interface RegisterListListener {
        void OnAddAvatarClicked();
        void OnRegisterClicked(String username, String name, String password, String confirm,
                               String birthday, String classOf, String major, Uri avatar);
    }
}
