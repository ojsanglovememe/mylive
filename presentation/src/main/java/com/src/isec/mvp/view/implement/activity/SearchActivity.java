package com.src.isec.mvp.view.implement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.nukc.stateview.StateView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.mvp.presenter.implement.SearchPresenter;
import com.src.isec.mvp.view.ISearchView;
import com.src.isec.mvp.view.IView;
import com.src.isec.mvp.view.adapter.HotRecommendAdapter;
import com.src.isec.mvp.view.adapter.SearchAdapter;
import com.src.isec.mvp.view.custom.ClearEditText;
import com.src.isec.mvp.view.custom.RVDividerHotGrid;
import com.src.isec.mvp.view.custom.SearchEmptyView;
import com.src.isec.mvp.view.custom.TagHeaderView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.AttentionEvent;
import com.src.isec.utils.RxLifecycleUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.activity
 * @class 首页搜索
 * @time 2018/4/25 0025 11:38
 * @change
 * @chang time
 * @class describe
 */

public class SearchActivity extends BaseActivity<SearchPresenter> implements ISearchView {

    @BindView(R.id.et_keyword)
    ClearEditText etKeyword;
    @BindView(R.id.recyclerView)
    RecyclerView mHotRecyclerView;
    @BindView(R.id.recyclerView_search)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.cl_content_view)
    FrameLayout clContentView;

    private BaseQuickAdapter mHotAdapter;
    private SearchAdapter mSearchAdapter;

    private SearchEmptyView emptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected View injectStateView() {
        return clContentView;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.title_top_view).init();
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        initAdapter();
        initHotAdapter();
        mPresenter.getHotList();
    }

    private void initAdapter() {
        ((DefaultItemAnimator) mSearchRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);  //去除刷新条目闪动动画
        emptyView = new SearchEmptyView(mContext);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSearchRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.colorHomeTabText).sizeResId(R.dimen.list_view_divider_height).build());
        mSearchAdapter = new SearchAdapter(R.layout.item_list_search);
        mSearchRecyclerView.setAdapter(mSearchAdapter);
        mSearchAdapter.bindToRecyclerView(mSearchRecyclerView);
        mSearchAdapter.setEmptyView(emptyView);
    }

    private void initHotAdapter() {
        ((DefaultItemAnimator) mHotRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);  //去除刷新条目闪动动画
        mHotRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mHotRecyclerView.addItemDecoration(new RVDividerHotGrid(mContext, R.color.background_gray_color));
        mHotAdapter = new HotRecommendAdapter(mContext, R.layout.item_list_hot_recommend);
        mHotRecyclerView.setAdapter(mHotAdapter);
        mHotAdapter.bindToRecyclerView(mHotRecyclerView);
        mHotAdapter.addHeaderView(new TagHeaderView(mContext, getString(R.string.hot_recommend)));
    }

    @Override
    public void initListener() {
        super.initListener();
        RxTextView.textChanges(etKeyword)
                .debounce(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) this))
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        mHotRecyclerView.setVisibility(TextUtils.isEmpty(charSequence) ? View.VISIBLE : View.INVISIBLE);
                        mSearchRecyclerView.setVisibility(TextUtils.isEmpty(charSequence) ? View.INVISIBLE : View.VISIBLE);
                        if(!TextUtils.isEmpty(charSequence)){
                            mPresenter.getSearchList(charSequence.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                mHotRecyclerView.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.INVISIBLE);
                mPresenter.getHotList();
            }
        });

        mSearchAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
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

        mHotAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
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

    @Override
    public void onSearchDataSuccess(String key, List<LiveEntity> list) {
        if(list.size() == 0 && !TextUtils.isEmpty(key)){
            emptyView.setEmptyText(key);
        }
        if(!TextUtils.isEmpty(key)){
            mSearchAdapter.setKeyWord(key);
        }
        mSearchAdapter.setNewData(list);
        mSearchAdapter.loadMoreComplete();
        mSearchAdapter.loadMoreEnd(true);
    }

    @Override
    public void onHotDataSuccess(List<LiveEntity> list) {
        mHotAdapter.setNewData(list);
        mHotAdapter.loadMoreComplete();
        mHotAdapter.loadMoreEnd(true);
    }

    @Override
    public void onAttentionCallback(Object o) {
        RxBus.getInstance().send(new AttentionEvent());
    }

    @OnClick(R.id.tv_cancel)
    public void onClickView(){
        onBackPressed();
    }
}
