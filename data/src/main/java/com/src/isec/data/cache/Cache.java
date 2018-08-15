/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.src.isec.data.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration.cache
 * @class 自定义缓存框架
 * @time 2018/3/19 13:56
 * @change
 * @chang time
 * @class describe
 */
public interface Cache<K, V> {

    /**
     * @author liujiancheng
     * @time 2018/3/19  13:53
     * @describe 缓存框架工厂
     */
    interface Factory {
        /**
         * @param type 框架中需要缓存的模块类型
         * @author liujiancheng
         * @time 2018/3/19  13:52
         * @describe 构建缓存
         */
        @NonNull
        Cache build(CacheType type);
    }


    /**
     * @author liujiancheng
     * @time 2018/3/19  13:54
     * @describe 返回当前缓存已占用的总 size
     */
    int size();


    /**
     * @author liujiancheng
     * @time 2018/3/19  13:54
     * @describe 返回当前缓存所能允许的最大 size
     */
    int getMaxSize();


    /**
     * @author liujiancheng
     * @time 2018/3/19  13:54
     * @describe 返回这个 {@code key} 在缓存中对应的 {@code value}, 如果返回 {@code null} 说明这个 {@code key} 没有对应的
     * {@code value}
     */
    @Nullable
    V get(K key);


    /**
     * @author liujiancheng
     * @time 2018/3/19  13:55
     * @describe 将 {@code key} 和 {@code value} 以条目的形式加入缓存,如果这个 {@code key} 在缓存中已经有对应的 {@code value}
     * 则此 {@code value} 被新的 {@code value} 替换并返回,如果为 {@code null} 说明是一个新条目
     */
    @Nullable
    V put(K key, V value);


    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:55
     *  @describe 移除缓存中这个 {@code key} 所对应的条目,并返回所移除条目的 value
     * 如果返回为 {@code null} 则有可能时因为这个 {@code key} 对应的 value 为 {@code null} 或条目不存在
     */
    @Nullable
    V remove(K key);


    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:55
     *  @describe  如果这个 {@code key} 在缓存中有对应的 value 并且不为 {@code null}, 则返回 {@code true}
     */
    boolean containsKey(K key);



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:55
     *  @describe 返回当前缓存中含有的所有 {@code key}
     *  使用迭代器进行遍历
     */
    Set<K> keySet();


    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:56
     *  @describe 清除缓存中所有的内容
     */
    void clear();
}
