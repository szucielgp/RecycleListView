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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.superlity.test.recyclelistviewtest.controller.ChatManager;
import com.superlity.test.recyclelistviewtest.service.ChatManagerAdapterImpl;
import com.superlity.test.recyclelistviewtest.service.PushManager;


public class MyApplication extends Application {


    private static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.setLastModifyEnabled(true);
        final String appId = "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm";
        final String appKey =  "ktslkgc5rm1kwk5hft7n5kmzdmqbu0o8vebbfvct48ybg1xk";
        AVOSCloud.initialize(this,appId ,appKey);
        savaInstall();
        PushManager.getInstance().init(instance);
        initImageLoader(instance);
        initChatManager();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    private void initChatManager() {
        final ChatManager chatManager = ChatManager.getInstance();
        chatManager.init(this);
        if (AVUser.getCurrentUser() != null) {
            chatManager.setupManagerWithUserId(AVUser.getCurrentUser().getObjectId());
        }
       // chatManager.setConversationEventHandler(ConversationManager.getEventHandler());
        ChatManagerAdapterImpl chatManagerAdapter = new ChatManagerAdapterImpl(MyApplication.this);
        chatManager.setChatManagerAdapter(chatManagerAdapter);
        ChatManager.setDebugEnabled(true);
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

    public void savaInstall(){
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });

    }
}
