package com.src.isec.data.utils;

import com.blankj.utilcode.util.EncryptUtils;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import timber.log.Timber;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.data.utils
 * @class 接口入参签名工具
 * @time 2018/4/17 9:49
 * @change
 * @chang time
 * @class describe
 */
public class SignRequestUtil {

    public static final String SIGNKEY = "ugame_live_test";
    public static final String SIGNTYPE = "MD5";


    /**
     * 默认签名加密
     *
     * @return
     */
    public static String signDefualRequest(String time) {
//
        TreeMap<String, String> treeMap = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        treeMap.put("appKey", SignRequestUtil.SIGNKEY);
        treeMap.put("t", time);

        Timber.w("升序排序结果：" + treeMap);
        StringBuffer sb = new StringBuffer();
        //把map中的集合拼接成字符串
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.length() == 0)
                continue;
            sb.append(key).append("=").append(value).append("&");
        }

        //删掉最后一个&符号
        if (sb.indexOf("&") != -1) {
            sb.deleteCharAt(sb.lastIndexOf("&"));
        }

        Timber.w("拼接后的字符：" + sb.toString());

        //进行MD5加密
        String sign = EncryptUtils.encryptMD5ToString(getContentBytes(sb.toString(), "utf-8"))
                .toLowerCase();
        Timber.w("加密后的签名：" + sign);
        return sign;

    }

    /**
     * @param content
     * @param charset
     * @return
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * 参数签名加密
     *
     * @param parameters
     * @return
     */
    public static String signRequest(TreeMap<String, String> parameters, String
            charset) {

        TreeMap<String, String> treeMap = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        treeMap = parameters;
        Timber.w("升序排序结果：" + treeMap);
        StringBuffer sb = new StringBuffer();
        //把map中的集合拼接成字符串
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.length() == 0)
                continue;
            sb.append(key).append("=").append(value).append("&");
        }

        //删掉最后一个&符号
        if (sb.indexOf("&") != -1) {
            sb.deleteCharAt(sb.lastIndexOf("&"));
        }

        Timber.w("拼接后的字符：" + sb.toString());

        //进行MD5加密
        String sign = EncryptUtils.encryptMD5ToString(getContentBytes(sb.toString(), charset))
                .toLowerCase();
        Timber.w("加密后的签名：" + sign);
        return sign;
    }


}
