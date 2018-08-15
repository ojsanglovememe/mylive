package com.src.isec.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.src.isec.R;
import com.src.isec.config.GlideApp;
import com.src.isec.widget.RoundImageView;

/**
 * 直播关注的弹出框
 */
public class PopFollowUtils {

    private PopupWindow popupWindow;
    private RoundImageView roundImageView;
    private TextView tvNickName, tvId, tvLaction;
    private Button btnFollow;
    private Context mContext;


    public PopFollowUtils(Context context, View.OnClickListener listener) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_follow, null);
        roundImageView = ((RoundImageView) view.findViewById(R.id.rv_cover));
        tvNickName = ((TextView) view.findViewById(R.id.tv_nickname));
        tvId = ((TextView) view.findViewById(R.id.tv_id));
        tvLaction = ((TextView) view.findViewById(R.id.tv_location));
        btnFollow = view.findViewById(R.id.btn_follow);
        btnFollow.setOnClickListener(listener);
        popupWindow = new PopupWindow(view);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
//        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置弹出窗体显示时的动画，从底部向上弹出
        popupWindow.setAnimationStyle(R.style.take_photo_anim);
    }

    /**
     * 设置数据
     */
    public void setData(String headIconUrl, String nickName, String id, String laction, boolean isFollow) {
        if (!TextUtils.isEmpty(headIconUrl) && roundImageView != null)
            GlideApp.with(mContext)
                    .load(headIconUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(roundImageView);


        if (!TextUtils.isEmpty(nickName) && tvNickName != null)
            tvNickName.setText(String.format("昵称：%1$s", nickName));

        if (!TextUtils.isEmpty(id) && tvId != null)
            tvId.setText(String.format("ID：%1$s", id));

        if (!TextUtils.isEmpty(laction) && tvLaction != null)
            tvLaction.setText(String.format("地点：%1$s", laction));

        if(isFollow){
            btnFollow.setText("已关注");
        }else {
            btnFollow.setText("关注");
        }

    }

    public void show(View view) {
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

}
