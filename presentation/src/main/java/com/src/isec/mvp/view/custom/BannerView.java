package com.src.isec.mvp.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.src.isec.R;
import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.mvp.view.adapter.BannerImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 首页顶部轮播图控件
 * @time 2018/4/16 0016 10:39
 * @change
 * @chang time
 * @class describe
 */

public class BannerView extends RelativeLayout {

    @BindView(R.id.banner)
    Banner bannerView;

    private Context mContext;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_banner_view, this);
        ButterKnife.bind(this, this);
    }

    public void initBannerImages(List<BannerEntity> list){
        if(bannerView == null){
            return;
        }
        bannerView.setImages(imgUrls(list))
                .setImageLoader(new BannerImageLoader(mContext))
                .start();

        bannerView.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
    }

    private List<String> imgUrls(List<BannerEntity> tagList){
        List<String> list = new ArrayList<>();
        if(tagList == null || tagList.size() == 0){
            return list;
        }
        for(BannerEntity bannerEntity : tagList){
            list.add(bannerEntity.getImage());
        }
        return list;
    }

}
