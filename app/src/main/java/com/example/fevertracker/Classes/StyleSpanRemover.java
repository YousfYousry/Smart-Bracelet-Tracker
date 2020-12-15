package com.example.fevertracker.Classes;

import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

public class StyleSpanRemover {

    public void RemoveOne(Spannable spannable,
                          int startSelection, int endSelection, Class<?> style){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeOneSpan(spannable, startSelection, endSelection, style);
        restoreSpans(spannable, spansParts);
    }

    public void RemoveStyle(Spannable spannable,
                            int startSelection, int endSelection, int styleToRemove){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeStyleSpan(spannable, startSelection, endSelection, styleToRemove);
        restoreSpans(spannable, spansParts);
    }

    public void RemoveAll(Spannable spannable, int startSelection, int endSelection){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeAllSpans(spannable, startSelection, endSelection);
        restoreSpans(spannable, spansParts);
    }

    protected void restoreSpans(Spannable spannable, ArrayList<SpanParts> spansParts){

        for (SpanParts spanParts : spansParts) {
            if(spanParts.part1.canAplly())
                spannable.setSpan(spanParts.part1.span, spanParts.part1.start,
                        spanParts.part1.end,spanParts.span_flag);
            if(spanParts.part2.canAplly())
                spannable.setSpan(spanParts.part2.span, spanParts.part2.start,
                        spanParts.part2.end, spanParts.span_flag);
        }
    }

    protected void removeAllSpans(Spannable spannable,int startSelection, int endSelection) {

        Object spansToRemove[] = spannable.getSpans(startSelection, endSelection, Object.class);
        for(Object span: spansToRemove){
            if(span instanceof CharacterStyle)
                spannable.removeSpan(span);
        }
    }

    protected void removeOneSpan(Spannable spannable,int startSelection, int endSelection,
                                 Class<?> style) {

        CharacterStyle spansToRemove[] = spannable.getSpans(startSelection, endSelection, CharacterStyle.class);
        for(CharacterStyle span: spansToRemove){
            if(span.getUnderlying().getClass() == style )
                spannable.removeSpan(span);
        }
    }

    protected void removeStyleSpan(Spannable spannable, int startSelection,
                                   int endSelection, int styleToRemove) {

        MetricAffectingSpan spans[] = spannable.getSpans(startSelection, endSelection, MetricAffectingSpan.class);
        for(MetricAffectingSpan span: spans){
            int stylesApplied = 0;
            int stylesToApply;
            int spanStart;
            int spanEnd;
            int spanFlag;
            Object spanUnd = span.getUnderlying();
            if(spanUnd instanceof StyleSpan){
                spanFlag = spannable.getSpanFlags(spanUnd);
                stylesApplied = ((StyleSpan) spanUnd).getStyle();
                stylesToApply = stylesApplied & ~styleToRemove;

                spanStart = spannable.getSpanStart(span);
                spanEnd = spannable.getSpanEnd(span);
                if(spanEnd >= 0 && spanStart >= 0){
                    spannable.removeSpan(span);
                    spannable.setSpan(new StyleSpan(stylesToApply), spanStart, spanEnd,spanFlag);
                }
            }
        }
    }

    protected ArrayList<SpanParts> getSpanParts(Spannable spannable,
                                                int startSelection,int endSelection){

        ArrayList<SpanParts> spansParts = new ArrayList<SpanParts>();
        Object spans[] = spannable.getSpans(startSelection, endSelection, Object.class);
        for(Object span: spans){
            if(span instanceof CharacterStyle){
                SpanParts spanParts = new SpanParts();
                int spanStart = spannable.getSpanStart(span);
                int spanEnd = spannable.getSpanEnd(span);
                if(spanStart == startSelection && spanEnd == endSelection) continue;
                spanParts.span_flag = spannable.getSpanFlags(span);
                spanParts.part1.span = CharacterStyle.wrap((CharacterStyle) span);
                spanParts.part1.start = spanStart;
                spanParts.part1.end = startSelection;
                spanParts.part2.span = CharacterStyle.wrap((CharacterStyle) span);
                spanParts.part2.start = endSelection;
                spanParts.part2.end = spanEnd;
                spansParts.add(spanParts);
            }
        }
        return spansParts;
    }

    private class SpanParts{
        int span_flag;
        Part part1;
        Part part2;
        SpanParts() {
            part1 = new Part();
            part2 = new Part();
        }
    }
    private class Part{
        CharacterStyle span;
        int start;
        int end;
        boolean canAplly() {
            return start < end;
        }
    }
}