package com.superlity.test.recyclelistviewtest.imapi.leancloud.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.ui.EntrySplashActivity;
import com.superlity.test.recyclelistviewtest.imapi.leancloud.service.event.InvitationEvent;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;
import com.superlity.test.recyclelistviewtest.utils.NotificationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/7/10.
 * 接收推送广播消息Receiver
 */
public class MyReceiver extends BroadcastReceiver {

    public final static String AVOS_DATA = "com.avoscloud.Data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(">>>>>", action);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(context.getString(R.string.invitation_action))) {
                String avosData = intent.getStringExtra(AVOS_DATA);
                if (!TextUtils.isEmpty(avosData)) {
                    try {
                        JSONObject json = new JSONObject(avosData);
                        if (null != json) {
                            String alertStr = json.getString(PushManager.AVOS_ALERT);
                            NotificationUtil.showNotification(context, "LeanChat", alertStr, EntrySplashActivity.class);
                        }
                    } catch (JSONException e) {
                        LogUtils.logException(e);
                    }
                }
            }
        }
        EventBus.getDefault().post(new InvitationEvent());
    }
}