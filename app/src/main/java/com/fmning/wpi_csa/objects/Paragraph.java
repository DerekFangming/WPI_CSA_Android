package com.fmning.wpi_csa.objects;

import android.text.Html;
import android.text.Spanned;

import java.util.Map;

/**
 * Created by Fangming
 * On 12/14/2017.
 */

public class Paragraph {
    public Spanned content;
    public ParagraphType type;
    public SeparatorType separatorType = SeparatorType.NONE;
    public Map<String, String> properties;

    public double cellHeight = 0.0;
    public double textViewY = 10.0;
    public double textViewHeight = 130.0;
    public double imgViewY = 10.0;
    public double imgViewHeight = 130.0;

    public Paragraph(){
        content = Html.fromHtml("");
        type = ParagraphType.PLAIN;
    }

    public Paragraph(Spanned content){
        this.content = content;
    }

    public Paragraph(Spanned content, ParagraphType type){
        this.content = content;
        this.type = type;
    }

    public Paragraph(Spanned content, ParagraphType type, Map<String, String> properties){
        this.content = content;
        this.type = type;
        this.properties = properties;
    }

}
