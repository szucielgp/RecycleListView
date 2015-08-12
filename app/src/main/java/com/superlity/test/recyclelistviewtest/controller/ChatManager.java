package com.superlity.test.recyclelistviewtest.controller;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.superlity.test.recyclelistviewtest.MyApplication;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatManager {
    private static ChatManager chatManager;
    private static Context context;

    public static synchronized ChatManager getInstance() {
        if (chatManager == null) {
            chatManager = new ChatManager();
        }
        return chatManager;
    }
    public static Context getContext() {
        return MyApplication.getInstance().getApplicationContext();
    }
    public void queryMessages(AVIMConversation conversation, final String msgId, long time, final int limit,
                              final AVIMTypedMessagesArrayCallback callback) {
        conversation.queryMessages(msgId, time, limit, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> imMessages, AVIMException e) {
                if (e != null) {
                    callback.done(Collections.EMPTY_LIST, e);
                } else {
                    List<AVIMTypedMessage> resultMessages = new ArrayList<>();
                    for (AVIMMessage msg : imMessages) {
                        if (msg instanceof AVIMTypedMessage) {
                            resultMessages.add((AVIMTypedMessage) msg);
                        } else {
                            LogUtils.i("unexpected message " + msg.getContent());
                        }
                    }
                    callback.done(resultMessages, null);
                }
            }




        });
    }
}
