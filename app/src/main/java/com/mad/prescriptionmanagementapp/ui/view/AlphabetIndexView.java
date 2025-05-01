package com.mad.prescriptionmanagementapp.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AlphabetIndexView extends View {

    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "-"};
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int highlightedIndex = -1;
    private OnLetterClickListener listener;

    public interface OnLetterClickListener {
        void onLetterClick(String letter);
    }

    public AlphabetIndexView(Context context) {
        super(context);
        init();
    }

    public AlphabetIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlphabetIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float height = getHeight();
        float letterHeight = height / letters.length;
        float centerX = getWidth() / 2f;

        for (int i = 0; i < letters.length; i++) {
            float y = letterHeight * (i + 1);

            if (i == highlightedIndex) {
                paint.setColor(Color.parseColor("#000000"));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                paint.setColor(Color.parseColor("#888888"));
                paint.setTypeface(Typeface.DEFAULT);
            }

            canvas.drawText(letters[i], centerX, y, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                int oldHighlightedIndex = highlightedIndex;
                highlightedIndex = (int) (y / getHeight() * letters.length);
                highlightedIndex = Math.max(0, Math.min(letters.length - 1, highlightedIndex));

                if (oldHighlightedIndex != highlightedIndex) {
                    if (listener != null) {
                        listener.onLetterClick(letters[highlightedIndex]);
                    }
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                highlightedIndex = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnLetterClickListener(OnLetterClickListener listener) {
        this.listener = listener;
    }
}
