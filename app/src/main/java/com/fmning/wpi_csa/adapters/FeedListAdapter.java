package com.fmning.wpi_csa.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.objects.WCFeed;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private WCFeed feed;

    public FeedListAdapter (WCFeed feed) {
        this.feed = feed;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_title, parent, false);
                return new ViewHolder(view1);

        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View cell = holder.itemView;
        if (position == 0) {
            ((TextView) cell.findViewById(R.id.feedTitleText)).setText(feed.title);
            ((TextView) cell.findViewById(R.id.feedTypeText)).setText(" " + feed.type + " ");
            ((TextView) cell.findViewById(R.id.feedCreatedTimeText)).setText(Utils.dateToString(feed.createdAt));
            ((TextView) cell.findViewById(R.id.feedCreatorText)).setText(feed.ownerName);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public interface FeedListListener {
        //void
    }

}
