package com.example.loafsmac.rubyred;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by bertolopez-cruz on 2/8/16.
 */
public class SquaredView extends ImageView {
    public SquaredView(Context context) {
        super(context);
    }
    public SquaredView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public SquaredView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

}