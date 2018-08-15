package com.src.isec.utils;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.src.isec.R;

import java.io.File;

/**
 * 选择封面的工具类
 */
public class PickPicDialogUtils {


    private final Dialog mPicChsDialog;

    public PickPicDialogUtils(Activity context, OnPickListener listener) {
        mPicChsDialog = new Dialog(context, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.dialog_pic_choose);
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度
        mPicChsDialog.getWindow().setAttributes(lp);
        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
                if (listener != null) {
                    listener.onCarmeraClick();
                }
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
                if (listener != null) {
                    listener.onPicLibClick();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
//                if (listener != null) {
//                    listener.onCancel();
//                }
            }
        });

    }

    public void show() {
        if (mPicChsDialog != null && !mPicChsDialog.isShowing()) {
            mPicChsDialog.show();
        }
    }

    /**
     * 创建封面地址
     *
     * @param type
     * @return
     */
    public Uri createCoverUri(String type) {
//        String filename = getUserId() + type + ".jpg";
        //TODO 命名规范需要确定
        String filename = "zhangling" + type + System.currentTimeMillis() + ".jpg";
        String path = Environment.getExternalStorageDirectory() + File.separator + "zhanglingzhibo";
        File outputImage = new File(path, filename);
        try {
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (outputImage.exists()) {
                outputImage.delete();
            }
//            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("生成封面失败");
        }
        return Uri.fromFile(outputImage);
    }


    public interface OnPickListener {
        void onCarmeraClick();

        void onPicLibClick();

//        void onCancel();
    }

}
