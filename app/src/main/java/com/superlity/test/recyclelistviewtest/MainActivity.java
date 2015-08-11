package com.superlity.test.recyclelistviewtest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.superlity.test.recyclelistviewtest.controller.AVIMTypedMessagesArrayCallback;
import com.superlity.test.recyclelistviewtest.controller.ChatManager;
import com.superlity.test.recyclelistviewtest.emoji.ParseEmojiMsgUtil;
import com.superlity.test.recyclelistviewtest.emoji.SelectFaceHelper;
import com.superlity.test.recyclelistviewtest.resize.AutoHeightLayout;
import com.superlity.test.recyclelistviewtest.resize.Utils;
import com.superlity.test.recyclelistviewtest.utils.LogUtils;
import com.superlity.test.recyclelistviewtest.utils.PathUtils;
import com.superlity.test.recyclelistviewtest.utils.PhotoUtils;
import com.superlity.test.recyclelistviewtest.utils.ProviderPathUtils;


import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends Activity {
    private RecyclerView rec;
    private EditText  msgcontent ;
    private ImageView sendButton;
    private ImageView emojiButton;
    private ImageView attchButton;
    private  PopupMenu popup;
    private RecycleViewAdapter adapter;
    public List<AVIMTypedMessage> messageList;
    private  String content_str;
    public  LinearLayoutManager layoutManager;
    public InputMethodManager imm;
    public PopupWindow pop;
    private View emojiView;
    public AutoHeightLayout resizelayout;
    public SwipeRefreshLayout swipeRefreshLayout;
    //消息部分的定义
    private SelectFaceHelper mFaceHelper;
    /**选择图片拍照路径*/
    public String localCameraPath = PathUtils.getPicturePathByCurrentTime();
    private static final int TAKE_CAMERA_REQUEST = 2;
    private static final int  TAKE_CROP_PHOTO = 1;
    private static final int GALLERY_REQUEST = 0;
    private static final int GALLERY_KITKAT_REQUEST = 3;

//会话ID
    private AVIMConversation conversation;
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String TAG = MainActivity.class.getSimpleName();
    private ChatHandler handler;
    static final int PAGE_SIZE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiConversation();
        intiView();
        loadMessagesWhenInit(PAGE_SIZE);
    }

    private void intiConversation() {
        final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        Log.d(TAG, "会话 id: " + conversationId);
        handler = new ChatHandler();
        MessageHandler.setActivityMessageHandler(handler);
        conversation = MyApplication.getIMClient().getConversation(conversationId);
    }

    private void intiView(){
        messageList = new ArrayList<>() ;
        rec = (RecyclerView)findViewById(R.id.listview);
        msgcontent = (EditText) findViewById(R.id.msg_text);
        sendButton = (ImageView) findViewById(R.id.send_btn);
        emojiButton = (ImageView) findViewById(R.id.msg_smile);
        attchButton = (ImageView) findViewById(R.id.attach);
        emojiView = (View) findViewById(R.id.emoji);
        resizelayout = (AutoHeightLayout) findViewById(R.id.root);
        resizelayout.setAutoHeightLayoutView(emojiView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //对RecycleView的处理
        layoutManager = new LinearLayoutManager(this);
        layoutManager.canScrollVertically();
        rec.setLayoutManager(layoutManager);
        //设置Item间距
        rec.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 10, 10);
            }
        });

        rec.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING://The RecyclerView is currently being dragged by outside input such as user touch input.
                        resizelayout.hideAutoView();
                        emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        adapter = new RecycleViewAdapter(MainActivity.this);
        rec.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                loadOldMessages();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        msgcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkSendButton(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        msgcontent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null == mFaceHelper) {
                    mFaceHelper = new SelectFaceHelper(MainActivity.this, emojiView);
                    mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
                }
                if (!msgcontent.isFocused()) {
                    msgcontent.setFocusable(true);
                    msgcontent.setFocusableInTouchMode(true);
                }
                if(adapter.getItemCount()!=0){
                    rec.smoothScrollToPosition(adapter.getItemCount());
                }
                return false;
            }
        });
        msgcontent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(getTrimmedString(msgcontent.getText().toString()))) {
                    Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String msgStr = ParseEmojiMsgUtil.convertToMsg(msgcontent.getText(), MainActivity.this);// 这里不要直接用mEditMessageEt.getText().toString();
                sendText(msgStr);
                checkSendButton(false);
                msgcontent.setText("");
            }
        });

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mFaceHelper) {
                    mFaceHelper = new SelectFaceHelper(MainActivity.this, emojiView);
                    mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
                }

                switch (resizelayout.mKeyboardState) {
                    case AutoHeightLayout.KEYBOARD_STATE_NONE:
                    case AutoHeightLayout.KEYBOARD_STATE_BOTH:
                        emojiButton.setImageResource(R.drawable.ic_msg_panel_smiles);
                        resizelayout.showAutoView();
                        Utils.closeSoftKeyboard(MainActivity.this);
                        if(adapter.getItemCount()!=0){
                            rec.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                        break;
                    case AutoHeightLayout.KEYBOARD_STATE_FUNC:
                        Utils.openSoftKeyboard(msgcontent);
                        emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
                        if(adapter.getItemCount()!=0){
                            rec.smoothScrollToPosition(adapter.getItemCount()-1);
                        }

                        break;
                }

            }
        });

        //对附加按钮的处理
        attchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onPopupButtonClick(attchButton);
            }
        });
    }

    //跳转到此页面来的方法
    public static void startActivity(Context context, String conversationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }



