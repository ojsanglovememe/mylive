package com.src.isec.mvp.view.implement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nukc.stateview.StateView;
import com.src.isec.R;
import com.src.isec.base.BaseFragment;
import com.src.isec.config.Constants;
import com.src.isec.config.GlideApp;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.presenter.implement.MyPresenter;
import com.src.isec.mvp.view.IMyView;
import com.src.isec.mvp.view.implement.activity.AttentionFansActivity;
import com.src.isec.mvp.view.implement.activity.EditPersonalInfoActivity;
import com.src.isec.mvp.view.implement.activity.LoginModeActivity;
import com.src.isec.mvp.view.implement.activity.RealNameAuthActivity;
import com.src.isec.mvp.view.implement.activity.SettingActivity;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.AttentionEvent;
import com.src.isec.reactivex.rxbus.event.LoginEvent;
import com.src.isec.reactivex.rxbus.event.RealNameAuthEvent;
import com.src.isec.reactivex.rxbus.event.UpdatePersonalInfoEvent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 个人中心
 * @time 2018/4/12 0012 09:35
 * @change
 * @chang time
 * @class describe
 */

public class MyFragment extends BaseFragment<MyPresenter> implements IMyView {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_id_num)
    TextView tvIdNum;
    @BindView(R.id.tv_attention)
    TextView tvAttention;
    @BindView(R.id.tv_fans)
    TextView tvFans;
    @BindView(R.id.tv_auth)
    TextView tvAuth;
    @BindView(R.id.ll_content_view)
    FrameLayout llContentView;

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected View injectStateView() {
        return llContentView;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initRealNameAuthView();
    }

    @Override
    protected void initData() {
        super.initData();
        initLoginView();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.top_view).init();
    }

    @Override
    public void initListener() {
        super.initListener();
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                mPresenter.onPersonalInfo();
            }
        });

        RxBus.getInstance().register(LoginEvent.class, new Consumer<LoginEvent>() {
            @Override
            public void accept(LoginEvent loginEvent) throws Exception {
                if(loginEvent.isLogin()){
                    mPresenter.onPersonalInfo();
                } else {
                    initNoLoginView();
                }

            }
        }, this);

        RxBus.getInstance().register(RealNameAuthEvent.class, new Consumer<RealNameAuthEvent>() {
            @Override
            public void accept(RealNameAuthEvent event) throws Exception {
                initRealNameAuthView();
            }
        }, this);

        RxBus.getInstance().register(AttentionEvent.class, new Consumer<AttentionEvent>() {
            @Override
            public void accept(AttentionEvent event) throws Exception {
                mPresenter.onPersonalInfo();
            }
        }, this);

        RxBus.getInstance().register(UpdatePersonalInfoEvent.class, new Consumer<UpdatePersonalInfoEvent>() {
            @Override
            public void accept(UpdatePersonalInfoEvent event) throws Exception {
                if(!TextUtils.isEmpty(event.getNickname())){
                    tvNickName.setText(event.getNickname());
                }

                if(!TextUtils.isEmpty(event.getAvatar())){
                    GlideApp.with(mContext)
                            .load(event.getAvatar())
                            .centerCrop()
                            .placeholder(R.drawable.ic_default_avatar)
                            .into(ivHead);
                }
            }
        }, this);
    }

    @OnClick({R.id.iv_head, R.id.iv_head_arrow, R.id.tv_attention, R.id.tv_fans, R.id.rl_auth, R.id.tv_setting})
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
                if (isLogin()) {
                    startActivity(EditPersonalInfoActivity.class);
                    return;
                }
                startActivity(LoginModeActivity.class);
                break;
            case R.id.iv_head_arrow:
                if (isLogin()) {
                    startActivity(EditPersonalInfoActivity.class);
                    return;
                }
                break;
            case R.id.tv_attention:
                startActivity(AttentionFansActivity.class);
                break;
            case R.id.tv_fans:
                Intent fansIntent = new Intent(mContext, AttentionFansActivity.class);
                fansIntent.putExtra(Constants.LIVE_TYPE_FANS, true);
                startActivity(fansIntent);
                break;
            case R.id.rl_auth:
                if(UserInfoManager.getInstance().isAuth()){
                    Intent intent = new Intent(mContext, RealNameAuthActivity.class);
                    intent.putExtra(Constants.AUTH_SUCCESS ,true);
                    startActivity(intent);
                    return;
                }
                if (isLogin()) {
                    Intent intent = new Intent(mContext, RealNameAuthActivity.class);
                    intent.putExtra(Constants.AUTH_TYPE ,true);
                    startActivity(intent);
                    return;
                }
                startActivity(LoginModeActivity.class);
                break;
            case R.id.tv_setting:
                startActivity(SettingActivity.class);
                break;
        }
    }

    @Override
    public void onPersonalInfoSuccess(UserEntity userEntity) {

        if (userEntity == null) {
            return;
        }
        UserInfoManager.getInstance().setAttentionNum(userEntity.getAttention());
        UserInfoManager.getInstance().setFansNum(userEntity.getFans());
        GlideApp.with(this)
                .load(userEntity.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(ivHead);

        tvNickName.setText(userEntity.getNickname());
        tvIdNum.setText("ID：3564875");
        tvAttention.setText(getString(R.string.attention, userEntity.getAttention()));
        tvFans.setText(getString(R.string.fans, userEntity.getFans()));
    }

    @Override
    public void handleTokenInvalid(String message) {
        super.handleTokenInvalid(message);
        initNoLoginView();
    }

    /**
     * 没有登录情况下
     */
    private void initNoLoginView(){
        ivHead.setImageResource(R.drawable.ic_default_avatar);
        tvNickName.setText(R.string.click_avatar_login);
        tvAttention.setText(getString(R.string.attention, 0));
        tvFans.setText(getString(R.string.fans, 0));
    }

    private void initLoginView(){
        GlideApp.with(this)
                .load(UserInfoManager.getInstance().getAvatar())
                .centerCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(ivHead);
        tvNickName.setText(UserInfoManager.getInstance().getNickName());
        tvIdNum.setText("ID：3564875");
        tvAttention.setText(getString(R.string.attention, UserInfoManager.getInstance().getAttentionNum()));
        tvFans.setText(getString(R.string.fans, UserInfoManager.getInstance().getFansNum()));
    }

    private void initRealNameAuthView(){
        tvAuth.setText(UserInfoManager.getInstance().isAuth() ? R.string.real_name_auth_yes : R.string.real_name_auth_no);
        tvAuth.setTextColor(ContextCompat.getColor(mContext, UserInfoManager.getInstance().isAuth() ? R.color.colorBlack : R.color.colorGray));
    }

}
