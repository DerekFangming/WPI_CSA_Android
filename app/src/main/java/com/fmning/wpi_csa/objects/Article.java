package com.fmning.wpi_csa.objects;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;

import com.fmning.wpi_csa.helpers.Utils;
import com.pixplicity.htmlcompat.HtmlCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public void processContent(Context context) {
        List<String> matchs = new ArrayList<>();
        int count = 0;

        Matcher m = Pattern.compile("(<img.*?/>)|(<imgtxt.*?</imgtxt>)|(<txtimg.*?</txtimg>)|(<tab.*?</tab>)|(<div.*?</div>)")
                .matcher(content);
        while (m.find()) {
            count ++;
            matchs.add(m.group(0));
        }

        for (int i = 0; i < count; i ++) {
            String[] parts = content.split(matchs.get(i), 2);
            String first = parts[0];
            ParagraphType paraType;
            if (first.length() > 0) {
                Spannable spannable = new SpannableString(HtmlCompat.fromHtml(context, first, 0));
                String s = spannable.toString();

                Matcher alignMatcher = Pattern.compile("<p.*?align.*?>.*?</p>")
                        .matcher(first);
                while (alignMatcher.find()) {
                    String[] alignParts = m.group(0).split(">", 2);
                    String align = Utils.getHtmlAttributes(alignParts[0]).get("align");
                    String alignedString = alignParts[1].replace("</p>", "");
                    if (align != null) {
                        int start = s.indexOf(alignedString);
                        if (align.equals("center")) {
                            spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start,
                                    alignedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (align.equals("right")) {
                            spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start,
                                    alignedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }

                paragraphs.add(new Paragraph(spannable, ParagraphType.PLAIN));
            }

            paraType = getParagraphType(matchs.get(i));
            switch (paraType) {
                case IMAGE:
                    paragraphs.add(new Paragraph(HtmlCompat.fromHtml(context, "", 0), ParagraphType.IMAGE, Utils.getHtmlAttributes(matchs.get(i))));
                    break;
                case IMAGETEXT:
                case TEXTIMAGE:
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
                    String divStr = matchs.get(i).substring(0, matchs.get(i).length() - 9);
                    String[] divParts = divStr.split(">", 2);
                    Paragraph paragraph = new Paragraph(Html.fromHtml(divParts[1]), paraType,
                            Utils.getHtmlAttributes(divParts[0]));
                    paragraphs.add(paragraph);
                    String colorStr = paragraph.properties.get("color");
                    if (colorStr != null && themeColor == -1) {
                        Utils.logMsg(colorStr);
                        themeColor = Color.parseColor("#" + colorStr);
                    }
                    break;
                default:
                    break;
            }
            content = parts[1];
        }

        if (!content.equals("")) {

            paragraphs.add(new Paragraph(getAlignedSpanned(context, content)));
        }
    }

    private Spanned getAlignedSpanned(Context context, String text) {
        Spannable spannable = new SpannableString(HtmlCompat.fromHtml(context, text, 0));
        String s = spannable.toString();

        Matcher alignMatcher = Pattern.compile("<p.*?align.*?>.*?</p>")
                .matcher(text);
        while (alignMatcher.find()) {
            String[] alignParts = alignMatcher.group(0).split(">", 2);
            String align = Utils.getHtmlAttributes(alignParts[0]).get("align");
            String alignedString = alignParts[1].replace("</p>", "");
            if (align != null) {
                int start = s.indexOf(alignedString);

                if (align.equals("center")) {
                    spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start,
                            alignedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (align.equals("right")) {
                    //&emsp;&emsp;Hello 亲爱的学弟学妹们！<br><br><p align="right">Cyan 谢珊珊 2018 ECE </p>
                    char aaa2 = s.charAt(start - 1);
                    char aaa = s.charAt(start);
                    char aaa1 = s.charAt(start + 1);
                    spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start,
                            alignedString.length() - 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        return spannable;
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