//初始化一开始的消息
   public void loadMessagesWhenInit(int limit) {
        ChatManager.getInstance().queryMessages(conversation, null, System.currentTimeMillis(), limit, new
                AVIMTypedMessagesArrayCallback() {
                    @Override
                    public void done(final List<AVIMTypedMessage> typedMessages, AVException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            new CacheMessagesTask(MainActivity.this, typedMessages) {
                                @Override
                                void onSucceed(List<AVIMTypedMessage> messages) {
                                    adapter.setMessageList(typedMessages);
                                    adapter.notifyDataSetChanged();
                                    //adapter.notifyItemRangeInserted(0, messages.size());
                                    if (adapter.getItemCount() != 0) {
                                        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                                    }
                                }
                            }.execute();
                        }
                    }
                });
    }
//刷新之前的消息
    public void loadOldMessages() {
        if (adapter.getMessageList().size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            AVIMTypedMessage firstMsg = adapter.getMessageList().get(0);
            String msgId = firstMsg.getMessageId();
            long time = firstMsg.getTimestamp();
            ChatManager.getInstance().queryMessages(conversation, msgId, time, PAGE_SIZE, new AVIMTypedMessagesArrayCallback() {
                @Override
                public void done(List<AVIMTypedMessage> typedMessages, AVException e) {
                    if (e!=null) {
                       e.printStackTrace();
                    }
                    else {
                        new CacheMessagesTask(MainActivity.this, typedMessages) {
                            @Override
                            void onSucceed(List<AVIMTypedMessage> typedMessages) {

                                List<AVIMTypedMessage> newMessages = new ArrayList<>(PAGE_SIZE);
                                newMessages.addAll(typedMessages);
                                newMessages.addAll(adapter.getMessageList());
                                adapter.setMessageList(newMessages);
                                adapter.notifyDataSetChanged();
                                if(typedMessages.size()==0){
                                    Toast.makeText(MainActivity.this,R.string.chat_activity_loadMessagesFinish,Toast.LENGTH_SHORT).show();
                                }
//                                else if (typedMessages.size()>0) {
//                                 //   rec.scrollToPosition();
//                                }
                            }
                        }.execute();
                    }

                }
            });
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //去除字符串
    private String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    //检查是否有内容
    public boolean hasText() {
        return msgcontent != null && msgcontent.length() > 0;
    }

    //控制发送按钮是否显示
    private void checkSendButton(boolean issend){
        String message = getTrimmedString(msgcontent.getText().toString());
        if(message.length()>0 && issend){
            attchButton.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        }
        else{
            attchButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
        }
    }

    //附件弹出的菜单项
    public void onPopupButtonClick(final View button)
    {

        popup = new PopupMenu(this, button);
        // 将R.menu.popup_menu菜单资源加载到popup菜单中
        getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.take_photo:
                                Toast.makeText(MainActivity.this,
                                        "您单击了【" + item.getTitle() + "】菜单项"
                                        , Toast.LENGTH_SHORT).show();
                                selectImageFromCamera();
                                break;
                            case R.id.gallery:
                                Toast.makeText(MainActivity.this,
                                        "您单击了【" + item.getTitle() + "】菜单项"
                                        , Toast.LENGTH_SHORT).show();

                                selectImageFromLocal();
                                break;
                            case R.id.file:
                                Toast.makeText(MainActivity.this,
                                        "您单击了【" + item.getTitle() + "】菜单项"
                                        , Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.exit:
                                popup.dismiss();
                                break;

                            default:
                                Toast.makeText(MainActivity.this,
                                        "您单击了【" + item.getTitle() + "】菜单项"
                                        , Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
        popup.show();
    }




    SelectFaceHelper.OnFaceOprateListener mOnFaceOprateListener = new SelectFaceHelper.OnFaceOprateListener() {
        @Override
        public void onFaceSelected(SpannableString spanEmojiStr) {
            if (null != spanEmojiStr) {
                msgcontent.append(spanEmojiStr);
            }
            if ( msgcontent != null) {
                msgcontent.setFocusable(true);
                msgcontent.setFocusableInTouchMode(true);
                msgcontent.requestFocus();
            }
        }
        @Override
        public void onFaceDeleted() {

            int selection = msgcontent.getSelectionStart();
            String text = msgcontent.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    msgcontent.getText().delete(start, end);
                    return;
                }
                msgcontent.getText().delete(selection - 1, selection);
            }

        }
    };

    //对返回键的处理
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (emojiView != null && emojiView.isShown()) {
                    resizelayout.hideAutoView();
                    emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }
//对edittext是否获得焦点的判断
    private void setEditableState(boolean b) {
        if (b) {
            msgcontent.setFocusable(true);
            msgcontent.setFocusableInTouchMode(true);
            msgcontent.requestFocus();
        } else {
            msgcontent.setFocusable(false);
            msgcontent.setFocusableInTouchMode(false);
        }
    }




    public void selectImageFromLocal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.chat_activity_select_picture)),
                    GALLERY_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_KITKAT_REQUEST);
        }
    }
    public void selectImageFromCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri imageUri = Uri.fromFile(new File(localCameraPath));
        takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                case GALLERY_KITKAT_REQUEST:
                    if (intent == null) {
                        return;
                    }
                    Uri uri;
                    if (requestCode == GALLERY_REQUEST) {
                        uri = intent.getData();
                    } else {
                        //for Android 4.4
                        uri = intent.getData();
                        final int takeFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    }
                    String localSelectPath = ProviderPathUtils.getPath(this, uri);
                    sendImage(localSelectPath);
                    break;
                case TAKE_CAMERA_REQUEST:
                   // Intent data  = new Intent("com.android.camera.action.CROP");

                    sendImage(localCameraPath);
                    break;
                case TAKE_CROP_PHOTO:


            }
        }

    }

    public void sendText(String string) {
        final AVIMTextMessage message = new AVIMTextMessage();
        message.setText(string);
        conversation.sendMessage(message, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (null != e) {
                    e.printStackTrace();
                } else {
                    adapter.add(message);//要在
                    if (adapter.getItemCount() != 0) {
                        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    }
                    finishSend();
                }
            }
        });
    }

    public void sendImage(String imagePath){
        final String newPath = PathUtils.getChatFilePath(com.superlity.test.recyclelistviewtest.utils.Utils.uuid());
        PhotoUtils.compressImage(imagePath, newPath);
        try {
            final AVIMImageMessage imageMsg = new AVIMImageMessage(newPath);
            conversation.sendMessage(imageMsg, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {

                @Override
                public void done(AVIMException e) {
                    if (e == null && newPath != null) {
                        File tmpFile = new File(newPath);
                        File newFile = new File(PathUtils.getChatFilePath(imageMsg.getMessageId()));
                        boolean result = tmpFile.renameTo(newFile);
                        if (!result) {
                            LogUtils.i("move file failed, can't use local cache");
                        }
                        adapter.add(imageMsg);
                        if (adapter.getItemCount() != 0) {
                            layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                        }
                        finishSend();
                    }else if(e!=null){
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            LogUtils.logException(e);
        }
    }

    public void finishSend() {
        Toast.makeText(MainActivity.this,"send success!",Toast.LENGTH_SHORT).show();
    }

    public class ChatHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

        @Override
        public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
            if (message instanceof AVIMTextMessage) {
                if (conversation.getConversationId().equals(MainActivity.this.conversation.getConversationId())) {
                    String msgStr = ParseEmojiMsgUtil.convertToMsg( ((AVIMTextMessage) message).getText(), MainActivity.this);
                    ((AVIMTextMessage) message).setText(msgStr);
                    adapter.add(message);
                    if(adapter.getItemCount()!=0){
                        rec.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                }
            }else if(message instanceof AVIMImageMessage){
                if(conversation.getConversationId().equals(MainActivity.this.conversation.getConversationId())){
                    adapter.add(message);
                    if(adapter.getItemCount()!=0){
                        rec.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                }
            }
        }
    }
    //异步任务来加载信息
    public abstract class CacheMessagesTask extends AsyncTask<Void, Void, Void> {
        private List<AVIMTypedMessage> messages;
        private volatile Exception e;

        public CacheMessagesTask(Context context, List<AVIMTypedMessage> messages) {
            this.messages = messages;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Set<String> userIds = new HashSet<>();
            for (AVIMTypedMessage msg : messages) {
                AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(msg.getMessageType());
                userIds.add(msg.getFrom());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (e!=null) {
                e.printStackTrace();
            }else{
                onSucceed(messages);
            }
        }
        abstract void onSucceed(List<AVIMTypedMessage> messages);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVIMMessageManager.unregisterMessageHandler(AVIMTypedMessage.class, handler);
    }
}
