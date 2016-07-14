package com.example.loveextra.microblog.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LoveExtra on 2016/6/23.
 */
public class Game2048Item extends View{

    int mNumber;
    String mNumberVal;
    Paint mPaint;//画笔
    Rect mBound;//矩形
    public Game2048Item(Context context) {
        this(context,null);
    }

    public Game2048Item(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Game2048Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }
    public void setNumber(int number) {
        Typeface font=Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
        mNumber = number;
        mNumberVal = mNumber + "";
        mPaint.setTextSize(100.0f);
        mPaint.setTypeface(font);
        mBound = new Rect();
        mPaint.getTextBounds(mNumberVal, 0, mNumberVal.length(), mBound);
        invalidate();//刷新
    }

    public int getNumber() {
        return mNumber;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String mBgColor=" ";
        switch (mNumber){
            case 0:
                mBgColor = "#CCC0B3";
                break;
            case 2:
                mBgColor = "#EEE4DA";
                break;
            case 4:
                mBgColor = "#EDE0C8";
                break;
            case 8:
                mBgColor = "#F2B179";
                break;
            case 16:
                mBgColor = "#F49563";
                break;
            case 32:
                mBgColor = "#F5794D";
                break;
            case 64:
                mBgColor = "#F55D37";
                break;
            case 128:
                mBgColor = "#EEE863";
                break;
            case 256:
                mBgColor = "#EDB04D";
                break;
            case 512:
                mBgColor = "#ECB04D";
                break;
            case 1024:
                mBgColor = "#EB9437";
                break;
            case 2048:
                mBgColor = "#EA7821";
                break;
            default:
                mBgColor = "#EA7821";
                break;
        }
        mPaint.setColor(Color.parseColor(mBgColor));
        mPaint.setStyle(Paint.Style.FILL);//调用Paint类中的静态枚举类Style中的FILL 填充
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        if (mNumber != 0)
            drawText(canvas);
    }
    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        float x = (getWidth() - mBound.width()) / 2;
        float y = getHeight() / 2 + mBound.height() / 2;
        canvas.drawText(mNumberVal, x, y, mPaint);
    }


}
