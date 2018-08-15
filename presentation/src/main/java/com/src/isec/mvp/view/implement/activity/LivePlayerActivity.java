package com.src.isec.mvp.view.implement.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.ChatEntity;
import com.src.isec.domain.entity.LiveEntity;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.domain.entity.WatcherEntity;
import com.src.isec.mvp.presenter.implement.LivePlayerPresenter;
import com.src.isec.mvp.view.ILivePlayerView;
import com.src.isec.mvp.view.adapter.ChatMsgListAdapter;
import com.src.isec.mvp.view.adapter.UserAvatarListAdapter;
import com.src.isec.mvp.view.custom.InputTextMsgDialog;
import com.src.isec.mvp.view.custom.SpaceItemDecoration;
import com.src.isec.utils.IMChatRoomMgr;
import com.src.isec.utils.PopFollowUtils;
import com.src.isec.utils.PopWindowUtil;
import com.src.isec.utils.ToastUtil;
import com.tencent.TIMMessage;
import com.tencent.TIMMessagePriority;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class LivePlayerActivity extends BaseActivity<LivePlayerPresenter> implements
        ITXLivePlayListener, ILivePlayerView, InputTextMsgDialog.OnTextSendListener,
        IMChatRoomMgr.TCChatRoomListener, RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.video_view)
    TXCloudVideoView mTXCloudVideoView;
    @BindView(R.id.background)
    ImageView mBackground;
    @BindView(R.id.rv_user_avatar)
    RecyclerView mRvUserAvatar;
    @BindView(R.id.btn_message_input)
    ImageView mBtnMessageInput;
    @BindView(R.id.btn_share)
    ImageView mBtnShare;
    @BindView(R.id.tool_bar)
    RelativeLayout mToolBar;
    @BindView(R.id.rl_controllLayer)
    RelativeLayout mRlControllLayer;
    @BindView(R.id.rl_play_root)
    RelativeLayout mRlPlayRoot;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_id_num)
    TextView mTvIdNum;
    @BindView(R.id.iv_follow)
    ImageView mIvFollow;
    @BindView(R.id.iv_head_iocn)
    CircleImageView mIvHeadIocn;
    @BindView(R.id.fl_head)
    FrameLayout mFlHead;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.imgbtn_live_quit)
    ImageButton mImgbtnLiveQuit;
    @BindView(R.id.recycle_chat_msg)
    RecyclerView mRecycleChatMsg;
    @BindView(R.id.rl_root)
    RelativeLayout mRlRoot;
    @BindView(R.id.ll_pusher_info)
    LinearLayout mLlPusherInfo;
    @BindView(R.id.rl_user_list)
    RelativeLayout mRlUserList;


    protected boolean mPausing = false;
    private boolean mPlaying = false;
    private String mPusherNickname;
    protected String mPusherId;
    protected String mPlayUrl = "rtmp://192.168.88.42:1935/live/wj";
    private String mGroupId = "";
    private String mCover = ""; //封面
    protected String mUserId = "";
    protected String mNickname = "";
    protected String mHeadPic = "";
    private boolean isFollow;//是否已关注主播
    private String mPusherAvatar;//主播头像
    private long mCurrentMemberCount = 0;
    private String mTitle = ""; //标题
    private String pusherCity;
    private String roomNumber;//直播房间号

    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private int mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private InputTextMsgDialog mInputTextMsgDialog;
    private UserAvatarListAdapter mUserAvatarListAdapter;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private LinkedList<ChatEntity> mChatEntities = new LinkedList<>();
    private Handler mHandler = new Handler();
    PopFollowUtils mPopFollowUtils;//关注主播弹窗
    PopWindowUtil popWindowUtil;

    //IM相关
    private IMChatRoomMgr mIMChatRoomMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    @Override
    protected View injectStateView() {
        return null;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        getRoomInfo();
        joinRoom();
        startPlay();
        initView();
        //在这里停留，让列表界面卡住几百毫秒，给sdk一点预加载的时间，形成秒开的视觉效果
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Intent startMyself(LiveEntity entity, Activity activity) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra("liveEntity", entity);
        return intent;
    }

    /**
     * 进入房间首先要做的事情
     */
    private void getRoomInfo() {
        //获取房间信息
        Intent intent = getIntent();
        LiveEntity entity = (LiveEntity) intent.getSerializableExtra("liveEntity");
        if (entity == null) {
            ToastUtil.show("获取房间信息失败");
            finish();
        }
        mPusherId = entity.getId();
        mPlayUrl = entity.getPlay_url2();//使用flv格式
        mGroupId = entity.getChat_room_id();
        mPusherNickname = entity.getNickname();
        mPusherAvatar = entity.getAvatar();
        mTitle = entity.getTitle();
        pusherCity = entity.getCity();
        isFollow = entity.isFollow();
        roomNumber = entity.getNumber();
        mUserId = UserInfoManager.getInstance().getUserId();
        mNickname = UserInfoManager.getInstance().getNickName();
    }

    private void joinRoom() {
        //发布一条系统消息
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("系统");
        handleMsgText(userEntity, "主播" + mPusherNickname + "开播了");
        mIMChatRoomMgr = IMChatRoomMgr.getInstance();
        mIMChatRoomMgr.setMessageListener(this);
        mIMChatRoomMgr.joinGroup(mGroupId);
        mPresenter.inJoinRoom(roomNumber);//上报业务服务器
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //根布局适应透明状态栏
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRlControllLayer
                .getLayoutParams();
        params.topMargin = BarUtils.getStatusBarHeight();
        mRlControllLayer.setLayoutParams(params);

        //设置信息
        mTvTitle.setText(mTitle);
        mTvIdNum.setText(mPusherId);
        mTvName.setText(mPusherNickname);
        Glide.with(this)
                .load(mPusherAvatar)
                .into(mIvHeadIocn);

        //聊天输入框
        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        //用户列表
        mUserAvatarListAdapter = new UserAvatarListAdapter(R.layout.recycle_item_user_avatar,
                mPusherId, new LinkedList<UserEntity>());
        mRvUserAvatar.setAdapter(mUserAvatarListAdapter);
        LinearLayoutManager userLinearLayout = new LinearLayoutManager(this);
        userLinearLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvUserAvatar.setLayoutManager(userLinearLayout);
        //把点击打开区别不同用户
        mUserAvatarListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtil.show(mUserAvatarListAdapter.getData().get(position).getNickname());
            }
        });

        //聊天列表
        mChatMsgListAdapter = new ChatMsgListAdapter(R.layout.recycle_item_chat_list, new
                LinkedList<ChatEntity>(), mRecycleChatMsg);
        LinearLayoutManager msgLinearLayout = new LinearLayoutManager(this);
        msgLinearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleChatMsg.setLayoutManager(msgLinearLayout);
        mRecycleChatMsg.setAdapter(mChatMsgListAdapter);
        mRecycleChatMsg.addItemDecoration(new SpaceItemDecoration(5));

        //软件盘弹起监听
        mRlControllLayer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //获取屏幕高度
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screenHeight = metrics.heightPixels;
                Rect r = new Rect(); //该对象代表一个矩形（rectangle）
                mRlControllLayer.getWindowVisibleDisplayFrame(r); //将当前界面的尺寸传给Rect矩形
                int deltaHeight = screenHeight - r.bottom;  //弹起键盘时的变化高度，在该场景下其实就是键盘高度。
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        mRecycleChatMsg.getLayoutParams();
                if (deltaHeight > 150) {
                    params.bottomMargin = deltaHeight;
                    mRecycleChatMsg.setLayoutParams(params);
                    mLlPusherInfo.setVisibility(View.GONE);
                    mRlUserList.setVisibility(View.GONE);

                } else {
                    params.bottomMargin = 0;
                    mRecycleChatMsg.setLayoutParams(params);
                    mLlPusherInfo.setVisibility(View.VISIBLE);
                    mRlUserList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.rl_follow, R.id.btn_message_input, R.id.btn_share, R.id.imgbtn_live_quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_follow:
                mPopFollowUtils = new PopFollowUtils(this, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mPresenter.setAttention(mPusherId);
                        mPopFollowUtils.dismiss();
                    }
                });
                mPopFollowUtils.setData(mPusherAvatar, mPusherNickname, mPusherId, pusherCity,
                        isFollow);
                mPopFollowUtils.show(mRlPlayRoot);
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_share:
                popWindowUtil = new PopWindowUtil(this);
                popWindowUtil.creatLiveSharePop(this);
                popWindowUtil.showAtBottom(mRlPlayRoot);
                break;
            case R.id.imgbtn_live_quit:
                finish();
                break;
        }
    }


    /**
     * 处理聊天类型消息
     *
     * @param userEntity
     * @param msg
     */
    private void handleMsgText(UserEntity userEntity, String msg) {
        ChatEntity entity = new ChatEntity();
        entity.setMsgSendLevel(1);
        entity.setMsgSendName(userEntity.getNickname());
        entity.setMsgText(msg);
        entity.setMsgType(Constants.TEXT_TYPE);
        notifyMsg(entity);
    }


    /**
     * 更新聊天列表
     *
     * @param entity
     */
    private void notifyMsg(final ChatEntity entity) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mChatMsgListAdapter.addMsg(entity);
            }
        });
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    /**
     * 开始播放，如果服务器返回异常信息，会直接退出activity。
     */

    protected void startPlay() {
        if (!checkPlayUrl()) {
            return;
        }

        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.clearLog();
        }
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(this);
        //极速模式
        mTXPlayConfig.setAutoAdjustCacheTime(true);
        mTXPlayConfig.setMinAutoAdjustCacheTime(1);
        mTXPlayConfig.setMaxAutoAdjustCacheTime(1);
        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result;
        result = mTXLivePlayer.startPlay(mPlayUrl, mUrlPlayType);

        //如果有错误，把异常信息带回到入口
        if (0 != result) {

            Intent rstData = new Intent();

            if (-1 == result) {
                Timber.d(getResources().getString(R.string.ERROR_MSG_NOT_QCLOUD_LINK));
                rstData.putExtra(Constants.ACTIVITY_RESULT, getResources().getString(R.string
                        .ERROR_MSG_NOT_QCLOUD_LINK));
            } else {
                Timber.d(getResources().getString(R.string.ERROR_RTMP_PLAY_FAILED));
                rstData.putExtra(Constants.ACTIVITY_RESULT, getResources().getString(R.string
                        .ERROR_RTMP_PLAY_FAILED));
            }

            mTXCloudVideoView.onPause();
            stopPlay(true);
            finish();
        } else {
            mPlaying = true;
        }
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mTXLivePlayer = null;
            mPlaying = false;
        }
    }

    private void quitRoom() {
        mIMChatRoomMgr.quitGroup(mGroupId);
        mIMChatRoomMgr.removeMsgListener();
        mPresenter.quitRoom(roomNumber);
    }

    /**
     * 判断一下地址是否为推流
     *
     * @return
     */
    private boolean checkPlayUrl() {
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl
                .startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast
                    .LENGTH_SHORT).show();
            return false;
        }

        if (mPlayUrl.startsWith("rtmp://")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) &&
                mPlayUrl.contains(".flv")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast
                    .LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 直播流事件监听
     * 很多直播流结束时不会抛出PLAY_EVT_PLAY_END，主播结束推流后，SDK 会很快发现数据流拉取失败（WARNING_RECONNECT）
     * 然后开始重试，直至三次重试失败后，抛出 PLAY_ERR_NET_DISCONNECT 事件。
     * 所以PLAY_ERR_NET_DISCONNECT也需要监听，用来作为直播结束标志
     *
     * @param i
     * @param bundle
     */
    @Override
    public void onPlayEvent(int i, Bundle bundle) {
        if (i == TXLiveConstants.PLAY_EVT_CONNECT_SUCC) {
            Timber.d("connect success");
        } else if (i == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            Timber.d("play begin");
        } else if (i == TXLiveConstants.PLAY_EVT_PLAY_END) {
            Timber.d("play end");
        } else if (i == TXLiveConstants.PLAY_WARNING_RECONNECT) {
            //提示网络缓慢
            Timber.d("net reconnect");
        } else if (i == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            //可以提示主播已断开
            Timber.d("net disconnect");
            showEndPop();
        }

    }

    /**
     * 当确定直播已经结束或者无法重连时调用
     */
    private void showEndPop() {
        if (popWindowUtil == null) {
            popWindowUtil = new PopWindowUtil(this);
            popWindowUtil.createLiveEndPop(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindowUtil.dismiss();
                    finish();
                }
            });

        } else if (!popWindowUtil.isShowing()) {
            popWindowUtil = new PopWindowUtil(this);
            popWindowUtil.createLiveEndPop(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindowUtil.dismiss();
                    finish();
                }
            });
        } else
            return;


        popWindowUtil.showInCenter(mRlPlayRoot);

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    //home键时直播界面要暂停，重新进入要恢复
    @Override
    protected void onResume() {
        super.onResume();
        if (!mPlaying) {
            if (mTXLivePlayer != null) {
                mTXLivePlayer.resume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTXLivePlayer != null) {
            mTXLivePlayer.pause();
            mPlaying = false;
        }
    }

    /**
     * 因为BaseActivity中butterknife会解绑view
     * 所以要先执行mTXCloudVideoView.onDestroy()，否则mTXCloudVideoView会为null
     */
    @Override
    public void onDestroy() {
        //先结束播放
        stopPlay(true); // true 代表清除最后一帧画面
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }
        mInputTextMsgDialog.release();
        quitRoom();
        super.onDestroy();
    }

    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname(UserInfoManager.getInstance().getNickName());
        handleMsgText(userEntity, msg);
        mIMChatRoomMgr.sendTextMessage(msg, TIMMessagePriority.Normal);
    }

    /**
     * 以下4个方法是IM中的回调
     *
     * @param code 错误码，成功时返回0，失败时返回相应错误码
     * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
     */
    @Override
    public void onJoinGroupCallback(int code, String msg) {
        Timber.d(msg + ":" + code);
    }

    @Override
    public void onSendMsgCallback(int code, TIMMessage timMessage) {
        if (code == 0) {
            Timber.d("sendMsg success");
        } else {
            Timber.d("sendMsg failed");
        }
    }

    @Override
    public void onReceiveMsg(int type, UserEntity userEntity, String content) {
        if (type == Constants.IMCMD_ENTER_LIVE) {
            handleMsgText(userEntity, getResources().getString(R.string.live_joinroom_notify));
            mUserAvatarListAdapter.addItem(userEntity);
        } else if (type == Constants.IMCMD_PAILN_TEXT) {
            handleMsgText(userEntity, content);
        } else if (type == Constants.IMCMD_EXIT_LIVE && userEntity.getId().equals(mPusherId)) {
            showEndPop();//当主播发出解散群聊的通知时，表明直播已结束
        } else if (type == Constants.IMCMD_EXIT_LIVE) {
            mUserAvatarListAdapter.removeItem(userEntity.getId());//普通成员退出时，如果头像在用户列表，删除掉
        } else if (type == Constants.IMCMD_DANMU) {

        } else if (type == Constants.IMCMD_PRESENT) {

        }
    }

    @Override
    public void onGroupDelete() {
        showEndPop();//当系统发来群被解散的消息（一般是后台操作的）
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String s = "";
        switch (checkedId) {
            case R.id.rb_share_circle:
                s = "朋友圈";
                break;
            case R.id.rb_share_qq:
                s = "qq";
                break;
            case R.id.rb_share_qzone:
                s = "qq空间";
                break;
            case R.id.rb_share_wb:
                s = "新浪微博";
                break;
            case R.id.rb_share_wx:
                s = "微信";
                break;
        }
        ToastUtil.show(s);
    }

    /**
     * 从业务服务器获取房间用户列表
     *
     * @param watcherEntityList
     */
    @Override
    public void getGroupMemberListSucc(List<WatcherEntity.WatchItem> watcherEntityList) {
        mRvUserAvatar.setVisibility(View.VISIBLE);
        mRecycleChatMsg.setVisibility(View.VISIBLE);
        LinkedList<UserEntity> linkedList = new LinkedList<>();
        for (WatcherEntity.WatchItem watchItem : watcherEntityList) {
            if (watchItem.getRole().equals("1")) {
                continue;
            }
            UserEntity userEntity = new UserEntity();
            userEntity.setId(watchItem.getId());
            userEntity.setAvatar(watchItem.getAvatar());
            userEntity.setNickname(watchItem.getNickname());
            linkedList.add(0, userEntity);
        }
        mUserAvatarListAdapter.addData(linkedList);
        UserEntity selfUserEntity = new UserEntity();
        selfUserEntity.setNickname(mNickname);
        handleMsgText(selfUserEntity, getResources().getString(R.string.live_joinroom_notify));
    }

    /**
     * 加入房间失败回调
     *
     * @param msg
     */
    @Override
    public void onJoinRequestFail(String msg) {
        ToastUtil.show(msg + ",加入房间失败");
        finish();
    }

    @Override
    public void onAttentionSuccess() {
        isFollow = !isFollow;
        if (isFollow) {
            ToastUtil.show(getResources().getString(R.string.live_attention_succ));
        } else {
            ToastUtil.show(getResources().getString(R.string.live_unattention_succ));
        }
    }

    @Override
    public void onAttentionFailed() {
        if (isFollow) {
            ToastUtil.show(getResources().getString(R.string.live_unattention_fail));
        } else {
            ToastUtil.show(getResources().getString(R.string.live_attention_fail));
        }
    }

}
