package com.src.isec.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.domain
 * @class 参数基类，用于规定入参的规则
 * @time 2018/4/18 14:22
 * @change
 * @chang time
 * @class describe
 */
public class BaseParams {
    /**
     * 所有入参都使用Map作为body
     */
    public Map<String, Object> params = new HashMap<>();
}
