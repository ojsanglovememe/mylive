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
package com.src.isec.utils;


import com.src.isec.domain.utils.Preconditions;
import com.src.isec.integration.lifecycle.ActivityLifecycleable;
import com.src.isec.integration.lifecycle.FragmentLifecycleable;
import com.src.isec.integration.lifecycle.Lifecycleable;
import com.src.isec.mvp.view.IView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.annotations.NonNull;


/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.mvp.view.implement
 * @class RxLifecycle工具类
 * 用来绑定Rxjava生命周期
 * @time 2018/3/20 16:39
 * @change
 * @chang time
 * @class describe
 */
public class RxLifecycleUtils {

    private RxLifecycleUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }



    /**
     *  @author liujiancheng
     *  @time 2018/3/20  18:12
     *  @describe 绑定 Activity 的指定生命周期
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final ActivityEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof ActivityLifecycleable) {
            return bindUntilEvent((ActivityLifecycleable) view, event);
        } else {
            throw new IllegalArgumentException("view isn't ActivityLifecycleable");
        }
    }


    /**
     *  @author liujiancheng
     *  @time 2018/3/20  18:12
     *  @describe 绑定 Fragment 的指定生命周期
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final FragmentEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof FragmentLifecycleable) {
            return bindUntilEvent((FragmentLifecycleable) view, event);
        } else {
            throw new IllegalArgumentException("view isn't FragmentLifecycleable");
        }
    }

    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final Lifecycleable<R>
                                                                        lifecycleable,
                                                                final R event) {
        Preconditions.checkNotNull(lifecycleable, "lifecycleable == null");
        return RxLifecycle.bindUntilEvent(lifecycleable.provideLifecycleSubject(), event);
    }




    /**
     *  @author liujiancheng
     *  @time 2018/3/20  18:13
     *  @describe 绑定 Activity/Fragment 的生命周期
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IView view) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof Lifecycleable) {
            return bindToLifecycle((Lifecycleable) view);
        } else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull Lifecycleable
                                                                      lifecycleable) {
        Preconditions.checkNotNull(lifecycleable, "lifecycleable == null");
        if (lifecycleable instanceof ActivityLifecycleable) {
            return RxLifecycleAndroid.bindActivity(((ActivityLifecycleable) lifecycleable)
                    .provideLifecycleSubject());
        } else if (lifecycleable instanceof FragmentLifecycleable) {
            return RxLifecycleAndroid.bindFragment(((FragmentLifecycleable) lifecycleable)
                    .provideLifecycleSubject());
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }

}
