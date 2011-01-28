package com.codexperiments.colossus.utility;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;
import android.widget.TextView.BufferType;


public class TextUtility
{
	public static void underlineText(TextView pTextView, String pText) {
        SpannableString lFormattedURL = new SpannableString(pText);
        lFormattedURL.setSpan(new UnderlineSpan(), 0, pText.length(), 0);
        pTextView.setText(lFormattedURL, BufferType.SPANNABLE);
	}
}
