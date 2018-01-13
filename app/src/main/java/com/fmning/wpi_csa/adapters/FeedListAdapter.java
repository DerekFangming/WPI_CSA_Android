package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.webService.objects.WCEvent;
import com.fmning.wpi_csa.webService.objects.WCFeed;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Paragraph;
import com.fmning.wpi_csa.objects.ParagraphType;

import java.util.Locale;

/**
 * Created by Fangming
 * On 12/30/2017.
 */

public class FeedListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private WCFeed feed;
    private Article article;
    private FeedListListener listener;

    public FeedListAdapter (Context context, WCFeed feed) {
        this.context = context;
        this.feed = feed;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            if (article != null) {
                if (position <= article.paragraphs.size()) {
                    if (article.paragraphs.get(position - 1).type == ParagraphType.PLAIN) {
                        return 2;
                    } else if (article.paragraphs.get(position - 1).type == ParagraphType.IMAGE) {
                        return 3;
                    } else {
                        return 4;
                    }
                } else if (feed.event != null) {
                    if (position == article.paragraphs.size() + 1) {
                        return 5;//event cell
                    } else {
                        return 6;// ticket button cell
                    }
                } else {
                    return 4;
                }
            } else {
                if (position == 2) {
                    return 5;//event cell
                } else {
                    return 6;// ticket button cell
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_title, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_text, parent, false);
                return new ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_image, parent, false);
                return new ViewHolder(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_separator, parent, false);
                return new ViewHolder(view4);
            case 5:
                View view5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_event, parent, false);
                return new ViewHolder(view5);
            case 6:
                View view6 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_feed_button, parent, false);
                return new ViewHolder(view6);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View cell = holder.itemView;
        if (position == 0) {
            ((TextView) cell.findViewById(R.id.feedTitleText)).setText(feed.title);
            String feedType = String.format(context.getString(R.string.feed_type_padding), feed.type);
            ((TextView) cell.findViewById(R.id.feedTypeText)).setText(feedType);
            Utils.logMsg("title is " + feed.title);
            ((TextView) cell.findViewById(R.id.feedCreatedTimeText)).setText(Utils.dateToString(feed.createdAt));
            Utils.logMsg("Name is " + feed.ownerName);
            ((TextView) cell.findViewById(R.id.feedCreatorText)).setText(feed.ownerName);
        } else {
            if (article != null) {
                if (position <= article.paragraphs.size()) {
                    Paragraph paragraph = article.paragraphs.get(position - 1);
                    if (paragraph.type == ParagraphType.PLAIN) {
                        ((TextView)cell.findViewById(R.id.sgText)).setText(paragraph.content);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgTextSeparator).getLayoutParams();
                        layoutParams.setMarginStart(Utils.paddingFullWidth);
                    } else if (paragraph.type == ParagraphType.IMAGE) {
                        String imgName = paragraph.properties.get("src");
                        if (imgName != null) {
                            CacheManager.getImage(context, imgName, new CacheManager.OnCacheGetImageListener() {
                                @Override
                                public void OnCacheGetImageDone(String error, Bitmap image) {
                                    ((ImageView)cell.findViewById(R.id.sgImage)).setImageBitmap(image);
                                }
                            });
                        }
                    }
                } else if (feed.event != null) {
                    if (position == article.paragraphs.size() + 1) {
                        ((TextView) cell.findViewById(R.id.eventTitleText)).setText(feed.event.title);
                        String time = String.format(context.getString(R.string.feed_time), Utils.dateToString(feed.event.startTime),
                                Utils.dateToString(feed.event.endTime));
                        ((TextView) cell.findViewById(R.id.eventTimeText)).setText(time);
                        String feedLocation = String.format(context.getString(R.string.feed_location), feed.event.location);
                        ((TextView) cell.findViewById(R.id.eventLocationText)).setText(feedLocation);
                        cell.findViewById(R.id.eventAddButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listener != null) {
                                    listener.addToCalendar();
                                }
                            }
                        });
                    } else {
                        Button button = (Button) cell.findViewById(R.id.feedTicketButton);
                        if (feed.event.fee == 0) {
                            button.setText(context.getString(R.string.feed_free_ticket));
                        } else {
                            button.setText(String.format(context.getString(R.string.feed_paid_ticket),
                                    String.format(Locale.getDefault(), "%.2f", feed.event.fee)));
                        }
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listener!= null) {
                                    listener.payAndGetTicket();
                                }
                            }
                        });
                    }
                }
            } else {
                if (position == 2) {
                    ((TextView) cell.findViewById(R.id.eventTitleText)).setText(feed.event.title);
                    String time = String.format(context.getString(R.string.feed_time), Utils.dateToString(feed.event.startTime),
                            Utils.dateToString(feed.event.endTime));
                    ((TextView) cell.findViewById(R.id.eventTimeText)).setText(time);
                    String feedLocation = String.format(context.getString(R.string.feed_location), feed.event.location);
                    ((TextView) cell.findViewById(R.id.eventLocationText)).setText(feedLocation);
                    cell.findViewById(R.id.eventAddButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.addToCalendar();
                            }
                        }
                    });
                } else {
                    Button button = (Button) cell.findViewById(R.id.feedTicketButton);
                    if (feed.event.fee == 0) {
                        button.setText(context.getString(R.string.feed_free_ticket));
                    } else {
                        button.setText(String.format(context.getString(R.string.feed_paid_ticket),
                                String.format(Locale.getDefault(), "%.2f", feed.event.fee)));
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener!= null) {
                                listener.payAndGetTicket();
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;//title
        if (article != null) {
            count += article.paragraphs.size();
        }
        if (feed.event != null) {
            if (feed.event.fee == -1) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }

    public void setOnFeedListener(FeedListListener listener) {
        this.listener = listener;
    }

    public void setAndProcessFeed (WCFeed feed) {
        this.feed = feed;
        article = new Article(feed.body);
        article.processContent();
    }

    public interface FeedListListener {
        void addToCalendar();
        void payAndGetTicket();
    }

}
