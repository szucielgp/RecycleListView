package com.superlity.test.recyclelistviewtest.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.superlity.test.recyclelistviewtest.ConversationType;
import com.superlity.test.recyclelistviewtest.MainActivity;
import com.superlity.test.recyclelistviewtest.MyApplication;
import com.superlity.test.recyclelistviewtest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConversationActivity extends BaseActivity implements View.OnClickListener {
  public static final String CONVERSATION_ID = "551a2847e4b04d688d73dc54";
  private static final String TAG = ConversationActivity.class.getSimpleName();


  private TextView clientIdTextView;
  private EditText otherIdEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversation);


    clientIdTextView = (TextView) findViewById(R.id.client_id);
    otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);

    clientIdTextView.setText(getString(R.string.welcome) + " "+ MyApplication.getClientIdFromPre());

    findViewById(R.id.join_conversation).setOnClickListener(this);

    findViewById(R.id.logout).setOnClickListener(this);
    findViewById(R.id.chat_with_other).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.logout:
        MyApplication.setClientIdToPre("");
        finish();
        break;

      case R.id.chat_with_other:
        String otherId = otherIdEditText.getText().toString();
        if (!TextUtils.isEmpty(otherId)) {
          fetchConversationWithClientIds(Arrays.asList(otherId), ConversationType.OneToOne, new
              AVIMConversationCreatedCallback
                  () {
                @Override
                public void done(AVIMConversation avimConversation, AVIMException e) {
                  if (filterException(e)) {
                     MainActivity.startActivity(ConversationActivity.this, avimConversation.getConversationId());
                    toast("会话建立成功");
                  }
                }
              });
        }
        break;
    }
  }

  private void fetchConversationWithClientIds(List<String> clientIds, final ConversationType type, final
  AVIMConversationCreatedCallback
      callback) {
    final AVIMClient imClient = MyApplication.getIMClient();
    final List<String> queryClientIds = new ArrayList<String>();
    queryClientIds.addAll(clientIds);
    if (!clientIds.contains(imClient.getClientId())) {
      queryClientIds.add(imClient.getClientId());
    }
    AVIMConversationQuery query = imClient.getQuery();
    query.whereEqualTo(Conversation.ATTRIBUTE_MORE + ".type", type.getValue());
    query.whereContainsAll(Conversation.COLUMN_MEMBERS, queryClientIds);
    query.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVIMException e) {
        if (e != null) {
          callback.done(null, e);
        } else {
          if (list == null || list.size() == 0) {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put(ConversationType.KEY_ATTRIBUTE_TYPE, type.getValue());
            imClient.createConversation(queryClientIds, attributes, callback);
          } else {
            callback.done(list.get(0), null);
          }
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MyApplication.getIMClient().close(new AVIMClientCallback() {

      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (e == null) {
          Log.d(TAG, "退出连接");
        } else {
          Toast.makeText(ConversationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }
}
