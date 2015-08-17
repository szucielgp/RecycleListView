package com.superlity.test.recyclelistviewtest.leancloud;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.superlity.test.recyclelistviewtest.service.PushManager;

/**
 * Created by lion on 2015/8/14 0014.
 * leancloud 初始化工具类
 */
public class LCSDKHelper {

    private static final String TAG = "LCSDKHelper";
    private static final String APPID = "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm";
    private static final String APPKEY = "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm";

    private static LCSDKHelper instance;

    public static LCSDKHelper getInstance() {
        if (instance == null) {
            instance = new LCSDKHelper();
        }
        return instance;
    }

    public void init( Context context ) {
        if (context == null) {
            //TODO
            Log.e(TAG, "未传入context");
            return;
        }

        AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.setLastModifyEnabled(true);
        AVOSCloud.initialize(context, APPID, APPKEY);

        PushManager.getInstance().init(context);
//        initInstallationId();
        initImageLoader(context);
        initChatManager(context);
    }

    private void initChatManager( Context context ) {
        ChatManager.setDebugEnabled(true);
        final ChatManager chatManager = ChatManager.getInstance();
        chatManager.init(context);
        if (AVUser.getCurrentUser() != null) {
            chatManager.setupManagerWithUserId(AVUser.getCurrentUser().getObjectId());
        }
        ChatManagerAdapterImpl chatManagerAdapter = new ChatManagerAdapterImpl(context);
        chatManager.setChatManagerAdapter(chatManagerAdapter);
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                        //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

//    private void initInstallationId() {
//        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
//            public void done(AVException e) {
//                if (e == null) {
//                    // 保存成功
//                    // String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
//                    // 关联  installationId 到用户表等操作……
//                } else {
//                    // 保存失败，输出错误信息
//                }
//            }
//        });
//    }
}
