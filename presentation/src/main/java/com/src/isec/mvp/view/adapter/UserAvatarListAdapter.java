package com.src.isec.mvp.view.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.domain.entity.UserEntity;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 直播间头像列表adapter
 * @time 2018/4/13 9:32
 * @change
 * @chang 2018/4/24 14:46
 * @class 改为使用真实的数据源
 */

public class UserAvatarListAdapter extends BaseQuickAdapter<UserEntity,BaseViewHolder> {

    //主播id
    private String mPusherId;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 100;


    public UserAvatarListAdapter(int layoutId, String pusherId,LinkedList<UserEntity> mUserAvatarList ) {
        super(layoutId,mUserAvatarList);
        this.mPusherId = pusherId;
    }

    /**
     * 带有过滤规则的添加用户信息
     * @param userEntity 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(UserEntity userEntity) {

        //去除主播头像
        if(userEntity.getId().equals(mPusherId))
            return false;

        //去重操作
        for (UserEntity mUserEntity : this.mData) {
            if(mUserEntity.getId().equals(userEntity.getId()))
                return false;
        }

        //始终显示新加入item为第一位
        mData.add(0, userEntity);
        //超出时删除末尾项
        if(mData.size() > TOP_STORGE_MEMBER) {
            mData.remove(TOP_STORGE_MEMBER);
            notifyItemRemoved(TOP_STORGE_MEMBER);
        }
        notifyItemInserted(0);
        return true;
    }

    public void removeItem(String userId) {
        UserEntity tempUserInfo = null;
        for(UserEntity mUserEntity : mData) {
            if (mUserEntity.getId().equals(userId))
                tempUserInfo = mUserEntity;
        }

        if(null != tempUserInfo) {
            mData.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, UserEntity item) {
        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.face)// 正在加载中的图片
                .error(R.drawable.face) // 加载失败的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(mContext)
                .load(item.getAvatar())
                .apply(options)
                .into((CircleImageView) helper.getView(R.id.iv_avatar));
    }
}
