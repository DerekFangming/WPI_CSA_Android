package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmning.wpi_csa.R;

/**
 * Created by Fangming
 * On 12/4/2017.
 */

public class RegisterListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;

    public RegisterListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 2 || position == 7 || position == 11) {
            return 1;
        } else if (position == 1 ) {
            return 2;
        } else {
            return 3;
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
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_register_input, parent, false);
                return new ViewHolder(view3);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 13;
    }
}
