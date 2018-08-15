package com.src.isec.mvp.view.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.src.isec.base.BaseFragment;

import java.util.List;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 首页顶部标签菜单适配器
 * @time 2018/4/10 0010 11:40
 * @change
 * @chang time
 * @class describe
 */

public class MenuTabAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments;
    private List<String> entityList;

    public MenuTabAdapter(List<BaseFragment> fragmentList, List<String> entityList, FragmentManager fm) {
        super(fm);
        mFragments = fragmentList ;
        this.entityList = entityList;
    }

    @Override
    public BaseFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return entityList.get(position);
    }
}
