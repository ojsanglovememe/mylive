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
package com.src.isec.data.network;



/**
 * @author liujiancheng
 * @name IsecLive
 * @class name：com.src.iseclive.integration
 * @class 资源管理
 * 用于管理RetrofitService
 * @time 2018/3/19 13:58
 * @change
 * @chang time
 * @class describe
 */

public interface IRepositoryManager {



    /**
     *  @author liujiancheng
     *  @time 2018/3/19  13:59
     *  @describe 根据传入的 Class 获取对应的 Retrofit service
     */
    <T> T obtainRetrofitService(Class<T> service);






}
