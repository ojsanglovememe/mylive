package com.src.isec.mvp.view.adapter;


import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.config.GlideApp;
import com.src.isec.domain.entity.LiveEntity;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 首页关注直播列表适配器
 * @time 2018/4/10 0010 11:40
 * @change
 * @chang time
 * @class describe
 */

public class HomeAttentionAdapter extends BaseQuickAdapter<LiveEntity, BaseViewHolder> {

    Fragment mFragment;
    public HomeAttentionAdapter(Fragment fragment, int layoutResId) {
        super(layoutResId);
        mFragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveEntity item) {
        helper.setText(R.id.tv_theme, item.getTitle())
                .setText(R.id.tv_people_num, item.isOnline() ? mContext.getString(R.string.live_online_num, item.getHeadCount()) : mContext.getString(R.string.anchor_resting))
                .setVisible(R.id.v_mask_bg, !item.isOnline());

        Drawable dotGreen = ContextCompat.getDrawable(mContext, R.drawable.ic_grass_green_dot);
        Drawable dotGray = ContextCompat.getDrawable(mContext, R.drawable.ic_grass_gray_dot);
        ((TextView)helper.getView(R.id.tv_people_num)).setCompoundDrawablesWithIntrinsicBounds(item.isOnline() ? dotGreen : dotGray,
                null, null, null);

        GlideApp.with(mFragment)
                .load(item.getCover())
                .transforms(new CenterCrop(), new RoundedCornersTransformation(ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.view_margin_2)), 0))
                .placeholder(R.drawable.ic_placeholder_default)
                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
