package com.src.isec.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;

import com.src.isec.R;

/**
 * @name
 * @class name：
 * @class describe 和图片有关的工具类
 * @author wj
 * @time
 * @change
 * @chang time
 * @class describe
 */
public class PicUtils {

    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /*
     * 获取网络类型
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 滤镜定义
     */
    public static final int FILTERTYPE_NONE         = 0;    //无特效滤镜
    public static final int FILTERTYPE_langman      = 1;    //浪漫滤镜
    public static final int FILTERTYPE_qingxin      = 2;    //清新滤镜
    public static final int FILTERTYPE_weimei       = 3;    //唯美滤镜
    public static final int FILTERTYPE_fennen 		= 4;    //粉嫩滤镜
    public static final int FILTERTYPE_huaijiu 		= 5;    //怀旧滤镜
    public static final int FILTERTYPE_landiao 		= 6;    //蓝调滤镜
    public static final int FILTERTYPE_qingliang 	= 7;    //清凉滤镜
    public static final int FILTERTYPE_rixi 		= 8;    //日系滤镜

    private static Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
    public static Bitmap getFilterBitmap(Resources resources, int filterType) {
        Bitmap bmp = null;
        switch (filterType) {
            case FILTERTYPE_langman:
                bmp = decodeResource(resources, R.drawable.filter_langman);
                break;
            case FILTERTYPE_qingxin:
                bmp = decodeResource(resources, R.drawable.filter_qingxin);
                break;
            case FILTERTYPE_weimei:
                bmp = decodeResource(resources, R.drawable.filter_weimei);
                break;
            case FILTERTYPE_fennen:
                bmp = decodeResource(resources, R.drawable.filter_fennen);
                break;
            case FILTERTYPE_huaijiu:
                bmp = decodeResource(resources, R.drawable.filter_huaijiu);
                break;
            case FILTERTYPE_landiao:
                bmp = decodeResource(resources, R.drawable.filter_landiao);
                break;
            case FILTERTYPE_qingliang:
                bmp = decodeResource(resources, R.drawable.filter_qingliang);
                break;
            case FILTERTYPE_rixi:
                bmp = decodeResource(resources, R.drawable.filter_rixi);
                break;
            default:
                bmp = null;
                break;
        }
        return bmp;
    }


    public static String getGreenFileName(int index) {
        String strGreenFileName;
        switch (index) {
            case 0:
                strGreenFileName = "";
                break;
            case 1:
                strGreenFileName = "green_1.mp4";
                break;
            case 2:
                strGreenFileName = "green_2.mp4";
                break;
            default:
                strGreenFileName = "";
                break;
        }
        return strGreenFileName;
    }

}
