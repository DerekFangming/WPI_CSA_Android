package com.fmning.wpi_csa.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.AppMode;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.WCService;
import com.fmning.wpi_csa.http.objects.WCUser;

/**
 * Created by fangmingning
 * On 11/27/17.
 */

public class SettingListAdapter extends RecyclerView.Adapter<ViewHolder> {


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            if (Utils.appMode == AppMode.OFFLINE) {
                return 1;
            } else if (Utils.appMode == AppMode.LOGIN) {
                return 2;
            } else {
                return 3;
            }
        } else if (position < 4) {
            return 4;// link cell
        } else {
            return 5;// button cell
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 2:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting_login, parent, false);
                return new ViewHolder(view1);
            default:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_default, parent, false);
                return new ViewHolder(view2);

        }
        //return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0)
            return;

        final View cell = holder.itemView;

        ((TextView) cell.findViewById(R.id.id)).setText("test");
        ((TextView) cell.findViewById(R.id.content)).setText("test123");
    }

    @Override
    public int getItemCount() {
        if (Utils.appMode == AppMode.LOGGED_ON) {
            WCUser user = WCService.currentUser;
            if (user != null && !user.emailConfirmed) {
                return 7;// 1 user cell, 3 link cells, 2 password and email cells, 1 logout cell
            } else {
                return 6;// 1 user cell, 3 link cells, 1 password cell, 1 logout cell
            }
        } else {
            return 4;// 1 user cell and 3 link cells
        }
    }
}
