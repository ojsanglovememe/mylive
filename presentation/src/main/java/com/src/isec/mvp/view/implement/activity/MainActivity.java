package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.mvp.presenter.implement.MainPresenter;
import com.src.isec.mvp.view.IMainView;
import com.src.isec.mvp.view.adapter.MainFragmentPagerAdapter;
import com.src.isec.mvp.view.custom.NoTouchViewPager;
import com.src.isec.mvp.view.implement.fragment.HomeFragment;
import com.src.isec.mvp.view.implement.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {

    @BindView(R.id.viewPager)
    NoTouchViewPager mViewPager;

    @BindView(R.id.tab)
    PageNavigationView mBottomTab;

    @BindView(R.id.iv_add_live)
    ImageView mIvStartLive;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        NavigationController navigationController = mBottomTab.custom()
                .addItem(newItem(R.drawable.ic_tab_home_normal, R.drawable.ic_tab_home_light,
                        getString(R.string.bottom_tab_menu_home)))
                .addItem(newItem(R.drawable.ic_tab_my_normal, R.drawable.ic_tab_my_light,
                        getString(R.string.bottom_tab_menu_my)))
                .build();
        mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),
                getFragments()));
        mViewPager.setOffscreenPageLimit(navigationController.getItemCount());
        //自动适配ViewPager页面切换
        navigationController.setupWithViewPager(mViewPager);

        //设置消息数
//        navigationController.setMessageNumber(1, 8);

        //设置显示小圆点
//        navigationController.setHasMessage(2, true);


        RxView.clicks(mIvStartLive).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {


            @Override
            public void accept(Object o) throws Exception {
                if (!isLogin()) {
                    startActivity(LoginModeActivity.class);
                    return;
                }
                if (!UserInfoManager.getInstance().isAuth()) {
                    startActivity(RealNameAuthActivity.class);
                    return;
                }
                if (UserInfoManager.getInstance().isAuth()) {
                    startActivity(PublishSettingActivity.class);
                    return;
                }
                startActivity(PublishSettingActivity.class);

            }
        });

    }

    private List<Fragment> getFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(HomeFragment.newInstance());
        list.add(MyFragment.newInstance());
        return list;
    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        normalItemView.setTextDefaultColor(ContextCompat.getColor(mContext, R.color
                .colorHomeTabText));
        normalItemView.setTextCheckedColor(ContextCompat.getColor(mContext, R.color
                .colorHomeTabTextSelect));
        return normalItemView;
    }


}
