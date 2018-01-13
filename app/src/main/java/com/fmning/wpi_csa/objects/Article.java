package com.fmning.wpi_csa.objects;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.util.Linkify;

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

    public void processContent() {
        List<String> matchs = new ArrayList<>();
        int count = 0;

        Matcher m = Pattern.compile("(<img.*?/>)|(<imgtxt.*?</imgtxt>)|(<txtimg.*?</txtimg>)|(<tab.*?</tab>)|(<div.*?</div>)")
                .matcher(content);
        while (m.find()) {
            count ++;
            matchs.add(m.group(0));
        }

        for (int i = 0; i < count; i ++) {
            String splitter = matchs.get(i).replaceAll("\\(", "\\\\\\(").replaceAll("\\)", "\\\\\\)")
                    .replaceAll("\\[", "\\\\\\[").replaceAll("\\]", "\\\\\\]");
            String[] parts = content.split(splitter, 2);
            String first = parts[0];
            ParagraphType paraType;
            if (first.length() > 0) {
                //Currently, only Plain text supports alignment
                //The getAlignedSpanned function is android specific because it does not support alignment
                paragraphs.add(new Paragraph(getAlignedSpanned(first), ParagraphType.PLAIN));
            }

            paraType = getParagraphType(matchs.get(i));
            switch (paraType) {
                case IMAGE:
                    paragraphs.add(new Paragraph(Html.fromHtml(""), ParagraphType.IMAGE, Utils.getHtmlAttributes(matchs.get(i))));
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
                    String divStr = matchs.get(i).substring(0, matchs.get(i).length() - 6);
                    String[] divParts = divStr.split(">", 2);
                    Paragraph paragraph = new Paragraph(Html.fromHtml(divParts[1]), paraType,
                            Utils.getHtmlAttributes(divParts[0]));
                    paragraphs.add(paragraph);
                    String colorStr = paragraph.properties.get("color");
                    if (colorStr != null && themeColor == -1) {
                        themeColor = Color.parseColor("#" + colorStr);
                    }
                    break;
                default:
                    break;
            }
            if (parts.length == 2) {
                content = parts[1];
            } else {
                return;
            }
        }

        if (!content.equals("")) {
            //Currently, only Plain text supports alignment
            //The getAlignedSpanned function is android specific because it does not support alignment
            paragraphs.add(new Paragraph(getAlignedSpanned(content)));
        }
    }

    private Spanned getAlignedSpanned(String text) {
        Spannable spannable = new SpannableString(Html.fromHtml(text));
        String spannableStr = spannable.toString();

        Matcher alignMatcher = Pattern.compile("<p.*?align.*?>.*?</p>")
                .matcher(text);
        while (alignMatcher.find()) {
            String matchedStr = alignMatcher.group(0);
            String[] alignParts = matchedStr.split(">", 2);
            String align = Utils.getHtmlAttributes(alignParts[0]).get("align");
            String alignedString = alignParts[1].replace("</p>", "");
            String matchedHtmlStr = Html.fromHtml(alignedString).toString();
            if (align != null) {
                int start = spannableStr.indexOf(matchedHtmlStr);
                if (start == -1) {
                    continue;
                }

                int end = start + matchedHtmlStr.length();
                if (align.equals("center")) {
                    spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), start,
                            end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (align.equals("right")) {
                    spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start,
                            end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        Linkify.addLinks(spannable, Linkify.WEB_URLS);
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
