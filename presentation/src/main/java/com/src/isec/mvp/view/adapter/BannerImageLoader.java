package com.src.isec.mvp.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.src.isec.R;
import com.src.isec.config.GlideApp;
import com.youth.banner.loader.ImageLoader;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 轮播图图片加载器
 * @time 2018/4/16 0016 10:48
 * @change
 * @chang time
 * @class describe
 */

public class BannerImageLoader extends ImageLoader {

    private Context mContext;

    public BannerImageLoader(Context context){
        this.mContext = context;
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideApp.with(this.mContext)
                .load(path)
                .placeholder(R.drawable.ic_banner_default)
                .centerCrop()
                .into(imageView);
    }
}
