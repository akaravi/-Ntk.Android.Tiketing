package ntk.android.ticketing.adapter;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PagerAdapter extends ViewPager {

    public PagerAdapter(Context context) {
        super(context);
    }

    public PagerAdapter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
