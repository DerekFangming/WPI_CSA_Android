package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
//                Spanned s = Html.fromHtml("<div color=\"468499\"><br><br><br><h1><big><big><font color=\"#FFFFFF\">写在前面的话</font></big></big></h1></div>&emsp;&emsp;Hello 亲爱的学弟学妹们！ <br>&emsp;&emsp;再次感谢国家。顺便感谢党。再感谢一下cctv。 <br>&emsp;&emsp;Alright，我反正写不出感天动地的前言。那就，欢迎大家来到WPI 吧！ <br>&emsp;&emsp;我在这里快第四年了，感觉大学时光真的很美好，还记得上大学前的时候期待着开学的心情，刚来到学校英语磕磕碰碰但是却特别兴奋和期待，也记得被作业压的喘不过气但是做完后还是感觉这个世界很美好的。这里有很多热心帮助我们的学长学姐。就是因为他们，这三年，我觉得生活特别温暖和美好。 你们的大学时光才刚刚开始，希望大家都有一个美好的四年大学时光，收获让自己又爱又恨的好朋友 and 学业爱情双丰收。 <br>&emsp;&emsp;欢迎大家多参与我们CSA的活动！有什么问题都可以在微信群上问我们，或者发邮件给csa@wpi.edu！会有很多热心的学长！和学姐帮你们解 答各种人生路上的迷茫和…….迷茫 。么么哒！ <br>&emsp;&emsp;掌声鼓励日夜为sg 更新换代而奋斗的同学们！期待学弟学妹们的到来！ <br><br><p align=\"right\">Cyan 谢珊珊 2018 ECE </p>");
//                int a = s.length();
//                char b = s.charAt(0);
//                Spannable styledResultText = new SpannableString(s);
//                styledResultText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Spanned bb = styledResultText;
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
        this.article.processContent(context);
    }

    public interface SGListListener {
        void OnPrevArticleClicked();
        void OnNextArticleClicked();
    }
}
