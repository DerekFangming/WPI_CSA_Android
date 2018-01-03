package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.fmning.wpi_csa.cache.Database;
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

    public SGListAdapter(Context context) {
        this.context = context;
    }

    public void setListener(SGListListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (article.paragraphs.size() == position) {
            return 5;
        }
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
            case 5:
                View view5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_navigation, parent, false);
                return new ViewHolder(view5);
            default:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_default, parent, false);
                return new ViewHolder(view4);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final View cell = holder.itemView;

        if (article.paragraphs.size() == position) {
            Button prevBtn = (Button)cell.findViewById(R.id.sgPrevButton);
            Button nextBtn = (Button)cell.findViewById(R.id.sgNextButton);
            if (article.themeColor != -1) {
                ((GradientDrawable)prevBtn.getBackground()).setColor(article.themeColor);
                ((GradientDrawable)nextBtn.getBackground()).setColor(article.themeColor);
            }
            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Database db = new Database(context);
                    db.open();
                    article = db.getArticle(article.prevMenuId);
                    db.close();

                    article.processContent();
                    notifyDataSetChanged();

                    if (listener != null) {
                        listener.OnPrevArticleShown(article.themeColor, article.menuId);
                    }
                }
            });
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Database db = new Database(context);
                    db.open();
                    article = db.getArticle(article.nextMenuId);
                    db.close();

                    article.processContent();
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.OnNextArticleShown(article.themeColor, article.menuId);
                    }
                }
            });

            if (article.prevMenuText != null) {
                prevBtn.setText(String.format(context.getString(R.string.sg_nav_prev), article.prevMenuText));
                prevBtn.setEnabled(true);
                prevBtn.setAlpha(1);
            } else {
                prevBtn.setText(context.getString(R.string.sg_nav_prev_end));
                prevBtn.setEnabled(false);
                prevBtn.setAlpha(0.5f);
            }
            if (article.nextMenuText != null) {
                nextBtn.setText(String.format(context.getString(R.string.sg_nav_next), article.nextMenuText));
                nextBtn.setEnabled(true);
                nextBtn.setAlpha(1);
            } else {
                nextBtn.setText(context.getString(R.string.sg_nav_next_end));
                nextBtn.setEnabled(false);
                nextBtn.setAlpha(0.5f);
            }
            return;
        }

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
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgDivPlaceholder).getLayoutParams();
                    layoutParams.setMargins(0, Utils.padding50, 0, 0);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cell.findViewById(R.id.sgDivPlaceholder).getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
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
            return article.paragraphs.size() + 1;
        }
    }

    public void setAndProcessArticle(Article article) {
        this.article = article;
        this.article.processContent();
    }

    public interface SGListListener {
        void OnPrevArticleShown(int color, int menuId);
        void OnNextArticleShown(int color, int menuId);
    }
}
