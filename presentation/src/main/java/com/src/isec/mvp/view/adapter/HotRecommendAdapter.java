package com.src.isec.mvp.view.adapter;


import android.content.Context;
import android.widget.ImageView;

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
 * @class 首页关注热门推荐适配器
 * @time 2018/4/10 0010 11:40
 * @change
 * @chang time
 * @class describe
 */

public class HotRecommendAdapter extends BaseQuickAdapter<LiveEntity, BaseViewHolder> {

    private Context mContext;

    public HotRecommendAdapter(Context context, int layoutResId) {
        super(layoutResId);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveEntity item) {
        helper.setText(R.id.tv_attention, item.isFollow() ? R.string.btn_attention_yes : R.string.btn_attention)
                .addOnClickListener(R.id.tv_attention)
                .getView(R.id.tv_attention).setSelected(item.isFollow());

        GlideApp.with(mContext)
                .load(item.getCover())
                .transforms(new CenterCrop(), new RoundedCornersTransformation(ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.view_margin_1_5)),
                        0, RoundedCornersTransformation.CornerType.TOP))
                .placeholder(R.drawable.ic_placeholder_default)
                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
