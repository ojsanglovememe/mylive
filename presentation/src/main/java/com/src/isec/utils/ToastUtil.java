package com.src.isec.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.utils
 * @class Toast工具类
 * @time 2018/3/26 15:46
 * @change
 * @chang time
 * @class describe
 */

public class ToastUtil {
    private static Toast it;
    private ToastUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/26  15:48
     *  @describe  在程序初始化的时候调用, 只需调用一次
     */
    public static void init(Context _context) {
        View v = Toast.makeText(_context, "", Toast.LENGTH_SHORT).getView();
        init(_context, v);
    }


    /**
     *  @author liujiancheng
     *  @time 2018/3/26  15:48
     *  @describe  在程序初始化的时候调用, 只需调用一次
     */
    public static void init(Context _context, View view) {
        it = new Toast(_context);
        it.setView(view);
    }


    /**
     *  @author liujiancheng
     *  @time 2018/3/26  15:49
     *  @describe 设置Toast背景
     */
    public static void setBackgroundView(View view) {
        checkInit();
        it.setView(view);
    }

    public static void show(CharSequence text, int duration) {
        checkInit();
        it.setText(text);
        it.setDuration(duration);
        it.show();
    }

    public static void show(int resid, int duration) {
        checkInit();
        it.setText(resid);
        it.setDuration(duration);
        it.show();
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void show(int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    private static void checkInit() {
        if (it == null) {
            throw new IllegalStateException("ToastMgr is not initialized, please call init once before you call this method");
        }
    }

}
