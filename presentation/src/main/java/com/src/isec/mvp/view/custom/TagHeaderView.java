package com.src.isec.mvp.view.custom;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.src.isec.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 热门推荐、猜你喜欢头部显示控件
 * @time 2018/4/24 0024 15:35
 * @change
 * @chang time
 * @class describe
 */

public class TagHeaderView extends RelativeLayout {


    @BindView(R.id.tv_head_text)
    TextView tvHeadText;

    public TagHeaderView(Context context, String headTitle) {
        super(context);
        initView(headTitle);
    }

    private void initView(String headTitle) {
        inflate(getContext(), R.layout.layout_hot_recommend_head_view, this);
        ButterKnife.bind(this, this);

        tvHeadText.setText(headTitle);
    }

}
