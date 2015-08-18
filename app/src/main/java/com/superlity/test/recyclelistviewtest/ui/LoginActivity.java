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
import com.superlity.test.recyclelistviewtest.imapi.ytx.CCPSDKCoreHelper;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;

public class LoginActivity extends BaseActivity {

    Toolbar toolbar;
    EditText clientIdEditText;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lc);

        initToolbar();

        clientIdEditText = (EditText) findViewById(R.id.client_id);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selfId = clientIdEditText.getText().toString();
                imLogin(selfId);
                voipLogin(selfId);
            }
        });
    }

    private void imLogin(String selfId) {
        if (!TextUtils.isEmpty(selfId)) {
            ChatManager.getInstance().login(selfId, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e != null) {
                        LogUtils.logException(e);
                    }
                    Intent intent = new Intent(LoginActivity.this, OtherAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void voipLogin(String selfId) {
        CCPSDKCoreHelper.getInstance().init(selfId, new CCPSDKCoreHelper.OnInitFinishListener() {
            @Override
            public void onInitSuccess() {
                //TODO 初始化成功
            }

            @Override
            public void onInitError() {
                //TODO 初始化失败
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        setTitle("Hi前辈");
    }
}
