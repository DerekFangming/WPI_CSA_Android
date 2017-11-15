package com.fmning.wpi_csa.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.fragments.LifeFragment.OnFeedClickListener;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.List;

/**
 * Created by fangmingning on 11/3/17.
 */

public class LifeListAdapter extends RecyclerView.Adapter<LifeListAdapter.CustomViewHolder> {

    private List<WCFeed> feedList;
    private OnFeedClickListener listener;

    public LifeListAdapter( List<WCFeed> feedItemList,  OnFeedClickListener listener) {
        this.feedList = feedItemList;
        this.listener = listener;
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
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        if(position < feedList.size()) {
            View view = holder.itemView;
            TextView tv = (TextView) view.findViewById(R.id.feedTitle);
            tv.setText("wtf ahahahhahaha");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.OnFeedClick(position);
                    }
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return feedList.size() + 1;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        //WCFeed feed;

        public CustomViewHolder(View itemView) {
            super(itemView);

        }
    }
}
