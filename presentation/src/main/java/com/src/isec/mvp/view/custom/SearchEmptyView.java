package com.src.isec.mvp.view.custom;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.src.isec.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 搜索空页面
 * @time 2018/4/25 0025 15:46
 * @change
 * @chang time
 * @class describe
 */

public class SearchEmptyView extends LinearLayout {

    @BindView(R.id.tv_empty_text)
    TextView tvEmptyText;

    public SearchEmptyView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_search_empty, this);
        ButterKnife.bind(this, this);
    }

    public void setEmptyText(String str){
        if(tvEmptyText == null){
            return;
        }
        tvEmptyText.setText(getContext().getString(R.string.search_empty, str));
    }
}
