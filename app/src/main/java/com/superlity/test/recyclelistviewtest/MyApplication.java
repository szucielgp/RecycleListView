/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.superlity.test.recyclelistviewtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class MyApplication extends Application {

    private static MyApplication instance;
    public static final String KEY_CLIENT_ID = "client_id";
    static SharedPreferences preferences;

    /**
     * 单例，返回一个实例
     *
     * @return
     */
    public static MyApplication getInstance() {

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AVOSCloud.setDebugLogEnabled(true);
        // 这是用于 SimpleChat 的 app id 和 app key，如果更改将不能进入 demo 中相应的聊天室
        AVOSCloud.initialize(this, "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm",
                "ktslkgc5rm1kwk5hft7n5kmzdmqbu0o8vebbfvct48ybg1xk");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 必须在启动的时候注册 MessageHandler
        // 应用一启动就会重连，服务器会推送离线消息过来，需要 MessageHandler 来处理
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(this));
        initImageLoader(instance);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static String getClientIdFromPre() {
        return preferences.getString(KEY_CLIENT_ID, "");
    }

    public static void setClientIdToPre(String id) {
        preferences.edit().putString(KEY_CLIENT_ID, id).apply();
    }

    public static AVIMClient getIMClient() {
        return AVIMClient.getInstance(getClientIdFromPre());
    }


    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                        //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
