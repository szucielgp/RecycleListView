package com.superlity.test.recyclelistviewtest.controller;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by lion on 2015/8/14 0014.
 */
public class LCSDKHelper {

    private static final String TAG = "LCSDKHelper";
    private static final String APPID = "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm";
    private static final String APPKEY = "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm";

    private static LCSDKHelper instance;
    private Context context;

    public static LCSDKHelper getInstance() {
        if ( instance == null ){
            instance = new LCSDKHelper();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void init(){
        if ( context == null ){
            //TODO
            Log.e( TAG, "未传入context" );
            return;
        }
        AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.initialize(context, "d9gdw2cwdszg97mgwnys4a7hal9pykqcvgde8xzsmf1qybtm",
                "ktslkgc5rm1kwk5hft7n5kmzdmqbu0o8vebbfvct48ybg1xk");
    }
}
