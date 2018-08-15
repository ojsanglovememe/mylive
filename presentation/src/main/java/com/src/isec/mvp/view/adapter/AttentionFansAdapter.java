package com.src.isec.mvp.view.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.config.GlideApp;
import com.src.isec.domain.entity.LiveEntity;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 关注/粉丝列表适配器
 * @time 2018/4/24 0010 10:00
 * @change
 * @chang time
 * @class describe
 */

public class AttentionFansAdapter extends BaseQuickAdapter<LiveEntity, BaseViewHolder> {

    public AttentionFansAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveEntity item) {
        helper.setText(R.id.tv_attention, item.isFollow() ? R.string.btn_attention_yes : R.string.btn_attention)
                .setText(R.id.tv_nick_name, item.getNickname())
                .setText(R.id.tv_id, mContext.getString(R.string.user_id_num, item.getId()))
                .addOnClickListener(R.id.tv_attention)
                .getView(R.id.tv_attention).setSelected(item.isFollow());

        GlideApp.with(mContext)
                .load(item.getCover())
                .transform(new CropCircleTransformation())
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_default)
                .into((ImageView) helper.getView(R.id.iv_avatar));
    }
}
