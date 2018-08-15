package com.src.isec.mvp.view.custom;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 首页列表分割控件
 * @time 2018/4/10 0010 11:51
 * @change
 * @chang time
 * @class describe
 */

public class RVDividerBannerGrid extends Y_DividerItemDecoration {

    private Context context;

    private int colorRes;

    public RVDividerBannerGrid(Context context, @ColorRes int colorRes) {
        super(context);
        this.context = context;
        this.colorRes = colorRes;
    }

    @Override
    public Y_Divider getDivider(int itemPosition) {
        Y_Divider divider = null;
        if(itemPosition == 0){
            return new Y_DividerBuilder()
                    .setRightSideLine(false, ContextCompat.getColor(context, this.colorRes), 0, 0, 0)
                    .setLeftSideLine(false, ContextCompat.getColor(context, this.colorRes), 0, 0, 0)
                    .setTopSideLine(false, ContextCompat.getColor(context, this.colorRes), 0, 0, 0)
                    .setBottomSideLine(false, ContextCompat.getColor(context, this.colorRes), 0, 0, 0)
                    .create();
        }
        switch (itemPosition % 2) {
            case 0:
                divider = new Y_DividerBuilder()
                        .setRightSideLine(true, ContextCompat.getColor(context, this.colorRes), 10, 0, 0)
                        .setLeftSideLine(true, ContextCompat.getColor(context, this.colorRes), 5, 0, 0)
                        .setBottomSideLine(true, ContextCompat.getColor(context, this.colorRes), 10, 0, 0)
                        .create();
                break;
            case 1:
                divider = new Y_DividerBuilder()
                        .setLeftSideLine(true, ContextCompat.getColor(context, this.colorRes), 10, 0, 0)
                        .setRightSideLine(true, ContextCompat.getColor(context, this.colorRes), 5, 0, 0)
                        .setBottomSideLine(true, ContextCompat.getColor(context, this.colorRes), 10, 0, 0)
                        .create();
                break;
            default:
                break;
        }
        return divider;
    }
}
