package com.src.isec.mvp.view.implement.activity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.ChatEntity;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.listener.PublishPhoneStateListener;
import com.src.isec.mvp.presenter.implement.PublisherPresenter;
import com.src.isec.mvp.view.IPublishView;
import com.src.isec.mvp.view.adapter.ChatMsgListAdapter;
import com.src.isec.mvp.view.adapter.UserAvatarListAdapter;
import com.src.isec.mvp.view.custom.InputTextMsgDialog;
import com.src.isec.mvp.view.custom.SpaceItemDecoration;
import com.src.isec.mvp.view.implement.fragment.DetailDialogFragment;
import com.src.isec.utils.ComfirmDialogUtils;
import com.src.isec.utils.IMChatRoomMgr;
import com.src.isec.utils.NavigationBarUtil;
import com.src.isec.utils.PicUtils;
import com.src.isec.utils.PopFollowUtils;
import com.src.isec.utils.PopWindowUtil;
import com.src.isec.utils.SwipeAnimationController;
import com.src.isec.utils.ToastUtil;
import com.src.isec.utils.sensetimeUtils.Accelerometer;
import com.src.isec.utils.sensetimeUtils.FileUtils;
import com.src.isec.utils.sensetimeUtils.STLicenseUtils;
import com.src.isec.widget.TCVideoView;
import com.src.isec.widget.beauty.BeautyDialogFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.TIMMessage;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;

import java.util.LinkedList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 直播界面
 * 开播界面流程：
 * 初始化→创建群组→获取推流地址→第三方分享→开始推流
 */
public class PublisherActivity extends BaseActivity<PublisherPresenter> implements IPublishView,
        ITXLivePushListener, BeautyDialogFragment.OnBeautyParamsChangeListener, IMChatRoomMgr.TCChatRoomListener {

    protected TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

    @BindView(R.id.video_view)
    TCVideoView mVideo;//播放界面
    @BindView(R.id.rv_user_avatar)
    RecyclerView rvUserAvatar;//用户列表
    @BindView(R.id.imgbtn_live_quit)
    ImageButton ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id_num)
    TextView tvId;
    @BindView(R.id.iv_follow)
    ImageView ivFollow;
    @BindView(R.id.iv_head_iocn)
    CircleImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    //    @BindView(R.id.ll_controllLayer)
