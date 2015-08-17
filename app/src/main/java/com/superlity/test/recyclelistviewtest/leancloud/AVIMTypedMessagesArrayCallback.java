package com.superlity.test.recyclelistviewtest.leancloud;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import java.util.List;

public interface AVIMTypedMessagesArrayCallback {
  void done(List<AVIMTypedMessage> typedMessages, AVException e);
}