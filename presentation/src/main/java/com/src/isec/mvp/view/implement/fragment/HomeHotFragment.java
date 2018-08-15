package com.src.isec.mvp.view.implement.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.nukc.stateview.StateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.src.isec.R;
import com.src.isec.base.BaseFragment;
import com.src.isec.config.Constants;
import com.src.isec.domain.entity.BannerEntity;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.implement.HomeHotPresenter;
import com.src.isec.mvp.view.IHomeHotView;
import com.src.isec.mvp.view.IView;
import com.src.isec.mvp.view.adapter.HomeHotAdapter;
import com.src.isec.mvp.view.custom.BannerView;
import com.src.isec.mvp.view.custom.RVDividerBannerGrid;
import com.src.isec.mvp.view.custom.RefreshHeaderView;
import com.src.isec.mvp.view.implement.activity.LivePlayerActivity;
import com.src.isec.utils.RxLifecycleUtils;
import com.src.isec.utils.TCLocationHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 首页热门Fragment
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */

public class HomeHotFragment extends BaseFragment<HomeHotPresenter> implements IHomeHotView, TCLocationHelper.OnLocationListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.cl_content_view)
    FrameLayout mContentView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private HomeHotAdapter mAdapter;

    private int liveType;
    private String tagId;

    private BannerView headView;

    private double lat = 0;
    private double lng = 0;

    public static HomeHotFragment newInstance(String tagId, int type) {
        HomeHotFragment fragment = new HomeHotFragment();
        Bundle args = new Bundle();
        args.putString(Constants.TAB_TAG_ID, tagId);
        args.putInt(Constants.LIVE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        this.tagId = bundle.getString(Constants.TAB_TAG_ID);
        this.liveType = bundle.getInt(Constants.LIVE_TYPE, Constants.LIVE_TYPE_HOT);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_hot;
    }

    @Override
    protected View injectStateView() {
        return mContentView;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initSmartRefreshLayout();
        initAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getBannerList(this.tagId);

        if(this.liveType == Constants.LIVE_TYPE_NEARBY){    //附近直播开启定位
            RxPermissions rxPermissions = new RxPermissions(mActivity);
            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .compose(RxLifecycleUtils.bindToLifecycle((IView) this))
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if(aBoolean){
                                TCLocationHelper.getMyLocation(mActivity, HomeHotFragment.this);
                            } else {
                                showToast(R.string.permission_request_denied);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mPresenter.getHotLiveList(LoadingTypeIntDef.RENDERING, true, page, pageTime);
        }

    }

    private void initAdapter() {
        headView = new BannerView(mContext);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new RVDividerBannerGrid(mContext, R.color.background_white_color));
        mAdapter = new HomeHotAdapter(this, R.layout.item_list_hot, this.liveType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.addHeaderView(headView);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LiveEntity liveEntity = (LiveEntity) adapter.getItem(position);
                startActivity(LivePlayerActivity.startMyself(liveEntity,getActivity()));
            }
        });
    }

    private void initSmartRefreshLayout(){
        mSmartRefreshLayout.setRefreshHeader(new RefreshHeaderView(mContext));
    }

    private void sendRequest(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime){

        if(this.liveType == Constants.LIVE_TYPE_NEARBY){
            mPresenter.getNearbyLiveList(loadingTypeIntDef, isRefresh, page, pageTime, this.lat, this.lng);
            return;
        }

        mPresenter.getHotLiveList(loadingTypeIntDef, isRefresh, page, pageTime);
    }

    @Override
    public void initListener() {
        super.initListener();
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                pageTime = getPageTime();
                page = 1;
                sendRequest(LoadingTypeIntDef.RENDERING, true, page, pageTime);
            }
        });

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageTime = getPageTime();
                page = 1;
                sendRequest(LoadingTypeIntDef.LISTREFRESH, true, page, pageTime);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page ++;
                sendRequest(LoadingTypeIntDef.LISTLOADMORE, false, page, pageTime);
            }
        });

    }

    @Override
    public void onBannerSuccess(List<BannerEntity> list) {
        headView.initBannerImages(list);
    }

    @Override
    public void onDataSuccess(List<LiveEntity> list, boolean isRefresh) {
        if(isRefresh){
            mAdapter.setNewData(list);
        } else {
            mAdapter.addData(list);
        }
        mAdapter.loadMoreComplete();
        mAdapter.loadMoreEnd(true);

        if(list.size() == 0 || list.size() < Constants.PAGE_NUM){
            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
        }
    }

    @Override
    public void hideRefreshLoading() {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.setNoMoreData(false);//恢复上拉状态
    }

    @Override
    public void hideRefreshLoadMore() {
        mSmartRefreshLayout.finishLoadMore();
    }

    @Override
    public void onLocationChanged(int code, double lat1, double long1, String location) {
        this.lat = lat1;
        this.lng = long1;
        mPresenter.getNearbyLiveList(LoadingTypeIntDef.RENDERING, true, page, pageTime, this.lat, this.lng);
    }
}
