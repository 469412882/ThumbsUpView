package com.hch.thumbsuplib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * 点赞数View
 *
 * Created by hch on 2017/11/21.
 */

public class PriseCountView extends View {
    /** 点赞数 */
    private int mCount;
    /** 之前的点赞数 */
    private int mOldCount;
    /** 点赞颜色 */
    private int mTextOnColor;
    /** 非点赞颜色 */
    private int mTextOffColor;
    /** 透明字体颜色 */
    private int mTransparentTextOnColor;
    /** 透明字体颜色 */
    private int mTransparentTextOffColor;
    /** 字体大小 */
    private float textSize;
    /** 右侧新值的baseline偏移量 */
    private int offYBaseline;
    /** 是否是点赞 */
    private boolean isPriseOn;
    /** 动画偏移量 */
    private float offsetYProgress;
    /** 动画执行进度 */
    private float mFraction = 1f;
    /** 画笔 */
    private Paint mPaint;
    /** 三个要画的数字 */
    private String[] mTextCount;
    /** 圆形颜色 */
    private long mDuration;

    public PriseCountView(Context context, int count, int onColor, int offColor, float textSize) {
        super(context);
        this.mCount = count;
        this.mTextOnColor = onColor;
        this.mTextOffColor = offColor;
        this.textSize = textSize;
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextOffColor);
        mPaint.setTextSize(textSize);
        mTextCount = new String[3];
        mTextCount[0] = String.valueOf(mCount);
        mTransparentTextOnColor = Color.argb(0, Color.red(mTextOnColor), Color.green(mTextOnColor),
                Color.blue(mTextOnColor));
        mTransparentTextOffColor = Color.argb(0, Color.red(mTextOffColor), Color.green(mTextOffColor),
                Color.blue(mTextOffColor));
        mDuration = ThumbsUpCountView.ANIM_DURATION;
    }

    public void setDuration(long duration){
        this.mDuration = duration;
    }

    public void setCount(int count, boolean isPriseOn) {
        mOldCount = mCount;
        this.mCount = count;
        this.isPriseOn = isPriseOn;
        if (analysisCount()) {
            startAnim();
        } else {
            requestLayout();
        }
    }

    /**
     * 执行动画
     */
    private void startAnim() {
        int startFloat = 0, endFloat = offYBaseline;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startFloat, endFloat);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = animation.getAnimatedFraction();
                offsetYProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.setDuration(mDuration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    /**
     * 计算固定的数字和需要动态改变的数字
     * 
     * @return 是否需要动态改变
     */
    private boolean analysisCount() {
        if (mCount == mOldCount) {
            return false;
        }
        /* 如果差值大于，认为是赋值语句，而不是因为点击而产生的变化 */
        if (Math.abs(mCount - mOldCount) != 1) {
            mTextCount[0] = String.valueOf(mCount);
            mTextCount[1] = "";
            mTextCount[2] = "";
            return false;
        }
        offYBaseline = mCount > mOldCount ? 1 : -1;
        String countStr = String.valueOf(mCount);
        String oldCountStr = String.valueOf(mOldCount);
        /* 位数发生变化了 */
        if (countStr.length() != oldCountStr.length()) {
            mTextCount[0] = "";
            mTextCount[1] = oldCountStr;
            mTextCount[2] = countStr;
            requestLayout();
            return true;
        }
        /* 从左往右比较，直到找到不同的位数 */
        for (int i = 0; i < countStr.toCharArray().length; i++) {
            if (countStr.toCharArray()[i] != oldCountStr.toCharArray()[i]) {
                mTextCount[0] = i == 0 ? "" : countStr.substring(0, i);
                mTextCount[1] = oldCountStr.substring(i);
                mTextCount[2] = countStr.substring(i);
                break;
            }
        }
        return true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String text = String.valueOf(mCount);
        mPaint.measureText(text);
        int width = (int) (getPaddingLeft() + getPaddingRight() + mPaint.measureText(text));
        int height = (int) (getPaddingTop() + getPaddingBottom() + mPaint.getTextSize() * 3);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = 0;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseLineY = getMeasuredHeight() / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2;

        mPaint.setColor(getCurrentColor(mFraction, isPriseOn ? mTextOffColor : mTextOnColor, isPriseOn ? mTextOnColor
                : mTextOffColor));
        if (!TextUtils.isEmpty(mTextCount[0])) {
            canvas.drawText(mTextCount[0], getPaddingLeft(), mTextCount[0].length(), startX, baseLineY, mPaint);
            startX = mPaint.measureText(mTextCount[0]);
        }

        float offset = mPaint.getTextSize() * offsetYProgress;

        mPaint.setColor(getCurrentColor(mFraction, isPriseOn ? mTextOffColor : mTextOnColor, mTransparentTextOffColor));
        if (!TextUtils.isEmpty(mTextCount[1])) {
            canvas.drawText(mTextCount[1], getPaddingLeft(), mTextCount[1].length(), startX, baseLineY - offset, mPaint);
        }

        mPaint.setColor(getCurrentColor(mFraction, isPriseOn ? mTransparentTextOffColor : mTransparentTextOnColor,
                isPriseOn ? mTextOnColor : mTextOffColor));
        if (!TextUtils.isEmpty(mTextCount[2])) {
            canvas.drawText(mTextCount[2], getPaddingLeft(), mTextCount[2].length(), startX,
                    baseLineY + mPaint.getTextSize() * offYBaseline - offset, mPaint);
        }

    }

    /**
     * 根据fraction值来计算当前的颜色。
     */
    private int getCurrentColor(float fraction, int startColor, int endColor) {
        int redCurrent;
        int blueCurrent;
        int greenCurrent;
        int alphaCurrent;

        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaStart = Color.alpha(startColor);

        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaEnd = Color.alpha(endColor);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        redCurrent = (int) (redStart + fraction * redDifference);
        blueCurrent = (int) (blueStart + fraction * blueDifference);
        greenCurrent = (int) (greenStart + fraction * greenDifference);
        alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }
}
