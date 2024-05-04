package com.theteam.taskz.view_models;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class UnderlineTextView extends AppCompatTextView {
    public UnderlineTextView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public UnderlineTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attr){
        final AppCompatTextView textView = this;
        final String text = getText().toString();
        final SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(spannableString);
    }
}
