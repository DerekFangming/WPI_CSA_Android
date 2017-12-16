package com.fmning.wpi_csa.objects;

import android.graphics.Color;
import android.text.Html;

import com.fmning.wpi_csa.helpers.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fangming
 * On 12/14/2017.
 */

public class Article {
    public int menuId = 0;
    public String content;
    public List<Paragraph> paragraphs;

    public int prevMenuId;
    public String prevMenuText;
    public int nextMenuId;
    public String nextMenuText;

    public int themeColor = -1;

    public Article(String content){
        this.content = content;
        paragraphs = new ArrayList<>();
    }

    private void processContent() {
        List<String> matchs = new ArrayList<>();
        int count = 0;

        Matcher m = Pattern.compile("(<img.*?/>)|(<imgtxt.*?</imgtxt>)|(<txtimg.*?</txtimg>)|(<tab.*?</tab>)|(<div.*?</div>)")
                .matcher(content);
        while (m.find()) {
            count ++;
            matchs.add(m.group(0));
        }

        for (int i = 0; i < count; i ++) {
            String[] parts = content.split(matchs.get(0), 2);
            String first = parts[0];
            ParagraphType paraType = ParagraphType.PLAIN;
            if (first.length() > 0) {
                paraType = getParagraphType(first);//TODO: This can only be Plain.  Fix also on iOS
                paragraphs.add(new Paragraph(Html.fromHtml(first), paraType));
            }

            paraType = getParagraphType(matchs.get(i));
            switch (paraType) {
                case IMAGE:
                    paragraphs.add(new Paragraph(Html.fromHtml(""), ParagraphType.IMAGE, Utils.getHtmlAttributes(matchs.get(i))));
                    break;
                case IMAGETEXT:
                case TEXTIMAGE:
                    //TODO: This is so hacky. Fix with recuglar exp. Also in iOS
                    String imgStr = matchs.get(i).substring(0, matchs.get(i).length() - 9);
                    String[] imgTextParts = imgStr.split(">", 2);
                    paragraphs.add(new Paragraph(Html.fromHtml(imgTextParts[1]), paraType,
                            Utils.getHtmlAttributes(imgTextParts[0])));
                    break;
                case TABLE:
                    String[] listItems = matchs.get(i).replace("<tab>", "")
                            .replace("</tab>", "").split("<tbr>");
                    if (listItems.length > 0) {
                        //This is valid because of the title cell
                        paragraphs.get(paragraphs.size() - 1).separatorType = SeparatorType.FULL;
                        for (String s :listItems) {
                            Paragraph p = new Paragraph(Html.fromHtml(s), ParagraphType.PLAIN);
                            p.separatorType = SeparatorType.NORMAL;
                            paragraphs.add(p);
                        }
                        //TODO Should I just set the separator type of p to full?
                        paragraphs.get(paragraphs.size() - 1).separatorType = SeparatorType.FULL;
                    }
                    break;
                case DIV:
                    //TODO: This is so hacky. Fix with recuglar exp. Also in iOS
                    String divStr = matchs.get(i).substring(0, matchs.get(i).length() - 9);
                    String[] divParts = divStr.split(">", 2);
                    Paragraph paragraph = new Paragraph(Html.fromHtml(divParts[1]), paraType,
                            Utils.getHtmlAttributes(divParts[0]));
                    paragraphs.add(paragraph);
                    String colorStr = paragraph.properties.get("color");
                    if (colorStr != null && themeColor == -1) {
                        themeColor = Color.parseColor(colorStr);
                    }
                    break;
                default:
                    break;
            }
            content = parts[1];
        }

        if (!content.equals("")) {
            paragraphs.add(new Paragraph(Html.fromHtml(content)));
        }
    }

    private ParagraphType getParagraphType (String string) {
        if (!string.startsWith("<")) {
            return ParagraphType.PLAIN;
        } else if (string.startsWith("<imgtxt")) {
            return ParagraphType.IMAGETEXT;
        } else if (string.startsWith("<img")) {
            return ParagraphType.IMAGE;
        } else if (string.startsWith("<tab")) {
            return ParagraphType.TABLE;
        } else if (string.startsWith("<txtimg")) {
            return ParagraphType.TEXTIMAGE;
        } else if (string.startsWith("<div")) {
            return ParagraphType.DIV;
        } else {
            return ParagraphType.PLAIN;
        }
    }
}
