package com.superlity.test.recyclelistviewtest.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.ytx.CCPSDKCoreHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

public class CallActivity extends AppCompatActivity {

    /**
     * 呼入方或者呼出方
     */
    public static final String EXTRA_OUTGOING_CALL = "con.yuntongxun.ecdemo.VoIP_OUTGOING_CALL";

    private TextView textPartnetAccount;
    private Button btnAccept;
    private Button btnRefuse;
    private String callId;
    private boolean isCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        getPartnerData();
        initView();
    }

    private void getPartnerData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        //获取是否是呼入还是呼出
        boolean mIncomingCall = !(getIntent().getBooleanExtra(EXTRA_OUTGOING_CALL, false));
        //获取是否是音频还是视频
        Object mCallType = (ECVoIPCallManager.CallType) getIntent().getSerializableExtra(ECDevice.CALLTYPE);
        //获取当前的callid
        callId = getIntent().getStringExtra(ECDevice.CALLID);
        //获取对方的号码
        Object mCallNumber = getIntent().getStringExtra(ECDevice.CALLER);

        String type = getIntent().getStringExtra("type");
        isCall = type != null;
    }

    private void initView() {
        textPartnetAccount = (TextView) findViewById(R.id.textParnetAcconut);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnRefuse = (Button) findViewById(R.id.btnRefuse);

        if (isCall) {
            btnAccept.setVisibility(View.GONE);
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callId != null) {
                    CCPSDKCoreHelper.getInstance().acceptCallVoice(callId);
                }
            }
        });

        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CCPSDKCoreHelper.getInstance().endCallVoice();
                finish();
            }
        });
    }
}
