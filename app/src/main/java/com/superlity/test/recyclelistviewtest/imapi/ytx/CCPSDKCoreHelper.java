package com.superlity.test.recyclelistviewtest.imapi.ytx;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.superlity.test.recyclelistviewtest.ui.CallActivity;
import com.superlity.test.recyclelistviewtest.ui.CallDialActivity;
import com.superlity.test.recyclelistviewtest.ui.LCLoginActivity;
import com.superlity.test.recyclelistviewtest.utils.UtilTools;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * Created by lion on 2015/8/12 0012.
 * 云通信SDK核心控制类
 */
public class CCPSDKCoreHelper implements ECDevice.InitListener, ECDevice.OnECDeviceConnectListener, ECVoIPCallManager.OnVoIPListener {

    private static final String APPKEY = "aaf98f894f16fdb7014f1cbc4d5a0884";
    private static final String APPTOKEN = "a7e244c1f4e11380ec671d9d069fa6af";
    private static final int MSG_LOGIN_FINISH = 100001;

    private static CCPSDKCoreHelper instance;
    private Context context;
    private String currentCallId;
    private ECInitParams params;
    private OnInitFinishListener listener;

    public static CCPSDKCoreHelper getInstance() {
        if (instance == null) {
            instance = new CCPSDKCoreHelper();
        }
        return instance;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if ( msg.what == MSG_LOGIN_FINISH ){
                initFinish();
                return true;
            }
            return false;
        }
    });

    public void setContext(Context context) {
        this.context = context;
    }

    public void init(OnInitFinishListener listener) {
        uninit();
        this.listener = listener;
        if (context == null) {
            //TODO
            return;
        }

        if (!ECDevice.isInitialized()) {
            ECDevice.initial(context, this);
        }
    }

    /**
     * @ECDevice.initial 初始化完成后会回调该函数
     */
    @Override
    public void onInitialized() {
        //TODO

        if (params == null || params.getInitParams() == null || params.getInitParams().isEmpty()) {
            params = new ECInitParams();
        }
        params.reset();
        params.setUserid(Constant.conversitionInfo.getSelfCount()); //这里需要设置用户的ID，用于与服务器保持长链接
        params.setAppKey(APPKEY); // 设置应用的APPKey
        params.setToken(APPTOKEN); // 设置应用的APPID
        params.setMode(ECInitParams.LoginMode.FORCE_LOGIN); // 设置登录模式，如果用户使用同一账号登陆则挤掉之前登陆的地方
        params.setOnDeviceConnectListener(this);

        initVoip();

        // 设置接收VoIP来电事件通知Intent
        // 呼入界面activity、开发者需修改该类
        Intent intent = new Intent(context, CallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        params.setPendingIntent(pendingIntent);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                ECDevice.login(params);
//                handler.sendEmptyMessage(MSG_LOGIN_FINISH);
//                Looper.loop();
//            }
//        }).start();
        ECDevice.login(params);
        initFinish();
    }

    /**
     * 初始化成功</br>
     * 首先跳转页面，回调初始化完成事件
     */
    private void initFinish(){
        Intent callIntent = new Intent(context, CallDialActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(callIntent);

        if (this.listener != null) {
            this.listener.onInitFinish();
        }
    }

    @Override
    public void onError(Exception e) {
        //TODO
    }

    @Override
    public void onConnect() {
        // 兼容4.0，5.0可不必处理
    }

    @Override
    public void onDisconnect(ECError ecError) {
        // 兼容4.0，5.0可不必处理
    }

    @Override
    public void onConnectState(ECDevice.ECConnectState state, ECError error) {
        if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
            if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                // 异地登陆
            } else {
                // 链接状态失败
            }
//            UtilTools.toast("登陆失败");
            //TODO
        } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            // 登陆成功
//            UtilTools.toast("登陆成功");
            //TODO
        }
    }

    private void initVoip() {
        ECVoIPCallManager callInterface = ECDevice.getECVoIPCallManager();
        if (callInterface != null) {
            callInterface.setOnVoIPCallListener(this);
        }
    }

    @Override
    public void onCallEvents(final ECVoIPCallManager.VoIPCall voipCall) {
//        UtilTools.toast("onCallEvents");
        //TODO
        // 处理呼叫事件回调
        if (voipCall == null) {
            Log.e("SDKCoreHelper", "handle call event error , voipCall null");
            return;
        }
        // 根据不同的事件通知类型来处理不同的业务
        ECVoIPCallManager.ECCallState callState = voipCall.callState;
        switch (callState) {
            case ECCALL_PROCEEDING:
                // 正在连接服务器处理呼叫请求
                UtilTools.toast("正在连接服务器处理呼叫请求");
                //TODO
                break;
            case ECCALL_ALERTING:
                // 呼叫到达对方客户端，对方正在振铃
                UtilTools.toast("呼叫到达对方客户端，对方正在振铃");
                //TODO
                break;
            case ECCALL_ANSWERED:
                // 对方接听本次呼叫
                UtilTools.toast("对方接听本次呼叫");
                //TODO
                break;
            case ECCALL_FAILED:
                // 本次呼叫失败，根据失败原因播放提示音
                UtilTools.toast("本次呼叫失败，根据失败原因播放提示音");
                UtilTools.toast(voipCall.reason + "");
                //TODO
                break;
            case ECCALL_RELEASED:
                // 通话释放[完成一次呼叫]
                UtilTools.toast("通话释放[完成一次呼叫]");
                //TODO
                break;
            default:
                Log.e("SDKCoreHelper", "handle call event error , callState " + callState);
                break;
        }
    }

    /**
     * 拨号
     * @param acount 对方的账号
     */
    public void startCallVoice(final String acount) {
        if (acount == null) {
//            UtilTools.toast("对方账号为空");
            //TODO
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentCallId = ECDevice.getECVoIPCallManager().makeCall(ECVoIPCallManager.CallType.VOICE, acount);
            }
        }).start();
    }

    /**
     * 接通语音通话
     * @param callid
     */
    public void acceptCallVoice( String callid ) {
        ECDevice.getECVoIPCallManager().acceptCall(callid);
    }

    /**
     * 结束语音通话
     */
    public void endCallVoice() {
        if (currentCallId != null) {
            ECDevice.getECVoIPCallManager().releaseCall(currentCallId);
        }
    }

    /**
     * 反初始化
     */
    public void uninit() {
        ECDevice.unInitial();
    }

    /**
     * 退出登录
     */
    public void logout() {
        AccountSettings.getInstance(context).setMyAccount("");
        ECDevice.logout(new ECDevice.OnLogoutListener() {
            @Override
            public void onLogout() {
                Intent intent = new Intent(context, LCLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 初始化完成监听器
     */
    public interface OnInitFinishListener {
        void onInitFinish();
    }
}
