package com.superlity.test.recyclelistviewtest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.leancloud.ChatManager;


public class ConversationActivity extends BaseActivity implements View.OnClickListener {

    private EditText otherIdEditText;
    private TextView client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);
        client_id = (TextView) findViewById(R.id.client_id);
        client_id.setText("您好：" + ChatManager.getInstance().getSelfId());
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
                                Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                                intent.putExtra(ChatActivity.CONVID, conversation.getConversationId());
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
