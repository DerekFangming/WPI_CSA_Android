package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fmning.wpi_csa.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fangming
 * On 12/4/2017.
 */

public class RegisterListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private List<String> labelText;
    private List<String> placeHolderText;

    public RegisterListAdapter(Context context) {
        this.context = context;
        labelText = Arrays.asList(context.getString(R.string.register_label_username),
                context.getString(R.string.register_label_name), context.getString(R.string.register_label_password),
                context.getString(R.string.register_label_confirm), context.getString(R.string.register_label_birthday),
                context.getString(R.string.register_label_classof), context.getString(R.string.register_label_major));
        placeHolderText = Arrays.asList(context.getString(R.string.register_hint_username),
                context.getString(R.string.register_hint_name), context.getString(R.string.register_hint_password),
                context.getString(R.string.register_hint_confirm), context.getString(R.string.register_hint_birthday),
                context.getString(R.string.register_hint_classof), context.getString(R.string.register_hint_major));
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        View cell = holder.itemView;
        if (position == 0) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_avatar));
        } else if (position == 2) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_required));
        } else if (position == 7) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText(context.getString(R.string.register_header_optional));
        } else if (position == 11) {
            ((TextView) cell.findViewById(R.id.separatorHeaderText)).setText("");
        } else if (position == 3 || position == 4 || position == 5 || position == 6) {
            int offset = 3;
            ((TextView) cell.findViewById(R.id.registerTitleField)).setText(labelText.get(position - offset));
            ((EditText) cell.findViewById(R.id.registerValueField)).setHint(placeHolderText.get(position - offset));
        } else if (position == 8 || position == 9 || position == 10) {
            int offset = 8 - 4;
            ((TextView) cell.findViewById(R.id.registerTitleField)).setText(labelText.get(position - offset));
            ((EditText) cell.findViewById(R.id.registerValueField)).setHint(placeHolderText.get(position - offset));
        } else if (position == 12) {
            ((TextView) cell.findViewById(R.id.settingButtonText)).setText(context.getString(R.string.register_activity));
        }
    }

    @Override
    public int getItemCount() {
        return 14;
    }
}
