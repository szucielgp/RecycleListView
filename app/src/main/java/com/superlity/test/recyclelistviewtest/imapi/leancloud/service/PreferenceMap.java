package com.superlity.test.recyclelistviewtest.imapi.leancloud.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.superlity.test.recyclelistviewtest.MyApplication;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.leancloud.entity.User;


/**
 * Created by lzw on 14-6-19.
 *
 */
public class PreferenceMap {
    public static final String NOTIFY_WHEN_NEWS = "notifyWhenNews";
    public static final String VOICE_NOTIFY = "voiceNotify";
    public static final String VIBRATE_NOTIFY = "vibrateNotify";

    public static PreferenceMap currentUserPreferenceMap;
    Context cxt;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public PreferenceMap(Context cxt) {
        this.cxt = cxt;
        pref = PreferenceManager.getDefaultSharedPreferences(cxt);
        editor = pref.edit();
        //Logger.d("PreferenceMap init no specific user");
    }

    public PreferenceMap(Context cxt, String prefName) {
        this.cxt = cxt;
        pref = cxt.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static PreferenceMap getCurUserPrefDao(Context ctx) {
        if (currentUserPreferenceMap == null) {
            currentUserPreferenceMap = new PreferenceMap(ctx, User.getCurrentUserId());
        }
        return currentUserPreferenceMap;
    }

    public boolean isNotifyWhenNews( Context context ) {
        return pref.getBoolean(NOTIFY_WHEN_NEWS,
                context.getResources().getBoolean(R.bool.defaultNotifyWhenNews));
    }

    boolean getBooleanByResId(Context context, int resId) {
        return context.getResources().getBoolean(resId);
    }

    public boolean isVoiceNotify( Context context ) {
        return pref.getBoolean(VOICE_NOTIFY,
                getBooleanByResId(context,R.bool.defaultVoiceNotify));
    }

    public boolean isVibrateNotify( Context context ) {
        return pref.getBoolean(VIBRATE_NOTIFY,
                getBooleanByResId(context,R.bool.defaultVibrateNotify));
    }
}
