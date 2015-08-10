package com.superlity.test.recyclelistviewtest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.superlity.test.recyclelistviewtest.activity.BaseActivity;
import com.superlity.test.recyclelistviewtest.activity.ConversationActivity;

public class LoginActivity extends BaseActivity {
  EditText clientIdEditText;
  Button login;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!TextUtils.isEmpty(MyApplication.getClientIdFromPre())) {
      openClient(MyApplication.getClientIdFromPre());
    }

    setContentView(R.layout.activity_login);
    clientIdEditText = (EditText) findViewById(R.id.client_id);
    login = (Button) findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                final String selfId = clientIdEditText.getText().toString();
                if (!TextUtils.isEmpty(selfId)) {
                  MyApplication.setClientIdToPre(selfId);
                  openClient(selfId);
                }
              }
            });
  }

  public void openClient(String selfId) {
    AVIMClient imClient = AVIMClient.getInstance(selfId);
    imClient.open(new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (filterException(e)) {
          Intent intent = new Intent(LoginActivity.this, ConversationActivity.class);
          startActivity(intent);
          finish();
        }
      }
    });
  }
}
