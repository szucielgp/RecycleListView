package com.superlity.test.recyclelistviewtest.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.ytx.CCPSDKCoreHelper;
import com.superlity.test.recyclelistviewtest.imapi.ytx.Constant;

public class CallDialActivity extends AppCompatActivity {

    private EditText editOtherCount;
    private Button btnConfirm;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calldial);

        initView();
    }

    private void initView() {
        editOtherCount = (EditText) findViewById(R.id.otherCount);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CCPSDKCoreHelper.getInstance().logout();
                finish();
            }
        });
    }

    private void call() {
        String otherAcountText = editOtherCount.getText().toString();

        if (otherAcountText.length() < 0) {
            //TODO
            return;
        }

        Constant.conversitionInfo.setPartnerCount(otherAcountText);
        CCPSDKCoreHelper.getInstance().startCallVoice(otherAcountText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
