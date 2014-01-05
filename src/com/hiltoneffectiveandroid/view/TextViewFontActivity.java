package com.hiltoneffectiveandroid.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hilton.effectiveandroid.R;
import com.hilton.effectiveandroid.R.id;
import com.hilton.effectiveandroid.R.layout;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.widget.TextView;

public class TextViewFontActivity extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.textview_font_1);
        
        // Demonstration of basic SpannableString and spans usage
        final TextView textWithString = (TextView) findViewById(R.id.text_view_font_1);
        String w = "The quick fox jumps over the lazy dog";
        int start = w.indexOf('q');
        int end = w.indexOf('k') + 1;
        Spannable word = new SpannableString(w);
        word.setSpan(new AbsoluteSizeSpan(22), start, end, 
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word.setSpan(new StyleSpan(Typeface.BOLD), start, end, 
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word.setSpan(new BackgroundColorSpan(Color.RED), start, end, 
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textWithString.setText(word);
        
        // Demonstration of basic SpannableStringBuilder and spans usage
        final TextView textWithBuilder = (TextView) findViewById(R.id.text_view_font_2);
        SpannableStringBuilder word2 = new SpannableStringBuilder();
        final String one = "Freedom is nothing but a chance to be better!";
        final String two = "The quick fox jumps over the lazy dog!";
        final String three = "The tree of liberty must be refreshed from time to time with " +
                "the blood of patroits and tyrants!";
        word2.append(one);
        start = 0;
        end = one.length();
        word2.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        word2.append(two);
        start = end;
        end += two.length();
        word2.setSpan(new ForegroundColorSpan(Color.CYAN), start, end, 
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        word2.append(three);
        start = end;
        end += three.length();
        word2.setSpan(new URLSpan(three), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textWithBuilder.setText(word2);
        
        // Troubleshooting when using SpannableStringBuilder
        final TextView textTroubles = (TextView) findViewById(R.id.text_view_font_3);
        SpannableStringBuilder word3 = new SpannableStringBuilder();
        start = 0;
        end = one.length();
        // Caution: must first append or set text to SpannableStringBuilder or SpannableString
        // then set the spans to them, otherwise, IndexOutOfBoundException is thrown when setting spans
        word3.append(one);
        // For AbsoluteSizeSpan, the flag must be set to 0, otherwise, it will apply this span to until end of text
        word3.setSpan(new AbsoluteSizeSpan(22), start, end, 0);//Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // For BackgroundColorSpanSpan, the flag must be set to 0, otherwise, it will apply this span to end of text
        word3.setSpan(new BackgroundColorSpan(Color.DKGRAY), start, end, 0); //Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.append(two);
        start = end;
        end += two.length();
        word3.setSpan(new TypefaceSpan("sans-serif"), start, end, 
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // TODO: sometimes, flag must be set to 0, otherwise it will apply the span to until end of text
        // which MIGHT has nothing to do with specific span type.
        word3.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, 0);//Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.setSpan(new ScaleXSpan(0.618f), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.setSpan(new StrikethroughSpan(), start, end, 0);//Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.setSpan(new ForegroundColorSpan(Color.CYAN), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.setSpan(new QuoteSpan(), start, end, 0); //Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.append(three);
        start = end;
        end += three.length();
        word3.setSpan(new RelativeSizeSpan((float) Math.E), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word3.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textTroubles.setText(word3);
        
        // Highlight some patterns
        final String four = "The gap between the best software engineering " +
                "practice and the average practice is very wide��perhaps wider " +
                " than in any other engineering discipline. A tool that disseminates " +
                "good practice would be important.��Fred Brooks";
        final Pattern highlight = Pattern.compile("the");
        final TextView textHighlight = (TextView) findViewById(R.id.text_view_font_4);
        SpannableString word4 = new SpannableString(four);
        Matcher m = highlight.matcher(word4.toString());
        while (m.find()) {
            word4.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word4.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word4.setSpan(new StrikethroughSpan(), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        textHighlight.setText(word4);
        
        // Set numbers, URLs and E-mail address to be clickable with TextView#setAutoLinkMask
        final TextView textClickable = (TextView) findViewById(R.id.text_view_font_5);  
        final String contact = "Email: mvp@microsoft.com\n" +
                "Phone: +47-24885883\n" +
                "Fax: +47-24885883\n" +
                "HTTP: www.microsoft.com/mvp.asp";
        // Set the attribute first, then set the text. Otherwise, it won't work
        textClickable.setAutoLinkMask(Linkify.ALL); // or set 'android:autoLink' in layout xml
        textClickable.setText(contact);
    }
}