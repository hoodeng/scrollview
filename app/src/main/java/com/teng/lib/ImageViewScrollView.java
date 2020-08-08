package com.teng.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * create by deng.wu on 2020/8/8
 */
public class ImageViewScrollView extends LoopScrollView<ImageView, Integer> {

    public ImageViewScrollView(Context context) {
        super(context);
    }

    public ImageViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawView(ImageView imageView, Integer res) {
        imageView.setImageResource(res.intValue());
    }

    @Override
    protected ImageView createView() {
        return new ImageView(getContext());
    }
}
