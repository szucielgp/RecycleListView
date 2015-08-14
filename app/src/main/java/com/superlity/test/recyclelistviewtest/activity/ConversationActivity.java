package com.superlity.test.recyclelistviewtest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
//import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.superlity.test.recyclelistviewtest.ConversationType;
import com.superlity.test.recyclelistviewtest.MainActivity;
import com.superlity.test.recyclelistviewtest.MyApplication;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.controller.ChatManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConversationActivity extends BaseActivity implements View.OnClickListener {
  public static final String CONVERSATION_ID = "551a2847e4b04d688d73dc54";
  private static final String TAG = ConversationActivity.class.getSimpleName();


  private EditText otherIdEditText;
  private TextView client_id;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversation);
    otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);
    client_id = (TextView) findViewById(R.id.client_id);
    client_id.setText("您好："+ChatManager.getInstance().getSelfId());
    findViewById(R.id.logout).setOnClickListener(this);
    findViewById(R.id.chat_with_other).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.logout:
        finish();
        break;

      case R.id.chat_with_other:
        String otherId = otherIdEditText.getText().toString();
        if (!TextUtils.isEmpty(otherId)) {
          final ChatManager chatManager = ChatManager.getInstance();
          chatManager.fetchConversationWithUserId(otherId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation conversation, AVIMException e) {
              if (e != null) {
                Toast.makeText(ConversationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
              } else {
                //这里先注册一下这个会话
                chatManager.registerConversation(conversation);
                Intent intent = new Intent(ConversationActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.CONVID, conversation.getConversationId());
                startActivity(intent);
                toast("会话建立成功");
              }
            }
          });

        }
        break;
    }
  }



  @Override
  protected void onDestroy() {
    super.onDestroy();

  }
}
