package com.superlity.test.recyclelistviewtest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.leancloud.ChatManager;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;

public class LCLoginActivity extends BaseActivity {

    Toolbar toolbar;
    EditText clientIdEditText;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        clientIdEditText = (EditText) findViewById(R.id.client_id);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String selfId = clientIdEditText.getText().toString();
                if (!TextUtils.isEmpty(selfId)) {
                    ChatManager.getInstance().login(selfId, new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if (e != null) {
                                LogUtils.logException(e);
                            }
                            Intent intent = new Intent(LCLoginActivity.this, OtherAccountActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon( null );
        setTitle("Hi前辈" );
    }


}
