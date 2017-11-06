package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangmingning on 11/3/17.
 */

public class LifeListAdapter extends RecyclerView.Adapter<LifeListAdapter.CustomViewHolder> {

    private List<WCFeed> feedList;
    public LifeListAdapter( ArrayList<WCFeed> feedItemList) {
        this.feedList = feedItemList;

    }

    @Override
    public int getItemViewType(int position) {

        if (position == feedList.size()) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View viewONE = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed, parent, false);
                CustomViewHolder rowONE = new CustomViewHolder(viewONE);
                return rowONE;

            case 2:
                View viewTWO = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_loading, parent, false);
                CustomViewHolder rowTWO = new CustomViewHolder(viewTWO);
                return rowTWO;


        }
        return null;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(position < feedList.size()) {
            View view = holder.itemView;
            TextView tv = (TextView) view.findViewById(R.id.feedTitle);
            tv.setText("wtf ahahahhahaha");
        }
    }


    @Override
    public int getItemCount() {
        return feedList.size() + 1;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView imgl;
        TextView tvspecies;
        FrameLayout container;

        public CustomViewHolder(View itemView) {
            super(itemView);

        }
    }
}
