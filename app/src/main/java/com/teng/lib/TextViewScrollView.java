package com.teng.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.teng.loopimage.R;

/**
 * create by deng.wu on 2020/8/8
 */
public class TextViewScrollView extends LoopScrollView<TextView, String> {

    public TextViewScrollView(Context context) {
        super(context);
    }

    public TextViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawView(TextView textView, String s) {
        textView.setText(s);
    }

    @Override
    protected TextView createView() {
        TextView textView = new TextView(getContext());
        textView.setBackgroundResource(R.drawable.shape_gradient_shape_bg);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        return textView;
    }
}
