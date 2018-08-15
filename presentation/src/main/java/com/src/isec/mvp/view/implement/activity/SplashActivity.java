package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.src.isec.R;
import com.src.isec.base.SimpleActivity;
import com.src.isec.config.GlideApp;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.utils.IMMgrUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class SplashActivity extends SimpleActivity {


    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {

        GlideApp.with(mContext)
                .load(R.drawable.ic_splash)
                .centerCrop()
                .into(ivSplash);

        Observable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (UserInfoManager.getInstance().isLogin()) {
                            IMMgrUtil.imLogin();
                            startActivity(MainActivity.class);
                            finish();
                        } else {
                            startActivity(LoginModeActivity.class);
                            finish();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
