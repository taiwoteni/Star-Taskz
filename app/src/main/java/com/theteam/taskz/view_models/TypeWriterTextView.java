package com.theteam.taskz.view_models;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TypeWriterTextView extends AppCompatTextView {
    private CharSequence text;
    private int index;
    private long delay = 80;
    private Handler handler = new Handler();

    public TypeWriterTextView(Context context) {
        super(context);
    }

    public TypeWriterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private Runnable character_adder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));

            if(index <= text.length()){
                handler.postDelayed(character_adder, delay);
            }
        }
    };


    public void animateText(String txt){
        text = txt;
        index =0;
        setText("");
        handler.removeCallbacks(character_adder);
        handler.postDelayed(character_adder, delay);
    }
    public void setStartDelay(long m){
        delay = m;
    }
    public void setTypingInterval(long m){
        delay = m;
    }
}
