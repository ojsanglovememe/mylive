package com.src.isec.mvp.view.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 纵向RecycleVIew的item间距适配
 * @time 2018/4/17 14:20
 * @change
 * @chang time
 * @class describe
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    int mSpace;

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 0;
        }else {
            outRect.top = ConvertUtils.dp2px(mSpace);
        }

    }

    public SpaceItemDecoration(int space) {
        this.mSpace = space;
    }
}
