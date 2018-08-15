package com.src.isec.mvp.view.implement.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.src.isec.R;
import com.src.isec.base.BaseFragment;
import com.src.isec.config.Constants;
import com.src.isec.domain.entity.TabTagEntity;
import com.src.isec.mvp.presenter.implement.HomePresenter;
import com.src.isec.mvp.view.IHomeView;
import com.src.isec.mvp.view.adapter.MenuTabAdapter;
import com.src.isec.mvp.view.custom.ScaleTransitionPagerTitleView;
import com.src.isec.mvp.view.custom.ShadowPagerIndicator;
import com.src.isec.mvp.view.implement.activity.SearchActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 首页容器Fragment
 * 包含热门、附近和关注三个子页面
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements IHomeView {


    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.top_view).init();
    }

    @Override
    protected boolean isLazyLoad() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.getTabTagList();
    }

    private List<BaseFragment> getFragments(List<TabTagEntity> tagList) {
        List<BaseFragment> list = new ArrayList<>();
        if(tagList == null || tagList.size() < 3){  //后台返回的标签数量小于3，加载默认页面
            list.add(HomeHotFragment.newInstance("", Constants.LIVE_TYPE_HOT));
            list.add(HomeHotFragment.newInstance("",Constants.LIVE_TYPE_NEARBY));
            list.add(HomeAttentionFragment.newInstance());
            return list;
        }
        //后台返回的标签前三个是固定的，所以这里就直接写死
        list.add(HomeHotFragment.newInstance(tagList.get(0).getId(), Constants.LIVE_TYPE_HOT)); //热门
        list.add(HomeHotFragment.newInstance(tagList.get(1).getId(), Constants.LIVE_TYPE_NEARBY));   //附近
        list.add(HomeAttentionFragment.newInstance());  //关注

        //如果标签数量大于3，就动态加载页面
        if(tagList.size() > 3){
            int tagSize = tagList.size() - 3;
            for(int i =0; i<tagSize; i++){
                list.add(HomeAttentionFragment.newInstance());
            }
        }
        return list;
    }

    private List<String> getTitles(List<TabTagEntity> tagList){
        if(tagList == null || tagList.size() < 3){  //后台返回的标签数量小于3，加载默认
            return Arrays.asList(mContext.getResources().getStringArray(R.array.home_top_tab_text));
        }
        List<String>  titles = new ArrayList<>();
        for(TabTagEntity tabTagEntity : tagList){
            titles.add(tabTagEntity.getName());
        }
        return titles;
    }

    private void initMagicIndicator(Context context, FragmentManager fm,
                                    MagicIndicator magicIndicator, final ViewPager pager,
                                    List<BaseFragment> fragmentList, final List<String> itemList, int currentItem) {
        pager.setAdapter(new MenuTabAdapter(fragmentList, itemList, fm));
        CommonNavigator commonNavigator = new CommonNavigator(context);
//        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {


            @Override
            public int getCount() {
                return fragmentList == null ? 0 : fragmentList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(itemList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setShadowLayer(5,0,8, ContextCompat.getColor(mContext, R.color.colorTabProjection));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.background_white_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.background_white_color));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                ShadowPagerIndicator indicator = new ShadowPagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setRoundRadius(UIUtil.dip2px(context, 1));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.WHITE);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, pager);
        pager.setOffscreenPageLimit(fragmentList.size());
        pager.setCurrentItem(currentItem);

    }

    @OnClick({R.id.iv_search})
    public void onClickEvent(View v){
        if(v.getId() == R.id.iv_search){
            startActivity(SearchActivity.class);
        }
    }

    @Override
    public void onDataSuccess(List<TabTagEntity> list) {
        initMagicIndicator(mContext, getChildFragmentManager(), magicIndicator, mViewPager,
                getFragments(list), getTitles(list), 0);
    }

    @Override
    public void handleCurrencyError(String message) {
        super.handleCurrencyError(message);
        initMagicIndicator(mContext, getChildFragmentManager(), magicIndicator, mViewPager,
                getFragments(null), getTitles(null), 0);

    }
}
