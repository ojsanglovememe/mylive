package com.src.isec.mvp.view.implement.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.implement.HomeAttentionPresenter;
import com.src.isec.mvp.view.IHomeAttentionView;
import com.src.isec.mvp.view.adapter.HomeAttentionAdapter;
import com.src.isec.mvp.view.adapter.HotRecommendAdapter;
import com.src.isec.mvp.view.custom.RVDividerGrid;
import com.src.isec.mvp.view.custom.RVDividerHotGrid;
import com.src.isec.mvp.view.custom.RefreshHeaderView;
import com.src.isec.mvp.view.custom.TagHeaderView;
import com.src.isec.mvp.view.implement.activity.LivePlayerActivity;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.AttentionEvent;
import com.src.isec.reactivex.rxbus.event.LoginEvent;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 首页关注Fragment
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */

public class HomeAttentionFragment extends BaseFragment<HomeAttentionPresenter> implements IHomeAttentionView {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.cl_content_view)
    FrameLayout mContentView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private BaseQuickAdapter mAdapter;
    private RVDividerGrid gridItemDecoration;
    private RVDividerHotGrid hotGridItemDecoration;

    public static HomeAttentionFragment newInstance() {
        HomeAttentionFragment fragment = new HomeAttentionFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_attention;
    }

    @Override
    protected View injectStateView() {
        return mContentView;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initSmartRefreshLayout();
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);  //去除刷新条目闪动动画
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
    }

    private void initSmartRefreshLayout(){
        mSmartRefreshLayout.setRefreshHeader(new RefreshHeaderView(mContext));
    }

    private void initAdapter(boolean isAttention) {
        if(isAttention){
            if(gridItemDecoration == null){
                gridItemDecoration = new RVDividerGrid(mContext, R.color.background_gray_color);
                mRecyclerView.addItemDecoration(gridItemDecoration);
            }
            mAdapter = new HomeAttentionAdapter(this, R.layout.item_list_hot);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.bindToRecyclerView(mRecyclerView);
            return;
        }
        initHotAdapter();
    }

    private void initHotAdapter() {
        if(hotGridItemDecoration == null){
            hotGridItemDecoration = new RVDividerHotGrid(mContext, R.color.background_gray_color);
            mRecyclerView.addItemDecoration(hotGridItemDecoration);
        }
        mAdapter = new HotRecommendAdapter(mContext, R.layout.item_list_hot_recommend);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.addHeaderView(new TagHeaderView(mContext, getString(R.string.hot_recommend)));
        mAdapter.addFooterView(LayoutInflater.from(mContext).inflate(R.layout.layout_hot_recommend_footer_view, null));
    }

    @Override
    protected void initData() {
        super.initData();
        sendRequest(LoadingTypeIntDef.RENDERING,  true, isLogin(), page, pageTime);
    }

    @Override
    public void initListener() {
        super.initListener();

        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                pageTime = getPageTime();
                page = 1;
                sendRequest(LoadingTypeIntDef.RENDERING,  true, isLogin(), page, pageTime);
            }
        });

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageTime = getPageTime();
                page = 1;
                sendRequest(LoadingTypeIntDef.LISTREFRESH, true, isLogin(), page, pageTime);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page ++;
                sendRequest(LoadingTypeIntDef.LISTLOADMORE, false, isLogin(), page, pageTime);
            }
        });

        RxBus.getInstance().register(LoginEvent.class, new Consumer<LoginEvent>() {
            @Override
            public void accept(LoginEvent loginEvent) throws Exception {
                mAdapter = null;
                pageTime = getPageTime();
                page = 1;
                sendRequest(LoadingTypeIntDef.RENDERING,  true, loginEvent.isLogin(), page, pageTime);

            }
        }, this);

//        RxBus.getInstance().register(AttentionEvent.class, new Consumer<AttentionEvent>() {
//            @Override
//            public void accept(AttentionEvent event) throws Exception {
//                mAdapter = null;
//                pageTime = getPageTime();
//                page = 1;
//                sendRequest(LoadingTypeIntDef.RENDERING,  true, true, page, pageTime);
//            }
//        }, this);

    }

    private void sendRequest(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, boolean isLogin, int page, long pageTime){
        if(isLogin){
            mPresenter.getAttentionList(loadingTypeIntDef, isRefresh, page, pageTime);
            return;
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
    public void onDataSuccess(boolean isAttention, List<LiveEntity> list, boolean isRefresh) {
        if(mAdapter == null){
            initAdapter(isAttention);
            initAdapterListener();
            mSmartRefreshLayout.setEnableLoadMore(isAttention);
        }

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
    public void onAttentionCallback(Object o) {
        RxBus.getInstance().send(new AttentionEvent());
    }

    private void initAdapterListener(){
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                LiveEntity item = (LiveEntity) adapter.getItem(position);
                if(view.getId() == R.id.tv_attention){
                    item.setFollow(!item.isFollow());
                    mPresenter.getAttention(item.getId());
                    adapter.setData(position, item);
                }
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LiveEntity liveEntity = (LiveEntity) adapter.getItem(position);
                startActivity(LivePlayerActivity.startMyself(liveEntity,getActivity()));
            }
        });
    }
}
