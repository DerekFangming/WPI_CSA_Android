package com.fmning.wpi_csa.objects;

import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;

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

    @SuppressWarnings("unused")
    public Paragraph(){
        content = Html.fromHtml("");
        type = ParagraphType.PLAIN;
    }

    Paragraph(Spanned content){
        this.content = content;
        type = ParagraphType.PLAIN;
    }

    Paragraph(Spanned content, ParagraphType type){
        this.content = content;
        this.type = type;
    }

    Paragraph(Spanned content, ParagraphType type, Map<String, String> properties){
        this.content = content;
        this.type = type;
        this.properties = properties;
    }

}
