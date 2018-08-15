package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.intdef.LoadingTypeIntDef;
import com.src.isec.mvp.presenter.implement.AttentionFansPresenter;
import com.src.isec.mvp.view.IAttentionFansView;
import com.src.isec.mvp.view.adapter.AttentionFansAdapter;
import com.src.isec.mvp.view.adapter.HotRecommendAdapter;
import com.src.isec.mvp.view.custom.RVDividerHotGrid;
import com.src.isec.mvp.view.custom.RefreshHeaderView;
import com.src.isec.mvp.view.custom.TagHeaderView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.AttentionEvent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;


public class AttentionFansActivity extends BaseActivity<AttentionFansPresenter> implements IAttentionFansView {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.cl_content_view)
    FrameLayout mContentView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private BaseQuickAdapter mAdapter;

    private boolean isFansType = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attention_fans;
    }

    @Override
    protected View injectStateView() {
        return mContentView;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        isFansType = getIntent().getBooleanExtra(Constants.LIVE_TYPE_FANS, false);
        setActivityTitle(isFansType ? R.string.my_fans : R.string.my_attention);
        initSmartRefreshLayout();
        sendRequest(LoadingTypeIntDef.RENDERING, true, page, pageTime);
    }

    private void initSmartRefreshLayout(){
        mSmartRefreshLayout.setRefreshHeader(new RefreshHeaderView(mContext));
    }

    private void initAdapter(boolean isRecommend) {
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);  //去除刷新条目闪动动画
        if(isRecommend){
            initRecommendAdapter();
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.background_gray_color).sizeResId(R.dimen.view_margin_6).build());
        mAdapter = new AttentionFansAdapter(R.layout.item_list_attention);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_fans_empty_view, null));
    }

    private void initRecommendAdapter() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.addItemDecoration(new RVDividerHotGrid(mContext, R.color.background_gray_color));
        mAdapter = new HotRecommendAdapter(mContext, R.layout.item_list_hot_recommend);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.addHeaderView(new TagHeaderView(mContext, getString(R.string.guess_like)));
    }

    private void sendRequest(@LoadingTypeIntDef int loadingTypeIntDef, boolean isRefresh, int page, long pageTime){
        String type = isFansType ? Constants.LIVE_TYPE_FANS : Constants.LIVE_TYPE_FOLLOW;
        mPresenter.getAttentionList(type, loadingTypeIntDef, isRefresh, page, pageTime);
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
                sendRequest(LoadingTypeIntDef.LISTREFRESH, true,  page, pageTime);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page ++;
                sendRequest(LoadingTypeIntDef.LISTLOADMORE, false,  page, pageTime);
            }
        });

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
    public void onDataSuccess(boolean isRecommend, List<LiveEntity> list, boolean isRefresh) {

        if(mAdapter == null){
            initAdapter(isRecommend);
            initAdapterListener();
            mSmartRefreshLayout.setEnableLoadMore(isRecommend);
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
    }
}
