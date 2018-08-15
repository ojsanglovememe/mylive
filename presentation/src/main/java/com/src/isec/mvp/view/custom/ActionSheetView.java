package com.src.isec.mvp.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.src.isec.R;
import com.src.isec.mvp.model.MenuEntity;
import com.src.isec.mvp.view.adapter.ActionSheetDialogAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement
 * @class 底部弹出对话框
 * @time 2018/4/18 0010 11:02
 * @change
 * @chang time
 * @class describe
 */
public class ActionSheetView {

    private ActionSheetView(){}


    public static Dialog showOperateCancelDialog(Context context, final List<MenuEntity> operList, final ActionSheetOperateListener listener) {
        final Dialog dialog = new Dialog(context, R.style.ActionSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_actionsheet_cancel_view, null);

        RecyclerView rvOper = (RecyclerView) layout.findViewById(R.id.rv_operate);
        ActionSheetDialogAdapter operAdapter = new ActionSheetDialogAdapter(R.layout.item_actionsheet_text, operList);

        //设置布局管理器
        rvOper.setLayoutManager(new LinearLayoutManager(context));
        rvOper.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.colorGrayLight).sizeResId(R.dimen.list_view_divider_height).build());
        operAdapter.bindToRecyclerView(rvOper);
        rvOper.setAdapter(operAdapter);

        operAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dialog.dismiss();
                if (listener != null) {
                    MenuEntity item = (MenuEntity) adapter.getItem(position);
                    listener.onOperateListener(item, position);
                }
            }
        });

        layout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        layout.setMinimumWidth(context.getResources().getDisplayMetrics().widthPixels);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.gravity = Gravity.BOTTOM;
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(layout);
        dialog.show();
        return dialog;
    }

    public  interface ActionSheetOperateListener {

        void onCancel();

        void onOperateListener(MenuEntity menuEntity, int position);
    }

}
