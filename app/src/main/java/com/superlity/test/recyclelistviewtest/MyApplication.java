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

import com.superlity.test.recyclelistviewtest.imapi.leancloud.LCSDKHelper;
import com.superlity.test.recyclelistviewtest.imapi.ytx.CCPSDKCoreHelper;


public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LCSDKHelper.getInstance().init(this);
        CCPSDKCoreHelper.getInstance().setContext(this);
    }
}
