package com.src.isec.mvp.view.implement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.src.isec.R;
import com.src.isec.base.SimpleActivity;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.LoginEvent;
import com.src.isec.utils.IMMgrUtil;

import butterknife.BindView;
import butterknife.OnClick;


public class SettingActivity extends SimpleActivity {

    @BindView(R.id.tv_binding_title)
    TextView tvBindingTitle;
    @BindView(R.id.tv_binding)
    TextView tvBinding;
    @BindView(R.id.rl_binding)
    RelativeLayout rlBinding;
    @BindView(R.id.tv_exit_login)
    TextView tvExitLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        setActivityTitle(R.string.setting);

        rlBinding.setVisibility(UserInfoManager.getInstance().isLogin() ? View.VISIBLE : View.INVISIBLE);
        tvExitLogin.setVisibility(UserInfoManager.getInstance().isLogin() ? View.VISIBLE : View.INVISIBLE);
        tvBinding.setText(UserInfoManager.getInstance().isBindingMobile() ? R.string.change_binding : R.string.binding_phone);
        tvBindingTitle.setText(UserInfoManager.getInstance().isBindingMobile() ? UserInfoManager.getInstance().getMobile() : "");
    }

    @OnClick({R.id.rl_binding, R.id.tv_exit_login})
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.rl_binding:
                if(UserInfoManager.getInstance().isBindingMobile()){
                    startActivity(ChangePhoneActivity.class);
                    return;
                }
                Intent intent = new Intent(mContext, ChangePhoneSubmitActivity.class);
                intent.putExtra(Constants.BINDING_MOBILE, true);
                startActivity(intent);
                break;
            case R.id.tv_exit_login:
                new MaterialDialog.Builder(mContext)
                        .content(R.string.sure_exit_login)
                        .positiveText(R.string.btn_sure).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UserInfoManager.getInstance().unInstallUserInfo();
                        RxBus.getInstance().send(new LoginEvent(false));
                        //登出IM
                        IMMgrUtil.logout();
                        finish();
                    }
                })
                        .negativeText(R.string.btn_cancel)
                        .show();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null){
            return;
        }
        String mobile = intent.getStringExtra(Constants.LOGIN_MOBILE);
        if(!TextUtils.isEmpty(mobile)){
            tvBindingTitle.setText(mobile);
        }
        tvBinding.setText(UserInfoManager.getInstance().isBindingMobile() ? R.string.change_binding : R.string.binding_phone);
        tvBindingTitle.setText(UserInfoManager.getInstance().isBindingMobile() ? UserInfoManager.getInstance().getMobile() : "");

    }
}
