package com.src.isec.mvp.view.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.domain.entity.ChatEntity;

import java.util.List;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 直播间聊天列表适配器
 * @time 2018/4/16 11:25
 * @change
 * @chang time
 * @class describe
 */

public class ChatMsgListAdapter extends BaseQuickAdapter<ChatEntity,BaseViewHolder> {

    RecyclerView mChatRecycleView;

    public ChatMsgListAdapter(int layoutResId, @Nullable List<ChatEntity> data, RecyclerView mChatRecycleView) {
        super(layoutResId, data);
        this.mChatRecycleView = mChatRecycleView;
    }

    /**
     * 添加消息
     * @param entity
     */
    public void addMsg(ChatEntity entity){
        if(mData.size()>1000){
            while (mData.size()>900){
                mData.remove(0);
            }
        }
        mData.add(entity);
        if(mData.size()<6){
            int length = mData.size();
            ChatEntity emptyEntity = new ChatEntity();
            emptyEntity.setMsgText("");
            for(int i = 0;i<6-length;i++){
                mData.add(0,emptyEntity);
            }
        }
        notifyDataSetChanged();
        ((LinearLayoutManager) mChatRecycleView.getLayoutManager()).scrollToPositionWithOffset(mData.size() - 1, 0);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatEntity item) {
        TextView textView = helper.getView(R.id.tv_chat_text);
        if(item.getMsgText().equals("")){
            helper.setText(R.id.tv_chat_text,"");
            textView.setVisibility(View.INVISIBLE);
        }else {
            SpannableString spanString = new SpannableString(item.getMsgSendName() + " " + item.getMsgText());

            spanString.setSpan(new ForegroundColorSpan(cacleNameColor(item.getMsgSendLevel())), 0, item.getMsgSendName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            helper.setText(R.id.tv_chat_text, spanString);
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据用户级别设置名字颜色
     * @param level
     * @return
     */
    private int cacleNameColor(int level){
        return mContext.getResources().getColor(R.color.color_send_name);
    }
}
