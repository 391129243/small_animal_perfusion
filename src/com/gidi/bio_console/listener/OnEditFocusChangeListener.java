package com.gidi.bio_console.listener;

import android.view.View;

/**
 * Created by hj on 2018/12/17.
 * 说明：由于使用了OnFocusChangeListener接口，如果项目中需要使用到可以使用这个接口
 */
public interface OnEditFocusChangeListener {
    void OnFocusChangeListener(View v, boolean hasFocus);
}
