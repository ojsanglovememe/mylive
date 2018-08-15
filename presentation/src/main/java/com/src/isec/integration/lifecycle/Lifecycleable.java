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
package com.src.isec.integration.lifecycle;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.RxLifecycle;

import io.reactivex.subjects.Subject;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.integration.lifecycle
 * @class RxLifecycle扩展接口
 *        Activity/Fragment实现此接口，实现Rx与Activity/Fragment的生命周期绑定
 *        无需再继承 {@link RxLifecycle} 提供的 Activity/Fragment
 * @time 2018/3/19 9:31
 * @change
 * @chang time
 * @class describe
 */
public interface Lifecycleable<E> {
    @NonNull
    Subject<E> provideLifecycleSubject();
}