//    LinearLayout mLlControllLayer;
    @BindString(R.string.no_perimisson)
    String noPerimisson;
    @BindString(R.string.TIPS_MSG_STOP_PUSH)
    String stopPushtTips;
    @BindString(R.string.ERROR_MSG_NET_DISCONNECTED)
    String errorNetTip;
    @BindView(R.id.rl_controllLayer)
    RelativeLayout mControllLayer;//控制层
    @BindView(R.id.rl_play_root)
    RelativeLayout rootView;
    @BindView(R.id.iv_beauty)
    ImageView ivBeauty;
    @BindView(R.id.recycle_chat_msg)
    RecyclerView mRecycleChatMsg;

    String nickName;
    String headPic;
    String userId;
    String location;
    boolean isFollow = false;
    private UserAvatarListAdapter mUserAvatarListAdapter;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private String mPusherId;
    private InputTextMsgDialog mInputTextMsgDialog;//发送消息的输入框
    private boolean mSharedNotPublished = false; //分享之后还未开始推流
    private PhoneStateListener mPhoneListener = null;
    //    private BeautyDialogFragment mBeautyDialogFragment;
    private int mScreenHeight;
    private TXLivePusher mTXLivePusher;//推流
    //手指滑动界面，各个控件消失
    private SwipeAnimationController swipeAnimationController;

    private boolean mPasuing;
    private RxPermissions rxPermissions;
    private String mPushUrl;//推流地址
    private BeautyDialogFragment.BeautyParams mBeautyParams = new BeautyDialogFragment.BeautyParams();
    private long mSecond;
    private long lMemberCount = 0;
    private long lHeartCount = 0;
    private long lTotalMemberCount = 0;
    private boolean isBeauty = true;//默认开启美颜
    private PopFollowUtils popFollowUtils;
    private String lat;
    private String longitude;
    private String cover;
    private String title;
    private String tmGroupId;//tm群聊id
    private IMChatRoomMgr imChatRoomMgr;
    private String roomNum;//房间号
    private Handler mHandler = new Handler();
    private Accelerometer mAccelerometer;//商汤使用的重力传感器

    public static final int MSG_DRAW_OBJECT_IMAGE = 4;
    public static final int MSG_CLEAR_OBJECT = 5;
    public static final int MSG_MISSED_OBJECT_TRACK = 6;
    public static final int MSG_DRAW_FACE_EXTRA_POINTS = 7;
    public final static int MSG_UPDATE_HAND_ACTION_INFO = 100;
    public final static int MSG_RESET_HAND_ACTION_INFO = 101;
    public final static int MSG_UPDATE_BODY_ACTION_INFO = 102;
    public final static int MSG_UPDATE_FACE_EXPRESSION_INFO = 103;
    public static final int MSG_NEED_UPDATE_TIMER = 8;
    public static final int MSG_NEED_START_CAPTURE = 9;
    public static final int MSG_NEED_START_RECORDING = 10;
    public static final int MSG_STOP_RECORDING = 11;
    public static final int MSG_SAVING_IMG = 1;
    public static final int MSG_SAVED_IMG = 2;
    public static final int MSG_DRAW_OBJECT_IMAGE_AND_RECT = 3;
    public static float[] DEFAULT_BEAUTIFY_PARAMS = {0.30f, 0.74f, 0.20f, 0.13f, 0.11f, 0.1f, 0.05f, 0.10f};


    @OnTouch(R.id.rl_play_root)
    boolean onTouch(View v, MotionEvent event) {
        return swipeAnimationController.processEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //进程后台时被系统强制kill，需重新checkLicense
        if (savedInstanceState != null && savedInstanceState.getBoolean("process_killed")) {
            if (!STLicenseUtils.checkLicense(this)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请检查License授权！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        //复制商汤的模型到sd卡
        FileUtils.copyModelFiles(this);
        //获取参数设置参数
        getData();
        initView();
        initAdaper();
        //先建立群组
//        createTmGroup();
        startPublish();
    }

    @Override
    public void initListener() {
        super.initListener();
        imChatRoomMgr = IMChatRoomMgr.getInstance();
        imChatRoomMgr.setMessageListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (mDanmuMgr != null) {
//            mDanmuMgr.destroy();
//            mDanmuMgr = null;
//        }
//        stopRecordAnimation();
        if (mVideo != null) {
            mVideo.onDestroy();
        }


        stopPublish();
        imChatRoomMgr.removeMsgListener();
//        mTCPusherMgr.setPusherListener(null);

        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 停止推流
     */
    private void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
    }

    /**
     * 获取参数
     */
    private void getData() {
        Intent intent = getIntent();
        roomNum = intent.getStringExtra("roomnum");
        mPushUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        lat = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        location = intent.getStringExtra("location");
        cover = intent.getStringExtra("cover");
        nickName = UserInfoManager.getInstance().getNickName();
        headPic = UserInfoManager.getInstance().getAvatar();
        userId = UserInfoManager.getInstance().getUserId();

        Glide.with(this)
                .load(headPic)
                .into(ivHead);
        tvName.setText(UserInfoManager.getInstance().getNickName());
        tvId.setText(userId);
        tvTitle.setText(title);
    }

    private void initView() {
        rxPermissions = new RxPermissions(this);
        //全屏下需要预留高度
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mControllLayer.getLayoutParams();
        params.topMargin = NavigationBarUtil.getStatusBarHeight(this);
        mControllLayer.setLayoutParams(params);

        //动效
//        mBeautyDialogFragment = new BeautyDialogFragment();
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        swipeAnimationController = new SwipeAnimationController(this);
        swipeAnimationController.setAnimationView(mControllLayer);

        mAccelerometer = new Accelerometer(getApplicationContext());
    }

    private void initAdaper() {
        //用户头像列表
        rvUserAvatar.setVisibility(View.VISIBLE);
        mUserAvatarListAdapter = new UserAvatarListAdapter(R.layout.recycle_item_user_avatar, mPusherId, new LinkedList<UserEntity>());
        rvUserAvatar.setAdapter(mUserAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvUserAvatar.setLayoutManager(linearLayoutManager);
        mUserAvatarListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        //聊天列表
        mRecycleChatMsg.setVisibility(View.VISIBLE);
        mChatMsgListAdapter = new ChatMsgListAdapter(R.layout.recycle_item_chat_list, new
                LinkedList<ChatEntity>(), mRecycleChatMsg);
        LinearLayoutManager msgLinearLayout = new LinearLayoutManager(this);
        msgLinearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleChatMsg.setLayoutManager(msgLinearLayout);
        mRecycleChatMsg.setAdapter(mChatMsgListAdapter);
        mRecycleChatMsg.addItemDecoration(new SpaceItemDecoration(5));

    }

    /**
     * 开始推流
     */
    protected void startPublish() {
        rxPermissions.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe(permission -> {
                    if (permission.granted) {
                        startPublishImpl();
                    } else {
                        ToastUtil.show(noPerimisson);
                    }
                });

    }


    private void startPublishImpl() {
        mSharedNotPublished = false;
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(this);
            //美颜设置
//            mBeautyDialogFragment.setBeautyParamsListner(mBeautyParams, this);
            mTXLivePusher.setPushListener(this);
            mTXPushConfig.setAutoAdjustBitrate(false);

            //切后台推流图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
            mTXPushConfig.setPauseImg(bitmap);
            // 300 为后台播放暂停图片的最长持续时间,单位是秒
            // 10 为后台播放暂停图片的帧率,最小值为 5,最大值为 20
            mTXPushConfig.setPauseImg(300, 5);
            //表示同时停止视频和音频采集，并且推送填充用的音视频流；
            mTXPushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
//            mTXPushConfig.setBeautyFilter(mBeautyParams.mBeautyProgress, mBeautyParams.mWhiteProgress, mBeautyParams.mRuddyProgress);
//            mTXPushConfig.setFaceSlimLevel(mBeautyParams.mFaceLiftProgress);
//            mTXPushConfig.setEyeScaleLevel(mBeautyParams.mBigEyeProgress);

            mTXPushConfig.enableHighResolutionCaptureMode(false);
            mTXLivePusher.setConfig(mTXPushConfig);

            mPhoneListener = new PublishPhoneStateListener(mTXLivePusher);
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            //设置来电状态监听
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
//        mAudioCtrl.setPusher(mTXLivePusher);
        if (mVideo != null) {
            mVideo.setVisibility(View.VISIBLE);
            mVideo.clearLog();
        }
        //mBeautySeekBar.setProgress(100);

        //设置视频质量：高清
        mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
        mVideo.enableHardwareDecode(true);
        mTXLivePusher.startCameraPreview(mVideo);
//        mTXLivePusher.setMirror(true);
        //临时切换到后置
//        mTXLivePusher.switchCamera();
        mTXLivePusher.startPusher(mPushUrl);
        //默认开启美颜
        mTXLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 6, 6, 6);
    }



    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (mVideo != null) {
            mVideo.setLogText(null, bundle, event);
        }
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
                showComfirmDialog(errorNetTip, true);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限，弹对话框强提醒，并退出
//                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL || event == TXLiveConstants.PUSH_ERR_MIC_RECORD_FAIL) { //未获得麦克风权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
//                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_MIC_FAIL);
            } else {
                //其他错误弹Toast弱提醒，并退出
                ToastUtil.show(bundle.getString(TXLiveConstants.EVT_DESCRIPTION));
                mVideo.onPause();
//                TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);
//                stopRecordAnimation();
                finish();
            }
        }

        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Log.d(TAG, "当前机型不支持视频硬编码");
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
            mTXPushConfig.setVideoBitrate(700);
            mTXPushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
            mTXPushConfig.enableHighResolutionCaptureMode(false);
            mTXLivePusher.setConfig(mTXPushConfig);
        } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
