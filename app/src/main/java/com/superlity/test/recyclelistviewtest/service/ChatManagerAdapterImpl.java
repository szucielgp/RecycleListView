package com.superlity.test.recyclelistviewtest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.superlity.test.recyclelistviewtest.ui.ChatActivity;
import com.superlity.test.recyclelistviewtest.leancloud.ChatManagerAdapter;
import com.superlity.test.recyclelistviewtest.leancloud.MessageHelper;
import com.superlity.test.recyclelistviewtest.leancloud.entity.User;
import com.superlity.test.recyclelistviewtest.leancloud.entity.UserInfo;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;


import java.util.List;
import java.util.Random;

/**
 * Created by lzw on 15/5/13.
 */
public class ChatManagerAdapterImpl implements ChatManagerAdapter {
  private static final long NOTIFY_PERIOD = 1000;
  private static final int REPLY_NOTIFY_ID = 1;
  private static long lastNotifyTime = 0;
  private Context context;

  public ChatManagerAdapterImpl(Context context) {
    this.context = context;
  }

  @Override
  public UserInfo getUserInfoById(String userId) {
    AVUser user = CacheService.lookupUser(userId);
    if (user==null){
      return null;
    }else{
      UserInfo userInfo = new UserInfo();
      userInfo.setUsername(user.getUsername());
      userInfo.setAvatarUrl(User.getAvatarUrl(user));
      return userInfo;
    }
  }

  @Override
  public void cacheUserInfoByIdsInBackground(List<String> userIds) throws Exception {
    CacheService.cacheUsers(userIds);
  }

  @Override
  public void shouldShowNotification(final Context context, String selfId, final AVIMConversation conversation, final AVIMTypedMessage message) {
    if (showNotificationWhenNewMessageCome(selfId)) {
      new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
          try {
            CacheService.cacheUserIfNone(message.getFrom());
          } catch (Exception e) {
            LogUtils.logException(e);
          }
          return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
          showMessageNotification(context, conversation, message);
        }
      }.execute();
    }
  }

  private void showMessageNotification(Context context, AVIMConversation conv, AVIMTypedMessage msg) {
    if (System.currentTimeMillis() - lastNotifyTime < NOTIFY_PERIOD) {
      return;
    } else {
      lastNotifyTime = System.currentTimeMillis();
    }
    int icon = context.getApplicationInfo().icon;
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra(ChatActivity.CONVID, conv.getConversationId());

    PendingIntent pend = PendingIntent.getActivity(context, new Random().nextInt(),
        intent, 0);
    Notification.Builder builder = new Notification.Builder(context);
    CharSequence notifyContent = MessageHelper.outlineOfMsg(msg);
    CharSequence username = msg.getFrom();
    UserInfo from = getUserInfoById(msg.getFrom());
    if (from != null) {
      username = from.getUsername();
    }
    builder.setContentIntent(pend)
        .setSmallIcon(icon)
        .setWhen(System.currentTimeMillis())
        .setTicker(username + "\n" + notifyContent)
        .setContentTitle(username)
        .setContentText(notifyContent)
        .setAutoCancel(true);
    NotificationManager man = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = builder.getNotification();
    PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(this.context);
    if (preferenceMap.isVoiceNotify()) {
      notification.defaults |= Notification.DEFAULT_SOUND;
    }
    if (preferenceMap.isVibrateNotify()) {
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    }
    man.notify(REPLY_NOTIFY_ID, notification);
  }


  private boolean showNotificationWhenNewMessageCome(String selfId) {
    PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(context);
    return preferenceMap.isNotifyWhenNews();
  }


  public void cancelNotification() {
    NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    nMgr.cancel(REPLY_NOTIFY_ID);
  }
}
