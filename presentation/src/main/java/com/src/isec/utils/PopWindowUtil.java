package com.src.isec.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.src.isec.R;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.utils
 * @class popWindow工具类
 * @time 2018/4/21 11:12
 * @change
 * @chang time
 * @class describe
 */

public class PopWindowUtil {
    private PopupWindow popupWindow;
    private Context mContext;


    public PopWindowUtil(Context context){
        mContext = context;
    }

    /**
     * 分享弹窗
     * @param listener
     */
    public void creatLiveSharePop(RadioGroup.OnCheckedChangeListener listener) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_share, null);
        Button btnCancel = view.findViewById(R.id.btn_live_share_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        RadioGroup radioGroup = view.findViewById(R.id.rg_root);
        radioGroup.setOnCheckedChangeListener(listener);
        popupWindow = new PopupWindow(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置弹出窗体显示时的动画，从底部向上弹出
        popupWindow.setAnimationStyle(R.style.take_photo_anim);
    }

    public void  createLiveEndPop(View.OnClickListener listener){
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_end_pop,null);
        view.findViewById(R.id.btn_live_end).setOnClickListener(listener);
        popupWindow = new PopupWindow(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
    }

    /**
     * 底部位置弹窗
     * @param view
     */
    public void showAtBottom(View view){
        popupWindow.showAtLocation(view, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 屏幕中央位置，传入页面根布局
     * @param view
     */
    public void showInCenter(View view){
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
    }

    /**
     * 在某个view下弹窗，需要偏移量可重载该方法
     * @param view
     */
    public void showDrop(View view){
        popupWindow.showAsDropDown(view,0,0);
    }

    public void dismiss(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    public boolean isShowing(){
        return popupWindow.isShowing();
    }
}
