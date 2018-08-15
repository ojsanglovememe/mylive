package com.src.isec.data.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import top.zibin.luban.Luban;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.utils
 * @class 图片压缩工具
 * @time 2018/4/24 11:53
 * @change
 * @chang time
 * @class describe
 */
public class PictureCompressUtil {


    private static Context mContext;

    private PictureCompressUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @author liujiancheng
     * @time 2018/4/24  11:58
     * @describe 在Application中传入Context
     */
    public static void init(Context _context) {

        mContext = _context;
    }

    /**
     * @author liujiancheng
     * @time 2018/4/24  12:01
     * @describe 开始压缩，并过滤100kb的图片
     */
    public static File compressPic(String fileUri) throws IOException {
        return Luban.with(mContext).ignoreBy(100).get(fileUri);

    }

}
