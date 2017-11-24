package com.hch.thumbsuplib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

/**
 * 拇指view
 *
 * Created by hch on 2017/11/21.
 */

public class ThumbsUpView extends View {
    /** 三个bitmap */
    private Bitmap thumbsUpOn, thumbsUpOff, thumbsUpDecoration;
    /** bitmap宽高 */
    private int mThumbsWidth, mThumbsHeight, mDecorationWidth, mDecorationHeight;
    /** 是否处于点赞状态 */
    private boolean isThumbsUpOn = false;
    /** 图片的绘制区域 */
    private Rect mThumbsRectSrc, mThumbsRectSrcDst, mThumbsRectSrcDstReal, mDecorationRectSrc, mDecorationRectDst;
    /** 画笔 */
    private Paint mBitmapPaint;
    /** 圆形画笔 */
    private Paint mCirclePaint;
    /** 圆形路径 */
    private Path mCirclePath;
    /** 动画执行进度 */
    private float mFraction;
    /** 圆形半径 */
    private int mRadius;
    /** 是否显示圆 */
    private boolean mShowCircle;
    /** 是否显示手指上边的装饰 */
    private boolean mShowDecoration;
    /** 圆形颜色 */
    private int mCircleColor;
    /** 圆形颜色 */
    private long mDuration;

    public ThumbsUpView(Context context, Bitmap thumbsUpOn, Bitmap thumbsUpOff, Bitmap thumbsUpDecoration) {
        super(context);
        this.thumbsUpOn = thumbsUpOn;
        this.thumbsUpOff = thumbsUpOff;
        this.thumbsUpDecoration = thumbsUpDecoration;
        init();
    }

    public void setShowCircle(boolean showCircle) {
        this.mShowCircle = showCircle;
    }

    public void setShowDecoration(boolean showDecoration) {
        this.mShowDecoration = showDecoration;
    }

    public void setCircleColor(int circleColor) {
        this.mCircleColor = circleColor;
        mCirclePaint.setColor(mCircleColor);
    }

    public void setDuration(long duration){
        this.mDuration = duration;
    }

    public void initThumbsUpOn(boolean isThumbsUpOn) {
        this.isThumbsUpOn = isThumbsUpOn;
        postInvalidate();
    }

    public void setThumbsUpOn(boolean isThumbsUpOn) {
        this.isThumbsUpOn = isThumbsUpOn;
        runAnim();
    }

    private void init() {
        mThumbsWidth = thumbsUpOn.getWidth();
        mThumbsHeight = thumbsUpOn.getHeight();
        mDecorationWidth = thumbsUpDecoration.getWidth();
        mDecorationHeight = thumbsUpDecoration.getHeight();
        mCircleColor = Color.parseColor("#22FF0000");
        mThumbsRectSrc = new Rect(0, 0, mThumbsWidth, mThumbsHeight);
        mThumbsRectSrcDst = new Rect();
        mThumbsRectSrcDstReal = new Rect();
        mDecorationRectSrc = new Rect(0, 0, mDecorationWidth, mDecorationHeight);
        mDecorationRectDst = new Rect();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
        mCirclePath = new Path();
        mShowCircle = true;
        mShowDecoration = true;
        mDuration = ThumbsUpCountView.ANIM_DURATION;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getPaddingLeft() + getPaddingRight() + Math.max(mThumbsWidth, mDecorationWidth);
        int height = getPaddingBottom() + getPaddingTop() + mThumbsHeight + mDecorationHeight;
        mRadius = (mThumbsHeight + mDecorationHeight) / 2;
        setMeasuredDimension(Math.min(width, MeasureSpec.getSize(widthMeasureSpec)),
                Math.min(height, MeasureSpec.getSize(heightMeasureSpec)));
        int centerX = getMeasuredWidth() / 2, centerY = getMeasuredHeight() / 2;
        mThumbsRectSrcDst.left = centerX - mThumbsWidth / 2;
        mThumbsRectSrcDst.top = centerY - mThumbsHeight / 2;
        mThumbsRectSrcDst.right = centerX + mThumbsWidth / 2;
        mThumbsRectSrcDst.bottom = centerY + mThumbsHeight / 2;

        mThumbsRectSrcDstReal.left = mThumbsRectSrcDst.left;
        mThumbsRectSrcDstReal.top = mThumbsRectSrcDst.top;
        mThumbsRectSrcDstReal.right = mThumbsRectSrcDst.right;
        mThumbsRectSrcDstReal.bottom = mThumbsRectSrcDst.bottom;

        mDecorationRectDst.left = centerX - mDecorationWidth / 2;
        mDecorationRectDst.top = mThumbsRectSrcDst.top - mDecorationHeight / 2;
        mDecorationRectDst.right = centerX + mDecorationWidth / 2;
        mDecorationRectDst.bottom = mThumbsRectSrcDst.top + mDecorationHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isThumbsUpOn) {
            canvas.save();
            if (!mCirclePath.isEmpty()) {
                if (mShowCircle) {
                    canvas.drawPath(mCirclePath, mCirclePaint);
                }
                canvas.clipPath(mCirclePath);
            }
            canvas.drawBitmap(thumbsUpOn, mThumbsRectSrc, mThumbsRectSrcDstReal, mBitmapPaint);
            if (mShowDecoration) {
                canvas.drawBitmap(thumbsUpDecoration, mDecorationRectSrc, mDecorationRectDst, mBitmapPaint);
            }
            canvas.restore();
        } else {
            canvas.drawBitmap(thumbsUpOff, mThumbsRectSrc, mThumbsRectSrcDstReal, mBitmapPaint);
        }
    }

    /**
     * 执行动画
     */
    private void runAnim() {
        float[] scaleLevel;
        /* 点赞和取消点赞的缩放比例变化 */
        if (isThumbsUpOn) {
            scaleLevel = new float[] { 0.8f, 1.2f, 1f };
        } else {
            scaleLevel = new float[] { 1f, 0.8f, 1f };
        }
        ValueAnimator animator = ValueAnimator.ofFloat(scaleLevel);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = animation.getAnimatedFraction();
                float value = (float) animation.getAnimatedValue();
                scaleThumbsRect(value);
                postInvalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCirclePath.reset();
                postInvalidate();
            }
        });
        animator.setDuration(mDuration);
        animator.start();
    }

    private void scaleThumbsRect(float scale) {
        float dx = mThumbsRectSrcDst.width() - mThumbsRectSrcDst.width() * scale;
        float dy = mThumbsRectSrcDst.height() - mThumbsRectSrcDst.height() * scale;
        mThumbsRectSrcDstReal.left = (int) (mThumbsRectSrcDst.left + dx / 2);
        mThumbsRectSrcDstReal.top = (int) (mThumbsRectSrcDst.top + dy / 2);
        mThumbsRectSrcDstReal.right = (int) (mThumbsRectSrcDst.right - dx / 2);
        mThumbsRectSrcDstReal.bottom = (int) (mThumbsRectSrcDst.bottom - dy / 2);
        float dr = mDecorationRectDst.height() / 2 * mFraction;
        mCirclePath.reset();
        mCirclePath.addCircle(mThumbsRectSrcDst.centerX(), mThumbsRectSrcDst.centerY(), mThumbsRectSrcDst.height() / 2
                + dr, Path.Direction.CW);
    }
}
