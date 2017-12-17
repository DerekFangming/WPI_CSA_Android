package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Paragraph;

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
            default:
                return 2;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_text, parent, false);
                return new ViewHolder(view1);
            default:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_default, parent, false);
                return new ViewHolder(view2);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View cell = holder.itemView;
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
        this.article.processContent();
    }

    public interface SGListListener {
        void OnPrevArticleClicked();
        void OnNextArticleClicked();
    }
}
