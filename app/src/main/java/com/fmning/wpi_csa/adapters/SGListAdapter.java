package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.cache.CacheManager;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Paragraph;
import com.fmning.wpi_csa.objects.ParagraphType;
import com.fmning.wpi_csa.objects.SeparatorType;

/**
 * Created by fangmingning
 * On 12/11/17.
 */

public class SGListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private SGListListener listener;
    private Article article;

    public SGListAdapter(Context context, SGListListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        switch (article.paragraphs.get(position).type) {
            case DIV:
            case PLAIN:
                return 1;
            case IMAGE:
                return 2;
            case IMAGETEXT:
                return 3;
            default:
                return 4;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_text, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_image, parent, false);
                return new ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_image_text, parent, false);
                return new ViewHolder(view3);
            default:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_default, parent, false);
                return new ViewHolder(view4);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View cell = holder.itemView;
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logLong("clicked");
            }
        });

        Paragraph paragraph = article.paragraphs.get(position);

        switch (paragraph.type) {
            case DIV:
            case PLAIN:
                ((TextView)cell.findViewById(R.id.sgText)).setText(paragraph.content);
                if (paragraph.separatorType == SeparatorType.FULL) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgTextSeparator).getLayoutParams();
                    layoutParams.setMarginStart(0);
                } else if (paragraph.separatorType == SeparatorType.NONE) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgTextSeparator).getLayoutParams();
                    layoutParams.setMarginStart(Utils.paddingFullWidth);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgTextSeparator).getLayoutParams();
                    layoutParams.setMarginStart(Utils.padding15);
                }

                if (paragraph.type == ParagraphType.DIV) {
                    cell.setBackgroundColor(article.themeColor);
                } else {
                    cell.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case IMAGE:
                String imgName = paragraph.properties.get("src");
                if (imgName != null) {
                    CacheManager.getImage(context, "sg_" + imgName, new CacheManager.OnCacheGetImageListener() {
                        @Override
                        public void OnCacheGetImageDone(String error, Bitmap image) {
                            ((ImageView)cell.findViewById(R.id.sgImage)).setImageBitmap(image);
                        }
                    });
                } else {
                    //TODO: friendly error message?
                    Utils.logMsg("Cannot read image");
                }


                break;
            case IMAGETEXT:
                ((TextView)cell.findViewById(R.id.sgImgtxtText)).setText(paragraph.content);
                imgName = paragraph.properties.get("src");
                if (imgName != null) {
                    CacheManager.getImage(context, "sg_" + imgName, new CacheManager.OnCacheGetImageListener() {
                        @Override
                        public void OnCacheGetImageDone(String error, Bitmap image) {
                            ((ImageView)cell.findViewById(R.id.sgImgtxtImage)).setImageBitmap(image);
                        }
                    });
                } else {
                    //TODO: friendly error message?
                    Utils.logMsg("Cannot read image");
                }


                break;

        }

    }

    @Override
    public int getItemCount() {
        if (article == null) {
            return 0;
        } else {
            return article.paragraphs.size();
        }
    }

    public void setAndProcessArticle(Article article) {
        this.article = article;
        this.article.processContent(context);
    }

    public interface SGListListener {
        void OnPrevArticleClicked();
        void OnNextArticleClicked();
    }
}
