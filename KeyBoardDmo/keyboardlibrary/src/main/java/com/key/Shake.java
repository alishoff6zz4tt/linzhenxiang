package com.key;

import android.view.View;


/**
 * @author:Lzx
 * @date:2017/4/17 0017
 * @time:上午 9:40
 * @todo: 防View 重复点击
 */
 class Shake {


    /**
     * 防止重复点击 1秒 内允许点击一次
     *
     * @param view
     * @return
     */
    public static boolean shake(View view) {
        Object lastTime = view.getTag(R.id.view_shake);
        if (lastTime != null) {
            long dx = System.currentTimeMillis() - (long) lastTime;
            if (dx < 500) {
                return true;
            } else {
                view.setTag(R.id.view_shake, System.currentTimeMillis());
                return false;
            }
        } else {
            view.setTag(R.id.view_shake, System.currentTimeMillis());
            return false;
        }
    }
}
