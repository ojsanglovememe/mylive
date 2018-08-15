package com.src.isec.mvp.view.implement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.mvp.presenter.implement.RealNameAuthPresenter;
import com.src.isec.mvp.view.IRealNameAuthView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.RealNameAuthEvent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 实名认证提示
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class RealNameAuthActivity extends BaseActivity<RealNameAuthPresenter> implements IRealNameAuthView {


    @BindView(R.id.iv_auth_img)
    ImageView ivAuthImg;
    @BindView(R.id.tv_auth_prompt)
    TextView tvAuthPrompt;
    @BindView(R.id.tv_btn_auth)
    TextView tvBtnAuth;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_name_auth;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        setActivityTitle(R.string.real_name_auth);

        if (getIntent().getBooleanExtra(Constants.AUTH_SUCCESS, false)) {
            ivAuthImg.setImageResource(R.drawable.ic_real_name_auth_success);
            tvAuthPrompt.setText(R.string.real_name_auth_success);
            tvBtnAuth.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.tv_btn_auth)
    public void onClickEvent() {
        Intent intent = new Intent(mContext, RealNameAuthSubmitActivity.class);
        intent.putExtra(Constants.AUTH_TYPE, getIntent().getBooleanExtra(Constants.AUTH_TYPE, false));
        startActivity(intent);
    }

    @Override
    public void initListener() {
        super.initListener();

        RxBus.getInstance().register(RealNameAuthEvent.class, new Consumer<RealNameAuthEvent>() {
            @Override
            public void accept(RealNameAuthEvent event) throws Exception {
                finish();
            }
        }, this);
    }

}
