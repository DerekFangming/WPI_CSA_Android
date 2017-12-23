package com.fmning.wpi_csa.helpers;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Fangming
 */

public class CustomDrawer extends DrawerLayout {
    public CustomDrawer(Context context) {
        super(context);
    }

    public CustomDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        return super.onInterceptTouchEvent(event);
    }

}
