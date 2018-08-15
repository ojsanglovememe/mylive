package com.src.isec.mvp.view.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.mvp.model.MenuEntity;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class 底部弹出对话框适配器
 * @time 2018/4/18 0010 11:02
 * @change
 * @chang time
 * @class describe
 */

public class ActionSheetDialogAdapter extends BaseQuickAdapter<MenuEntity, BaseViewHolder> {

    public ActionSheetDialogAdapter(@LayoutRes int layoutResId, @Nullable List<MenuEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuEntity item) {
        helper.setText(R.id.tv_item_title, item.getMenuTitle());
    }
}
