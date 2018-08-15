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
import com.src.isec.config.Constants;
import com.src.isec.config.GlideApp;
import com.src.isec.domain.entity.LiveEntity;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 首页热门直播列表适配器
 * @time 2018/4/10 0010 11:40
 * @change
 * @chang time
 * @class describe
 */

public class HomeHotAdapter extends BaseQuickAdapter<LiveEntity, BaseViewHolder> {

    Fragment mFragment;
    private int liveType;

    public HomeHotAdapter(Fragment fragment, int layoutResId, int liveType) {
        super(layoutResId);
        mFragment = fragment;
        this.liveType = liveType;
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveEntity item) {
        helper.setText(R.id.tv_theme, item.getTitle());
        helper.setText(R.id.tv_people_num, liveType == Constants.LIVE_TYPE_HOT ? mContext.getString(R.string.live_online_num, item.getHeadCount()) : formatDistance(item.getDistance()));

        Drawable dotGreen = ContextCompat.getDrawable(mContext, R.drawable.ic_grass_green_dot);
        Drawable dotLocation = ContextCompat.getDrawable(mContext, R.drawable.ic_live_location);
        ((TextView)helper.getView(R.id.tv_people_num)).setCompoundDrawablesWithIntrinsicBounds(liveType == Constants.LIVE_TYPE_HOT ? dotGreen : dotLocation,
                null, null, null);

        GlideApp.with(mFragment)
                .load(item.getCover())
                .placeholder(R.drawable.ic_placeholder_default)
                .transforms(new CenterCrop(), new RoundedCornersTransformation(ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.view_margin_2)), 0))
                .into((ImageView) helper.getView(R.id.iv_cover));
    }

    private String formatDistance(double distance){
        return Math.round(distance/100d)/10d + "km";
    }
}
