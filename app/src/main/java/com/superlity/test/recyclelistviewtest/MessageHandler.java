package com.superlity.test.recyclelistviewtest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;


class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
    private static AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler;
    private Context context;
    private String TAG = MessageHandler.this.getClass().getSimpleName();

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        Log.d(TAG, "消息已到达对方" + message.getContent());
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        if (client.getClientId().equals(MyApplication.getClientIdFromPre())) {
            if (activityMessageHandler != null) {
                // 正在聊天时，分发消息，刷新界面
                activityMessageHandler.onMessage(message, conversation, client);
            } else {
                if (message instanceof AVIMTextMessage) {
                    AVIMTextMessage textMessage = (AVIMTextMessage) message;
                }
            }
        } else {
            client.close(null);
        }
    }

    public static AVIMTypedMessageHandler<AVIMTypedMessage> getActivityMessageHandler() {
        return activityMessageHandler;
    }

    public static void setActivityMessageHandler(AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler) {
        MessageHandler.activityMessageHandler = activityMessageHandler;
    }
}
