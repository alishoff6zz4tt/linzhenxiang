package com.key;

import android.view.View;

/**
 * Created by Administrator on 2017/4/27 0027.
 */
class ViewWrapper {
    private View view;

    public ViewWrapper(View view) {
        this.view = view;
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public void setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
    }
}
