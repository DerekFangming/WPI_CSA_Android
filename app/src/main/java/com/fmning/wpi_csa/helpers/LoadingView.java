package com.fmning.wpi_csa.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.fmning.wpi_csa.R;

/**
 * Created by fangmingning
 * On 11/21/17.
 */

public class LoadingView extends RelativeLayout {
    View serverDownView;
    OnVisibleChangedListener listener;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        serverDownView = findViewById(R.id.serverDownView);
    }

    public void showServerDownView(){
        serverDownView.setVisibility(View.VISIBLE);
    }

    public void removeServerDownView(){
        serverDownView.setVisibility(View.INVISIBLE);
    }

    //The following two methods are conforming to iOS's default methods for view
    public void removeFromSuperview(){
        setVisibility(View.INVISIBLE);
        if (listener != null){
            listener.OnVisible(false);
        }
    }

    public void addToSuperview(){
        setVisibility(View.VISIBLE);
        if (listener != null){
            listener.OnVisible(true);
        }
    }

    public void setVisibleChangeListener(OnVisibleChangedListener listener) {
        this.listener = listener;
    }

    public interface OnVisibleChangedListener {
        void OnVisible(boolean visible);
    }
}
