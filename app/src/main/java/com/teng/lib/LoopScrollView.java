package com.teng.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teng.loopimage.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * create by deng.wu on 2020/8/7
 */
public abstract class LoopScrollView<V extends View, T> extends ViewGroup {
    private static final String TAG = LoopScrollView.class.getSimpleName();

    private static final long ANIMATOR_LOOPER_DELAY = 3000;
    private static final int IMAGEVIEW_QUEUE_SIZE = 5;

    private final LinkedList<V> mImageViewQueue = new LinkedList<>();
    private final List<Rect> mAnchorRects = new ArrayList<>();

    private int mOuterSpace, mInnerSpace;
    private int mMoveupSpace;

    private LinkedList<T> mModelQueue = new LinkedList<>();

    private Handler mHandler = new Handler();
    private boolean mIsPlaying;
    private Rect mOriginRect;

    private Runnable mLooperRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mIsPlaying) return;

            palyAnimator();
            mHandler.postDelayed(mLooperRunnable, ANIMATOR_LOOPER_DELAY);
        }
    };

    public LoopScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public LoopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoopScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopScrollView);
            mOuterSpace = a.getDimensionPixelSize(R.styleable.LoopScrollView_lsv_outerSpace, 0);
            mInnerSpace = a.getDimensionPixelSize(R.styleable.LoopScrollView_lsv_innerSpace, 0);
            mMoveupSpace = a.getDimensionPixelSize(R.styleable.LoopScrollView_lsv_moveupSpace, 0);

            a.recycle();
        }

        initailizeImageViewQueue();
    }

    //待循环播放数据
    public void shouldLoopPlayModels(List<T> playModels) {
        if (playModels != null) {
            mModelQueue.addAll(playModels);
            int size = mModelQueue.size();
            for (int i = 0; (i < size && i < IMAGEVIEW_QUEUE_SIZE); i++) {
                V v = mImageViewQueue.get(i);
                T t = mModelQueue.poll();
                mModelQueue.offer(t);
                drawView(v, t);
            }
        }
    }

    private void relativedRect(V v, Rect target) {
        Rect origin = getOriginArchorRect();
        v.setTranslationX(target.left - origin.left);
        v.setTranslationY(target.top - origin.top);
    }

    private Rect getOriginArchorRect() {
        return mOriginRect;
    }

    protected abstract void drawView(V v, T t);

    protected abstract V createView();

    private void prepareNextPlay() {
        V v = mImageViewQueue.poll();
        mImageViewQueue.offer(v);
        T t = mModelQueue.poll();
        mModelQueue.offer(t);

        v.setTranslationX(0);
        v.setTranslationX(0);
        drawView(v, t);
    }

    private void initailizeImageViewQueue() {
        for (int i = 0; i < IMAGEVIEW_QUEUE_SIZE; i++) {
            V v = createView();
            mImageViewQueue.push(v);
            this.addView(v);
        }
    }

    public void startAnimator() {
        if (mModelQueue.size() >= IMAGEVIEW_QUEUE_SIZE) { //如果model队列小于5，无需循环
            mIsPlaying = true;
            mHandler.postDelayed(mLooperRunnable, ANIMATOR_LOOPER_DELAY);
        }
    }

    public void stopAnimator() {
        mIsPlaying = false;
        mHandler.removeCallbacks(mLooperRunnable);
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    private void palyAnimator() {
        List<Animator> animators = new ArrayList<>();
        for (int i = 0, size = mImageViewQueue.size(); i < size; i++) {
            V v = mImageViewQueue.get(i);
            Rect target = mAnchorRects.get(i);
            Rect origin = mAnchorRects.get(i + 1);
//            if (v instanceof TextView) {
//                Log.v(TAG, "palyAnimator translationX---> " + ((TextView) v).getText() + "  " + v.getTranslationX());
//                Log.v(TAG, "palyAnimator translationY---> " + ((TextView) v).getText() + "  " + v.getTranslationY());
//            }

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(v, "translationX", v.getTranslationX(), v.getTranslationX() + target.left - origin.left);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(v, "translationY", v.getTranslationY(), v.getTranslationY() + target.top - origin.top);
            animators.add(animatorX);
            animators.add(animatorY);
        }


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setDuration(300);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                prepareNextPlay();
            }
        });
        animatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = widthSize;
        int childW = (width - 2 * mOuterSpace - 3 * mInnerSpace) / 4;
        int childH = childW;

        for (int i = 0, size = getChildCount(); i < size; i++) {
            View child = getChildAt(i);
            child.measure(getChildMeasureSpec(childW, MeasureSpec.EXACTLY), getChildMeasureSpec(childH, MeasureSpec.EXACTLY));
        }
        int height = childH + mMoveupSpace;
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private int getChildMeasureSpec(int childSpecSize, int childSpecMode) {
        return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.v(TAG, "onSizeChanged");
        resizeRect(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v(TAG, "onLayout");
        Rect rect = getOriginArchorRect();//右边
        for (int i = 0, size = mImageViewQueue.size(); i < size; i++) {
            V v = mImageViewQueue.get(i);
            layoutImageView(v, rect);
            relativedRect(v, mAnchorRects.get(i + 1));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    private void layoutImageView(V v, Rect rect) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.width = rect.right - rect.left;
        lp.height = rect.bottom - rect.top;
        v.setLayoutParams(lp);
        v.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    private void resizeRect(int w, int h) {
        int width = w;
        int rectW = (width - 2 * mOuterSpace - 3 * mInnerSpace) / 4;

        int left = -rectW;
        int top = mMoveupSpace;
        Rect rect = new Rect(left, top, 0, top + rectW);
        mAnchorRects.add(rect);

        left = mOuterSpace;
        top = mMoveupSpace;
        rect = new Rect(left, top, left + rectW, top + rectW);
        mAnchorRects.add(rect);

        left = left + rectW + mInnerSpace;
        top = 0;
        rect = new Rect(left, top, left + rectW, top + rectW);
        mAnchorRects.add(rect);

        left = left + rectW + mInnerSpace;
        top = 0;
        rect = new Rect(left, top, left + rectW, top + rectW);
        mAnchorRects.add(rect);

        left = left + rectW + mInnerSpace;
        top = mMoveupSpace;
        rect = new Rect(left, top, left + rectW, top + rectW);
        mAnchorRects.add(rect);

        left = w;
        top = mMoveupSpace;
        rect = new Rect(left, top, left + rectW, top + rectW);
        mAnchorRects.add(rect);

        mOriginRect = rect;
    }

    public void setOuterSpace(int outerSpace) {
        this.mOuterSpace = outerSpace;
    }

    public void setInnerSpace(int innerSpace) {
        this.mInnerSpace = innerSpace;
    }

    public void setMoveupSpace(int moveupSpace) {
        this.mMoveupSpace = moveupSpace;
    }
}
