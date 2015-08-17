package com.superlity.test.recyclelistviewtest.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.ytx.AccountSettings;
import com.superlity.test.recyclelistviewtest.ytx.CCPSDKCoreHelper;
import com.superlity.test.recyclelistviewtest.ytx.Constant;

public class YTXLoginActivity extends AppCompatActivity {

    private EditText editMyAccount;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ytx);

        initView();
    }

    private void initView() {
        editMyAccount = (EditText) findViewById(R.id.editMyAccount);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccount();
            }
        });
        if (AccountSettings.getInstance(this).getMyAccount().length() > 0 ) {
            btnLogin.setEnabled(false);
            editMyAccount.setText(AccountSettings.getInstance(this).getMyAccount());
            saveAccount();
        }
        else{
            btnLogin.setEnabled(true);
        }
    }

    private void saveAccount() {
        String account = editMyAccount.getText().toString();
        if (account.length() > 0) {
            AccountSettings.getInstance(this).setMyAccount(account);
            Constant.conversitionInfo.setSelfCount(account);
            CCPSDKCoreHelper.getInstance().init(new CCPSDKCoreHelper.OnInitFinishListener() {
                @Override
                public void onInitFinish() {
                    finish();
                }
            });
        } else {
//            UtilTools.toast("请输入账号");
            //TODO
            btnLogin.setEnabled(true);
        }
    }
}
