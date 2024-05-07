package com.example.taskmanagerfeatures;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

public class CircularProgressBar extends View {
    private int progress;
    private Paint paint, textPaint;
    private RectF rectF;
    private String text, nProgress;
    private boolean isPaused = false;
    private boolean isAnimating = false;
    private Handler handler;
    private Runnable runnable;

    public CircularProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        paint = new Paint();
        paint.setColor(Color.GRAY); // Set color as needed
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20); // Set stroke width as needed
        paint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.GRAY); // Set text color as needed
        textPaint.setTextSize(100); // Set text size as needed
        textPaint.setTextAlign(Paint.Align.CENTER); // Center the text
        textPaint.setAntiAlias(true);

        rectF = new RectF();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate(); // Trigger onDraw to redraw the view
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // Trigger onDraw to redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 10;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float angle = 360 * (progress / 100f);
        canvas.drawArc(rectF, -90, -angle, false, paint);

        canvas.drawText(text, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
    }
}
