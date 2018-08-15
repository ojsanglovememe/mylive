package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.mvp.presenter.implement.RealNameAuthPresenter;
import com.src.isec.mvp.view.IRealNameAuthView;
import com.src.isec.mvp.view.IView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.RealNameAuthEvent;
import com.src.isec.utils.RxLifecycleUtils;
import com.src.isec.utils.ToastUtil;
import com.zmxy.ZMCertification;
import com.zmxy.ZMCertificationListener;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 实名认证提交资料
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class RealNameAuthSubmitActivity extends BaseActivity<RealNameAuthPresenter> implements IRealNameAuthView, ZMCertificationListener {


    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.et_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.et_id_card)
    EditText etIdCard;
    @BindView(R.id.tv_btn_submit)
    TextView tvBtnAuth;
    private ZMCertification zmCertification;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_name_auth_submit;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        zmCertification = ZMCertification.getInstance();
        zmCertification.setZMCertificationListener(this);
        setActivityTitle(R.string.real_name_auth);
    }

    @Override
    public void initListener() {
        super.initListener();

        Observable.combineLatest(RxTextView.textChanges(etRealName), RxTextView.textChanges(etPhoneNum), RxTextView.textChanges(etIdCard),
                new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                        return !TextUtils.isEmpty(charSequence) && RegexUtils.isMobileExact(charSequence2) && RegexUtils.isIDCard18(charSequence3);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        tvBtnAuth.setEnabled(aBoolean);
                    }
                });

    }

    @OnClick(R.id.tv_btn_submit)
    public void onClickEvent() {
        UserInfoManager.getInstance().setAuth(true);
        RxBus.getInstance().send(new RealNameAuthEvent());
        if (getIntent().getBooleanExtra(Constants.AUTH_TYPE, false)) {
            startActivity(MainActivity.class);
            return;
        }
        startActivity(PublishSettingActivity.class);
        finish();

        //TODO 这里会请求后台服务器获取业务串号bizNO，商户号merchantID
        //TODO 拿到后台数据后，开始验证
    }

    private void doVerify(String bizNO, String merchantID) {
        zmCertification.startCertification(this, bizNO, merchantID, null);
    }

    @Override
    public void onFinish(boolean isCanceled, boolean isPassed, int errorCode) {
        zmCertification.setZMCertificationListener(null);
        if (isCanceled)
            ToastUtil.show("cancel : 芝麻验证失败，原因是：" + errorCode);
        else {
            if (isPassed)
                ToastUtil.show("complete : 芝麻验证成功，原因是：" + errorCode);
            else
                ToastUtil.show(" complete : 芝麻验证失败，原因是： " + errorCode);
        }

        //TODO 如果验证成功，需要调用服务器接口，改变验证状态
    }
}
