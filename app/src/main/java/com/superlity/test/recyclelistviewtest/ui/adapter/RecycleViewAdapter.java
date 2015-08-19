package com.superlity.test.recyclelistviewtest.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.superlity.test.recyclelistviewtest.R;
import com.superlity.test.recyclelistviewtest.imapi.leancloud.MessageHelper;
import com.superlity.test.recyclelistviewtest.ui.ImageBrowserActivity;
import com.superlity.test.recyclelistviewtest.ui.emoji.EmojiParser;
import com.superlity.test.recyclelistviewtest.ui.emoji.ParseEmojiMsgUtil;
import com.superlity.test.recyclelistviewtest.utils.PathUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/17.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private Context context;
    private List<AVIMTypedMessage> messageList = new ArrayList<AVIMTypedMessage>();
    private static PrettyTime prettyTime = new PrettyTime();

    private enum MsgViewType {
        ComeText(0), ToText(1), ComeImage(2), ToImage(3);
        int value;

        MsgViewType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public RecycleViewAdapter(Context context) {
        this.context = context;
    }

    public RecycleViewAdapter(Context context, List<AVIMTypedMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }


    public void setMessageList(List<AVIMTypedMessage> messageList) {
        this.messageList = messageList;
    }

    public List<AVIMTypedMessage> getMessageList() {
        return messageList;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void add(AVIMTypedMessage message) {
        messageList.add(message);
        notifyItemRangeInserted(messageList.size(), 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == MsgViewType.ComeText.getValue()) {
            View view = View.inflate(parent.getContext(), R.layout.left_cell_layout, null);
            return new ViewHolder(view, 0);
        } else if (viewType == MsgViewType.ToText.getValue()) {
            View view = View.inflate(parent.getContext(), R.layout.right_cell_layout, null);
            return new ViewHolder(view, 1);
        } else if (viewType == MsgViewType.ComeImage.getValue()) {//加载发送方的图片
            View view = View.inflate(parent.getContext(), R.layout.left_imagecell_layout, null);
            return new ViewHolder(view, 2);
        } else if (viewType == MsgViewType.ToImage.getValue()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_imagecell_layout, parent, false);
            return new ViewHolder(view, 3);
        } else if (viewType == 4) {//加载消息提醒
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_remindcell_layout, parent, false);//false可以控制在中间显示
            return new ViewHolder(view, 4);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                initMessageView(position, holder);
                break;
            case 1:
                initMessageView(position, holder);
                break;
            case 2:
                final AVIMImageMessage imageMsg = (AVIMImageMessage) messageList.get(position);
                Uri uri = Uri.parse(PathUtils.getChatFilePath(imageMsg.getMessageId()));
                holder.mImageView.setImageURI(uri);
                holder.mImageView.setLayoutParams(calculateImageSize(imageMsg, holder.mImageView));
                if (position == 0 || haveTimeGap(messageList.get(position - 1).getTimestamp(),
                        imageMsg.getTimestamp())) {
                    holder.tTextView.setVisibility(View.VISIBLE);
                    holder.tTextView.setText(millisecsToDateString(imageMsg.getTimestamp()));
                } else {
                    holder.tTextView.setVisibility(View.GONE);
                }
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageBrowserActivity.go(context, PathUtils.getChatFilePath(imageMsg.getMessageId()), imageMsg.getFileUrl());
                    }
                });
                break;
            case 3:
                final AVIMImageMessage image = (AVIMImageMessage) messageList.get(position);
                Uri urito = Uri.parse(PathUtils.getChatFilePath(image.getMessageId()));
                holder.mImageView.setImageURI(urito);
                holder.mImageView.setLayoutParams(calculateImageSize(image, holder.mImageView));
                if (position == 0 || haveTimeGap(messageList.get(position - 1).getTimestamp(),
                        image.getTimestamp())) {
                    holder.tTextView.setVisibility(View.VISIBLE);
                    holder.tTextView.setText(millisecsToDateString(image.getTimestamp()));
                } else {
                    holder.tTextView.setVisibility(View.GONE);
                }
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageBrowserActivity.go(context, PathUtils.getChatFilePath(image.getMessageId()), image.getFileUrl());
                    }
                });
                break;
            default:
                initMessageView(position, holder);
                break;
        }
    }

    private RelativeLayout.LayoutParams calculateImageSize(AVIMImageMessage image, SimpleDraweeView view) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        int width = image.getWidth();
        int height = image.getHeight();
        int min = dp2px(context, 300);
        int max = dp2px(context, 500);

        float scale = 1;

        if (width > height) {
            if (width < min) {
                scale = min / (float) width;
            } else if (width > max) {
                scale = max / (float) width;
            }
        } else {
            if (height < min) {
                scale = min / (float) height;
            } else if (height > max) {
                scale = max / (float) height;
            }
        }

        System.out.println("scale = " + scale);
        System.out.println("width = " + width);
        System.out.println("height = " + height);
        System.out.println("min = " + min);
        System.out.println("max = " + max);

        layoutParams.width = (int) (width * scale);
        layoutParams.height = (int) (height * scale);

        System.out.println("layoutParams.width = " + layoutParams.width);
        System.out.println("layoutParams.height = " + layoutParams.height);

        return layoutParams;
    }

    private static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void initMessageView(int position, RecycleViewAdapter.ViewHolder holder) {

        final AVIMTextMessage msg = (AVIMTextMessage) messageList.get(position);
        String unicode = EmojiParser.getInstance(context).parseEmoji(msg.getText());
        SpannableString spannableString = ParseEmojiMsgUtil.getExpressionString(context, unicode);
        holder.mTextView.setText(spannableString);
        if (position == 0 || haveTimeGap(messageList.get(position - 1).getTimestamp(),
                msg.getTimestamp())) {
            holder.tTextView.setVisibility(View.VISIBLE);
            holder.tTextView.setText(millisecsToDateString(msg.getTimestamp()));
        } else {
            holder.tTextView.setVisibility(View.GONE);
        }
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView tTextView;
        public ImageView mImageView2;
        public SimpleDraweeView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tv1);
        }

        public ViewHolder(View v, int ViewType) {
            super(v);
            if (ViewType == 0) {
                mTextView = (TextView) v.findViewById(R.id.tv1);
                tTextView = (TextView) v.findViewById(R.id.time);
            } else if (ViewType == 1) {
                mTextView = (TextView) v.findViewById(R.id.tv1);
                tTextView = (TextView) v.findViewById(R.id.time);
            } else if (ViewType == 2) {
                mImageView = (SimpleDraweeView) v.findViewById(R.id.iv1);
                tTextView = (TextView) v.findViewById(R.id.time);
            } else if (ViewType == 3) {
                mImageView = (SimpleDraweeView) v.findViewById(R.id.iv1);
                tTextView = (TextView) v.findViewById(R.id.time);
            }
        }
    }

    //判断消息时候来自自己
    boolean isComeMsg(AVIMTypedMessage msg) {
        // return !msg.getFrom().equals(ChatManager.getSelfId());
        return !MessageHelper.fromMe(msg);
    }

    @Override
    public int getItemViewType(int position) {
        AVIMTypedMessage msg = messageList.get(position);
        boolean comeMsg = isComeMsg(msg);

        MsgViewType viewType;
        AVIMReservedMessageType msgType = AVIMReservedMessageType.getAVIMReservedMessageType(msg.getMessageType());
        switch (msgType) {
            case TextMessageType:
                viewType = comeMsg ? MsgViewType.ComeText : MsgViewType.ToText;
                break;
            case ImageMessageType:
                viewType = comeMsg ? MsgViewType.ComeImage : MsgViewType.ToImage;
                break;
            default:
                viewType = comeMsg ? MsgViewType.ComeText : MsgViewType.ToText;
                break;
        }
        return viewType.getValue();
    }


    //控制时间部分
    // time
    public static String millisecsToDateString(long timestamp) {
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = prettyTime.format(new Date(timestamp));
            //return s.replace(" ", "");
            return s;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            return format.format(new Date(timestamp));
        }
    }

    public static boolean haveTimeGap(long lastTime, long time) {
        int gap = 1000 * 60 * 3;
        return time - lastTime > gap;
    }

}
