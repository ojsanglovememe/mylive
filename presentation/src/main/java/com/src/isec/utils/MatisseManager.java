package com.src.isec.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.src.isec.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

import java.io.File;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.utils
 * @class 图片选择器帮助类
 * @time 2018/4/25 18:14
 * @change
 * @chang time
 * @class describe
 */
public class MatisseManager {

    public static final int REQUEST_CODE_CHOOSEMATISSE = 1001;

    /**拍照**/
    public static final int REQUEST_CODE_CAPTURE = 1002;
    /**裁剪**/
    public static final int REQUEST_CODE_CROP = 1003;

    /**
     * @author liujiancheng
     * @time 2018/4/25  18:18
     * @describe 单选并且裁剪成方形图片
     */
    public static void singleSquarePicture(Activity activity) {
        Matisse.from(activity)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.src.isec.fileprovider"))
                .gridExpectedSize(
                        activity.getResources().getDimensionPixelSize(R.dimen
                                .grid_expected_size))
                .singleElection()
                .thumbnailScale(0.85f)
                .needCrop()
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSEMATISSE);
    }

    /**
     * @author liujiancheng
     * @time 2018/4/25  18:18
     * @describe 单选并且裁剪成方形图片
     */
    public static void singleSquarePicture(Fragment fragment) {
        Matisse.from(fragment)
                .choose(MimeType.ofImage())
                .capture(true)

                .captureStrategy(
                        new CaptureStrategy(true, "com.src.isec.fileprovider"))
                .gridExpectedSize(
                        fragment.getResources().getDimensionPixelSize(R.dimen
                                .grid_expected_size))
                .singleElection()
                .thumbnailScale(0.85f)
                .needCrop()
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSEMATISSE);
    }

    public static MediaStoreCompat getMediaStoreCompat(Activity activity){
        MediaStoreCompat mediaStoreCompat = new MediaStoreCompat(activity);
        mediaStoreCompat.setCaptureStrategy(new CaptureStrategy(true, "com.src.isec.fileprovider"));
        return mediaStoreCompat;
    }

    public static Uri startImageCrop(Activity activity, Uri uri) {
        String filename = "zhanglingcrop" + System.currentTimeMillis() + ".jpg";
        String path = Environment.getExternalStorageDirectory() + File.separator + "zhanglingzhibo";
        File outputImage = new File(path, filename);

        try {
            File pathFile = new File(path);
            if(!pathFile.exists()) {
                pathFile.mkdirs();
            }

            if(outputImage.exists()) {
                outputImage.delete();
            }

            Uri mUriCrop = Uri.fromFile(outputImage);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 750);
            intent.putExtra("aspectY", 750);
            intent.putExtra("outputX", 750);
            intent.putExtra("outputY", 750);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriCrop);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            activity.startActivityForResult(intent, REQUEST_CODE_CROP);

            return mUriCrop;

        } catch (Exception e){
            return null;
        }

    }

}
