package com.src.isec.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.src.isec.R;
/**
 * @name
 * @class name：
 * @class describe  退出直播间时的dialog
 * @author wj
 * @time
 * @change
 * @chang time
 * @class describe
 */
public class ComfirmDialogUtils {

    private final AlertDialog.Builder builder;

    public ComfirmDialogUtils(Activity activity, String msg) {
        builder = new AlertDialog.Builder(activity, R.style.ConfirmDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(msg);
    }

    /**
     * 显示确认消息
     *
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(Boolean isError, OnComfirmListener listener) {
        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (listener != null)
                        listener.onSure();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            if (listener != null) {
                listener.onErrorEvent();
            }
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (listener != null) {
                        listener.onErrorSure();
                    }
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public interface OnComfirmListener {
        void onSure();

        void onErrorEvent();

        void onErrorSure();


    }

}
