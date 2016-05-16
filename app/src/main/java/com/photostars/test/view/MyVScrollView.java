package com.photostars.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Photostsrs on 2016/5/12.
 */
public class MyVScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public MyVScrollView(Context context) {
        super(context);
    }

    public MyVScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public interface ScrollViewListener {

        void onScrollChanged( MyVScrollView scrollView,int x, int y, int oldx, int oldy);

    }
}
