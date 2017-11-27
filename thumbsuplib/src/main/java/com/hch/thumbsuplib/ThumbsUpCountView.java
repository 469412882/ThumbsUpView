package com.hch.thumbsuplib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 点赞view
 * 
 * Created by hch on 2017/11/21.
 */

public class ThumbsUpCountView extends LinearLayout {
    /** 动画执行时间 */
    public static long ANIM_DURATION = 200;
    /** 是否点赞 */
    private boolean mThumbsUp;
    /** 点赞数 */
    private int mPriseCount;
    /** 三个bitmap */
    private Bitmap mThumbsUpOn, mThumbsUpOff, mThumbsUpDecoration;
    /** 是否显示手指上边的装饰 */
    private boolean mShowDecoration;
    /** 是否显示动态圆 */
    private boolean mShowCircle;
    /** 动态圆的颜色 */
    private int mCircleColor;
    /** 点赞数字颜色 */
    private int mTextOnColor;
    /** 非点赞数字颜色 */
    private int mTextOffColor;
    /** 字体大小 */
    private float mTextSize;
    /** 拇指view */
    private ThumbsUpView thumbsUpView;
    /** 赞数view */
    private PriseCountView priseCountView;

    public ThumbsUpCountView(Context context) {
        this(context, null);
    }

    public ThumbsUpCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbsUpCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClipChildren(false);
        removeAllViews();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ThumbsUpCountView);
            Drawable on = array.getDrawable(R.styleable.ThumbsUpCountView_thumbsup_on_drawable);
            if (on != null) {
                mThumbsUpOn = ((BitmapDrawable) on).getBitmap();
            } else {
                mThumbsUpOn = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_on);
            }
            Drawable off = array.getDrawable(R.styleable.ThumbsUpCountView_thumbsup_off_drawable);
            if (off != null) {
                mThumbsUpOff = ((BitmapDrawable) off).getBitmap();
            } else {
                mThumbsUpOff = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_off);
            }
            Drawable decoration = array.getDrawable(R.styleable.ThumbsUpCountView_thumbsup_on_decoration_drawable);
            if (decoration != null) {
                mThumbsUpOn = ((BitmapDrawable) decoration).getBitmap();
            } else {
                mThumbsUpDecoration = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_decoration);
            }
            mShowCircle = array.getBoolean(R.styleable.ThumbsUpCountView_thumbsup_on_show_circle, true);
            mShowDecoration = array.getBoolean(R.styleable.ThumbsUpCountView_thumbsup_on_show_decoration, true);
            mCircleColor = array.getColor(R.styleable.ThumbsUpCountView_thumbsup_on_circle_color, getResources()
                    .getColor(R.color.thumbs_circle_color));
            mTextOnColor = array.getColor(R.styleable.ThumbsUpCountView_thumbsup_on_text_color, getResources()
                    .getColor(R.color.thumbs_text_color));
            mTextOffColor = array.getColor(R.styleable.ThumbsUpCountView_thumbsup_off_text_color, getResources()
                    .getColor(R.color.thumbs_text_color));
            mTextSize = array.getDimension(R.styleable.ThumbsUpCountView_thumbsup_text_size, getResources()
                    .getDimension(R.dimen.prise_count_text_size));
            array.recycle();
        } else {
            mThumbsUpOn = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_on);
            mThumbsUpOff = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_off);
            mThumbsUpDecoration = BitmapFactory.decodeResource(getResources(), R.mipmap.thumbs_decoration);
            mTextOnColor = getResources().getColor(R.color.thumbs_text_color);
            mTextOffColor = getResources().getColor(R.color.thumbs_text_color);
            mTextSize = getResources().getDimension(R.dimen.prise_count_text_size);
            mShowCircle = true;
            mShowDecoration = true;
            mCircleColor = getResources().getColor(R.color.thumbs_circle_color);
        }

        thumbsUpView = new ThumbsUpView(getContext(), mThumbsUpOn, mThumbsUpOff, mThumbsUpDecoration);
        thumbsUpView.setShowCircle(mShowCircle);
        thumbsUpView.setCircleColor(mCircleColor);
        thumbsUpView.setShowDecoration(mShowDecoration);
        LayoutParams lp1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.leftMargin = (int) getResources().getDimension(R.dimen.thumbs_child_margin);
        addView(thumbsUpView, lp1);
        priseCountView = new PriseCountView(getContext(), mPriseCount, mTextOnColor, mTextOffColor, mTextSize);
        LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.leftMargin = getResources().getDimensionPixelSize(R.dimen.prise_count_left_margin);
        lp2.rightMargin = (int) getResources().getDimension(R.dimen.thumbs_child_margin);
        addView(priseCountView, lp2);
    }

    public void initData(boolean thumbsUp, int priseCount) {
        this.mThumbsUp = thumbsUp;
        this.mPriseCount = priseCount;
        thumbsUpView.initThumbsUpOn(thumbsUp);
        priseCountView.setCount(priseCount, thumbsUp);
    }

    public void setAnimDuration(long duration){

    }

    public void priseChange() {
        if (mThumbsUp) {
            mPriseCount--;
        } else {
            mPriseCount++;
        }
        mThumbsUp = !mThumbsUp;
        thumbsUpView.setThumbsUpOn(mThumbsUp);
        priseCountView.setCount(mPriseCount, mThumbsUp);
    }

    public int getPriseCount() {
        return mPriseCount;
    }

}
