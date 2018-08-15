package com.src.isec.utils;

/**
 * @name
 * @class name：
 * @class describe  数学计算的工具类
 * @author wj
 * @time
 * @change
 * @chang time
 * @class describe
 */
public class MathUtils {
    /**
     * 根据比例转化实际数值为相对值
     * @param gear 档位
     * @param max 最大值
     * @param curr 当前值
     * @return 相对值
     */
    public static int filtNumber(int gear, int max, int curr) {
        return curr / (max / gear);
    }
}
