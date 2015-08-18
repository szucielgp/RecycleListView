package com.superlity.test.recyclelistviewtest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.leancloud.ChatManager;


public class OtherAccountActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText otherIdEditText;
    private TextView client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_account);
        otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);
        client_id = (TextView) findViewById(R.id.client_id);
        client_id.setText("您好：" + ChatManager.getInstance().getSelfId());
        findViewById(R.id.chat_with_other).setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("聊天");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_with_other:
                String otherId = otherIdEditText.getText().toString();
                if (!TextUtils.isEmpty(otherId)) {
                    final ChatManager chatManager = ChatManager.getInstance();
                    chatManager.fetchConversationWithUserId(otherId, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation conversation, AVIMException e) {
                            if (e != null) {
                                Toast.makeText(OtherAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                //这里先注册一下这个会话
                                chatManager.registerConversation(conversation);
                                Intent intent = new Intent(OtherAccountActivity.this, ChatActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ){
            case R.id.action_logout:
            case android.R.id.home:
                Intent intent = new Intent(OtherAccountActivity.this, LCLoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
