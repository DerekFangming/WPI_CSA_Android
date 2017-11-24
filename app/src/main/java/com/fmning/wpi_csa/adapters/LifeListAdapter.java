package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.http.objects.WCFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangmingning
 * On 11/3/17.
 */

public class LifeListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<WCFeed> feedList;
    private boolean stopLoadingFlag;
    private Context context;
    private FeedListListener listener;

    public LifeListAdapter(Context context, FeedListListener listener) {
        this.feedList = new ArrayList<>();
        this.stopLoadingFlag = false;
        this.context = context;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed, parent, false);
                return new ViewHolder(view1);

            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_loading, parent, false);
                return new ViewHolder(view2);


        }
        return null;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position < feedList.size()) {
            final View cell = holder.itemView;
            WCFeed feed = feedList.get(position);

            ((TextView) cell.findViewById(R.id.feedCellTitle)).setText(feed.title);
            ((TextView) cell.findViewById(R.id.feedCellType)).setText(feed.type);
            ((TextView) cell.findViewById(R.id.feedCellOwnerName)).setText(feed.ownerName);
            ((TextView) cell.findViewById(R.id.feedCellCreatedAt)).setText(Utils.dateToString(feed.createdAt));

            if (feed.avatarId != -1) {
                CacheManager.getImage(context, Utils.convertToWCImageId(feed.avatarId), new CacheManager.OnCacheGetImageDoneListener() {
                    @Override
                    public void OnCacheGetImageDone(String error, Bitmap image) {
                        if (error.equals("")) {
                            ((ImageView) cell.findViewById(R.id.feedCellAvatar)).setImageBitmap(image);
                        } else {
                            ((ImageView) cell.findViewById(R.id.feedCellAvatar))
                                    .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
                        }
                    }
                });
            } else {
                ((ImageView) cell.findViewById(R.id.feedCellAvatar))
                        .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
            }

            ((ImageView) cell.findViewById(R.id.feedCellCoverImage))
                    .setImageBitmap(Utils.createImage(context.getResources().getColor(R.color.white)));
            if (feed.coverImgId != -1) {
                CacheManager.getImage(context, Utils.convertToWCImageId(feed.coverImgId), new CacheManager.OnCacheGetImageDoneListener() {
                    @Override
                    public void OnCacheGetImageDone(String error, Bitmap image) {
                        if (error.equals("")) {
                            ((ImageView) cell.findViewById(R.id.feedCellCoverImage)).setImageBitmap(image);
                        }
                    }
                });
            }

            if (listener != null) {
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnFeedClick(holder.getLayoutPosition());
                    }
                });

                if (position == feedList.size() - 1) {
                    listener.scrolledToLastFeed();
                }
            }





        } else {
            TextView coverLabel = (TextView) holder.itemView.findViewById(R.id.coverLabel);
            if (stopLoadingFlag) {
                coverLabel.setVisibility(View.VISIBLE);
            } else {
                coverLabel.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return feedList.size() + 1;
    }

    public void setFeedList(List<WCFeed> feedList, boolean stopLoadingFlag){
        this.feedList = feedList;
        this.stopLoadingFlag = stopLoadingFlag;
    }

    public void appendFeedList(List<WCFeed> feedList, boolean stopLoadingFlag){
        this.feedList.addAll(feedList);
        this.stopLoadingFlag = stopLoadingFlag;
    }

    public interface FeedListListener {
        void OnFeedClick(int index);
        void scrolledToLastFeed();
    }
}