//            showNetBusyTips();
        }

        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
            //TODO 改变告诉服务器状态
//            TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Online);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private void showComfirmDialog(String title, boolean isError) {
        ComfirmDialogUtils comfirmDialogUtils = new ComfirmDialogUtils(this, title);
        comfirmDialogUtils.showComfirmDialog(isError, new ComfirmDialogUtils.OnComfirmListener() {
            @Override
            public void onSure() {
                stopPublish();
                quitRoom();
//                stopRecordAnimation();
                showDetailDialog();
            }

            @Override
            public void onErrorEvent() {
                //当情况为错误的时候，直接停止推流
                stopPublish();
                quitRoom();
            }

            @Override
            public void onErrorSure() {
//                stopRecordAnimation();
                showDetailDialog();
            }
        });
    }

    /**
     * TODO
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {
        imChatRoomMgr.deleteGroup();
        //主播退出房间
        mPresenter.exitRoom(roomNum);
    }

    public void showDetailDialog() {
        //确认则显示观看detail
//        stopRecordAnimation();
        DetailDialogFragment dialogFragment = new DetailDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("time", TimeUtils.formattedTime(mSecond));
//        args.putString("heartCount", String.format(Locale.CHINA, "%d", lHeartCount));
//        args.putString("totalMemberCount", String.format(Locale.CHINA, "%d", lTotalMemberCount));
//        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        if (dialogFragment.isAdded())
            dialogFragment.dismiss();
        else
            dialogFragment.show(getSupportFragmentManager(), "");

    }

    /**
     * 美颜设置
     *
     * @param params
     * @param key
     */
    @Override
    public void onBeautyParamsChange(BeautyDialogFragment.BeautyParams params, int key) {
        switch (key) {
            case BeautyDialogFragment.BEAUTYPARAM_BEAUTY:
            case BeautyDialogFragment.BEAUTYPARAM_WHITE:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setBeautyFilter(params.mBeautyStyle, params.mBeautyProgress, params.mWhiteProgress, params.mRuddyProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FACE_LIFT:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFaceSlimLevel(params.mFaceLiftProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_BIG_EYE:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setEyeScaleLevel(params.mBigEyeProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FILTER:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFilter(PicUtils.getFilterBitmap(getResources(), params.mFilterIdx));
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_MOTION_TMPL:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_GREEN:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setGreenScreenFile(PicUtils.getGreenFileName(params.mGreenIdx));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPasuing = true;
        mVideo.onPause();
        if (mTXLivePusher != null) {
//            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }
    }

    @Override
    public void onBackPressed() {
        showComfirmDialog(stopPushtTips, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO 弹幕类型
//        if (mDanmuMgr != null) {
//            mDanmuMgr.pause();
//        }

        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }

    }

    /**
     * 生命周期管理
     */
    @Override
    protected void onResume() {
        super.onResume();
//        if (mDanmuMgr != null) {
//            mDanmuMgr.resume();
//        }
        mVideo.onResume();

        if (mPasuing) {
            mPasuing = false;
            if (mTXLivePusher != null) {
                mTXLivePusher.resumePusher();
            }
        }

        if (mTXLivePusher != null) {
            mTXLivePusher.resumeBGM();
        }
//
//        if (mSharedNotPublished)
//            startPublish();
    }

    //美颜 目前不用提供手动选择，只需要一健化美颜
    @OnClick(R.id.iv_beauty)
    void onBeauty() {
        if (isBeauty) {
            isBeauty = false;
            mTXLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 0, 0, 0);
            ivBeauty.setImageResource(R.drawable.live_buttom_btn_beauty_off);
        } else {
            isBeauty = true;
            mTXLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, 6, 6, 6);
            ivBeauty.setImageResource(R.drawable.live_buttom_btn_beauty);
        }

        //如果需要手动设置，用这个
//        if (mBeautyDialogFragment.isAdded())
//            mBeautyDialogFragment.dismiss();
//        else
//            mBeautyDialogFragment.show(getSupportFragmentManager(), "");

    }

    @OnClick(R.id.iv_share)
    void onShare() {
//        ToastUtil.show("分享");
        PopWindowUtil popWindowUtil = new PopWindowUtil(this);
        popWindowUtil.creatLiveSharePop(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ToastUtil.show("checkedId:" + checkedId);
            }
        });
        popWindowUtil.showAtBottom(rootView);
    }

    @OnClick(R.id.imgbtn_live_quit)
    void onBack() {
        showComfirmDialog(stopPushtTips, false);
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
     * 聊天室创建成功
     *
     * @param code 错误码，成功时返回0，失败时返回相应错误码
     * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
     */
    @Override
    public void onJoinGroupCallback(int code, String msg) {

    }

    /**
     * 发送消息  主播端不用发送消息
     *
     * @param code       错误码，成功时返回0，失败时返回相应错误码
     * @param timMessage 发送的TIM消息
     */
    @Override
    public void onSendMsgCallback(int code, TIMMessage timMessage) {
        LogUtils.i("code:"+code);
    }


    /**
     * 接受消息
     *
     * @param type       消息类型
     * @param userEntity 发送者信息
     * @param content    内容
     */
    @Override
    public void onReceiveMsg(int type, UserEntity userEntity, String content) {
        if(type == Constants.IMCMD_ENTER_LIVE){
            handleMsgText(userEntity,getResources().getString(R.string.live_joinroom_notify));
            mUserAvatarListAdapter.addItem(userEntity);
        }else if(type == Constants.IMCMD_PAILN_TEXT){
            handleMsgText(userEntity,content);
        }else if(type == Constants.IMCMD_EXIT_LIVE){
            mUserAvatarListAdapter.removeItem(userEntity.getId());//普通成员退出时，如果头像在用户列表，删除掉
        }
    }


    /**
     * 聊天室解散
     */
    @Override
    public void onGroupDelete() {

    }



}
