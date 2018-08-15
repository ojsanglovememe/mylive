package com.src.isec.utils;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.utils
 * @class Toolbar工具类
 * @time 2018/3/20 10:37
 * @change
 * @chang time
 * @class describe
 */

public class ToolbarHelper {

    /**
     * @author liujiancheng
     * @time 2018/3/20  10:38
     * @describe 设置Toolbar的标题
     */
    public static void addMiddleTitle(Context context, CharSequence title, Toolbar toolbar) {

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextSize(18);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        toolbar.addView(textView, params);

    }



}
