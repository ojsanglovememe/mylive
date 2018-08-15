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
package com.src.isec.di.socpe;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.isec.di.socpe
 * @class Activity的作用域
 *        注解依赖后，表明此依赖的作用域与Activity的生命周期一样
 * @time 2018/3/19 9:12
 * @change
 * @chang time
 * @class describe
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface ActivityScope {}
