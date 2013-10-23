package com.find.guide.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SectionIndexer;

import com.find.guide.R;

public class SlideView extends View {

    String[] DEFAULT = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", "#" };
    String[] b = DEFAULT;

    int choose = -1;

    Paint paint = new Paint();

    // private Rect bigRect=new Rect();
    boolean showBkg = false;

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    public SlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideView(Context context) {
        super(context);
    }

    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        if (sectionIndexer != null) {
            b = (String[]) sectionIndexer.getSections();
            if (b == null)
                b = DEFAULT;
        }
    }

    private int dip2px(int value) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        int pxValue = (int) (value * scale);// + 0.5f
        return pxValue;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            if (showBkg) {

                setBackgroundResource(R.drawable.slideview_bg);

                // paint.setColor(Color.parseColor("#FFFFFFFF"));
                paint.setColor(R.color.sliderview_character);
            } else {
                paint.setColor(R.color.sliderview_character);
            }

            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(dip2px(12));

            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight / 2 - paint.measureText(b[i]) / 2;
            float yH = singleHeight / 2 - paint.measureText(b[i]) / 2;

            yPos = singleHeight * (i + 1) - yH;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        int action = event.getAction();
        float height = event.getY();
        // Log.i("rs", "y==" + height);
        final int oldChoose = choose;
        // int choose=(int)(height/(getHeight()*b.length));
        int c = (int) (height / getHeight() * b.length);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (oldChoose != c && listener != null) {
                if (c >= 0 && c <= b.length) {
                    showBkg = true;
                    if (c == b.length) {
                        c = b.length - 1;
                    }

                    onTouchingLetterChangedListener.onTouchingLetterChanged(b[c]);
                    choose = c;
                    postInvalidate();
                }
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (listener != null && oldChoose != c) {
                if (c >= 0 && c <= b.length) {
                    showBkg = true;
                    if (c == b.length) {
                        c = b.length - 1;
                    }
                    onTouchingLetterChangedListener.onTouchingLetterChanged(b[c]);
                    choose = c;
                    postInvalidate();
                }
            }
            break;
        case MotionEvent.ACTION_UP:

            showBkg = false;
            choose = -1;
            setBackgroundDrawable(null);
            postInvalidate();
            break;
        }

        return true;
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {

        public void onTouchingLetterChanged(String s);
    }
}
