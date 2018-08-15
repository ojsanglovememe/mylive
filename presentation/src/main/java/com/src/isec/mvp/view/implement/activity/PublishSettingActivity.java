package com.src.isec.mvp.view.implement.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jakewharton.rxbinding2.view.RxView;
import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.model.MenuEntity;
import com.src.isec.mvp.presenter.implement.PublishSettingPresenter;
import com.src.isec.mvp.view.IPublishSettingView;
import com.src.isec.mvp.view.IView;
import com.src.isec.mvp.view.custom.ActionSheetView;
import com.src.isec.utils.IMChatRoomMgr;
import com.src.isec.utils.MatisseManager;
import com.src.isec.utils.RxLifecycleUtils;
import com.src.isec.utils.TCLocationHelper;
import com.src.isec.utils.ToastUtil;
import com.src.isec.utils.sensetimeUtils.STLicenseUtils;
import com.src.isec.widget.RoundImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.TIMMessage;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

/**
 * @author wj
 * @name
 * @class name：
 * @class describe  开播设置界面
 * @time
 * @change
 * @chang time
 * @class describe
 */
public class PublishSettingActivity extends BaseActivity<PublishSettingPresenter> implements
        IPublishSettingView,
        TCLocationHelper.OnLocationListener, CompoundButton
        .OnCheckedChangeListener, IMChatRoomMgr.TCChatRoomListener {
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int CROP_CHOOSE = 10;

    @BindView(R.id.tv_location)
    TextView tvLoaction;//定位
    @BindView(R.id.iv_cancel)
    ImageView ivBack;//返回键
    @BindView(R.id.rv_cover)
    RoundImageView rvCover;//封面
    @BindView(R.id.btn_change_cover)
    Button btnChangeCover;//切换封面
    @BindView(R.id.et_input_tilte)
    EditText etInput;//主题输入

    @BindView(R.id.cb_share_circle)
    CheckBox cbCircle;
    @BindView(R.id.cb_share_qzone)
    CheckBox cbQzone;
    @BindView(R.id.cb_share_qq)
    CheckBox cbQQ;
    @BindView(R.id.cb_share_wb)
    CheckBox cbWb;
    @BindView(R.id.cb_share_wx)
    CheckBox cbWx;

    @BindView(R.id.tv_publish)
    TextView tvPublish;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindString(R.string.title_is_no_empty)
    String noEmpty;
    @BindString(R.string.no_perimisson)
    String noPerimisson;
    @BindString(R.string.no_net)
    String noNet;
    private boolean isPermissions = false;//是否可以开播
    private RxPermissions rxPermissions;
    private Uri fileUri;
    private Uri cropUri = null;
    private String latitude;
    private String longitude;
    private String mCoverUrl;//封面网络地址
    private String location;
    private CompoundButton mCbLastChecked;
    private IMChatRoomMgr imChatRoomMgr;


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarView(R.id.title_top_view)
                .keyboardEnable(true,
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_setting;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        getCover();

    }

    /**
     * 获取默认封面图片
     */
    private void getCover() {
        //获取封面
        mCoverUrl = UserInfoManager.getInstance().getAvatar();
        if (!TextUtils.isEmpty(mCoverUrl)) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_default_avatar);
            requestOptions.error(R.drawable.ic_default_avatar);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions).load(mCoverUrl).into(rvCover);

        } else {
            rvCover.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        cbWx.setOnCheckedChangeListener(this);
        cbCircle.setOnCheckedChangeListener(this);
        cbQQ.setOnCheckedChangeListener(this);
        cbQzone.setOnCheckedChangeListener(this);
        cbWb.setOnCheckedChangeListener(this);

        RxView.clicks(tvPublish).throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) PublishSettingActivity.this))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (!isPermissions) {
                            ToastUtil.show(noPerimisson);
                        } else {

                            createTmGroup();

//                            if (null == cropUri || TextUtils.isEmpty(cropUri.getPath())) {
//                                //如果没有更换封面，直接请求群聊id
//
//                            } else {
//                                mPresenter.uploadCover(cropUri.getPath());
//                                ToastUtil.show("正在上传封面");
//                            }
                        }
                    }
                });

    }

    /**
     * 创建聊天室
     */
    private void createTmGroup() {
        //初始化消息回调 聊天室
        imChatRoomMgr = IMChatRoomMgr.getInstance();
//        //聊天的监听：加入群组，发送消息，接受消息，群组删除
        imChatRoomMgr.setMessageListener(this);
        imChatRoomMgr.createGroup();
    }


    @Override
    protected void onStart() {
        super.onStart();
        initPermission();
    }

    /**
     * 访问“相机、麦克风、位置”，
     * “相机、麦克风”不授权则无法开播，“位置”不授权可以开播，
     * 默认分享方式是“朋友圈”，不强制分享
     */


    private void initPermission() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(permission -> {
                    if (permission.name.equals(Manifest.permission.CAMERA)) {
                        if (permission.granted) {
                            isPermissions = true;
                        } else {
                            isPermissions = false;
                            ToastUtil.show(noPerimisson);
                        }

                    }

                    if (permission.name.equals(Manifest.permission.RECORD_AUDIO) && !permission
                            .granted) {
                        if (permission.granted) {
                            isPermissions = true;
                        } else {
                            isPermissions = false;
                            tvPublish.setEnabled(false);
                        }

                    }

                    if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //开始定位
                        if (permission.granted) {
                            TCLocationHelper.getMyLocation(PublishSettingActivity.this,
                                    PublishSettingActivity.this);
                        } else {
                            tvLoaction.setText("定位·关");
                        }

                    }


                });
    }

    @OnClick(R.id.iv_cancel)
    void onBack() {
        //返回
        onBackPressed();
    }

    //点击定位
    @OnClick(R.id.tv_location)
    void onLoacation() {
        ToastUtil.show("更新定位");
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        TCLocationHelper.getMyLocation(PublishSettingActivity.this,
                                PublishSettingActivity.this);
                    } else {
                        tvLoaction.setText("定位·关");
                    }
                });
    }

    //切换封面
    @OnClick(R.id.btn_change_cover)
    void onChangeCover() {
//        pickPicDialogUtils.show();

        showTakePhotoDialog(new ActionSheetView.ActionSheetOperateListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOperateListener(MenuEntity menuEntity, int position) {
                if (position == 0) {
                    //拍照
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                        if (aBoolean) {
                            getPicFrom(CAPTURE_IMAGE_CAMERA);
                        } else {
                            ToastUtil.show(noPerimisson);
                        }
                    });
                } else if (position == 1) {
                    //相册
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                        if (aBoolean) {
//                getPicFrom(IMAGE_STORE);
                            MatisseManager.singleSquarePicture(PublishSettingActivity.this);
                        } else {
                            ToastUtil.show(noPerimisson);
                        }
                    });
                }
            }
        });
    }

    private void showTakePhotoDialog(ActionSheetView.ActionSheetOperateListener listener) {
        String[] operTitles = mContext.getResources().getStringArray(R.array.take_photo_text);
        List<MenuEntity> list = new ArrayList<>();
        for (String str : operTitles) {
            list.add(new MenuEntity(str));
        }
        ActionSheetView.showOperateCancelDialog(mContext, list, listener);
    }


    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUriFromCamera();
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent_photo.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent_photo.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    /**
     * 从拍照选择图片
     *
     * @return
     */
    private Uri createCoverUriFromCamera() {
        Uri imageUri;
        String filename = "zhangling" + System.currentTimeMillis() + ".jpg";
        String path = Environment.getExternalStorageDirectory() + File.separator + "zhanglingzhibo";
        File outputImage = new File(path, filename);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            //7.0版本开始，直接使用本地真实路径的Uri被认为不安全，会抛出一个FileUriExposedException异常
            //参数2 ：任意唯一的字符串
            imageUri = FileProvider.getUriForFile(this, "com.src.isec.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        return imageUri;
    }

    /**
     * 获取到位置
     *
     * @param code
     * @param lat1
     * @param long1
     * @param location
     */
    @Override
    public void onLocationChanged(int code, double lat1, double long1, String location) {
        latitude = String.valueOf(lat1);
        longitude = String.valueOf(long1);
        String[] strings = location.split(" ");
        if (!TextUtils.isEmpty(strings[0])) {
            this.location = strings[0];
        } else {
            this.location = location;
        }
        tvLoaction.setText(this.location);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA://拍照返回
//                    //TODO 选择完图片后，需要上传服务器，在获取服务器返回的图片地址，并保存在本地数据中
                    startPhotoZoom(fileUri);
                    break;

                case MatisseManager.REQUEST_CODE_CHOOSEMATISSE://相册选择
                    cropUri = null;
                    cropUri = Matisse.obtainSingleResult(data);
                    Glide.with(PublishSettingActivity.this).load(cropUri).into(rvCover);
                    break;
                case CROP_CHOOSE://切割图片
                    //先显示图片，不上传
                    Glide.with(PublishSettingActivity.this).load(cropUri).into(rvCover);
                    break;

            }
        }
    }

    /**
     * 切割图片
     */
    private void startPhotoZoom(Uri uri) {
        cropUri = createCoverUri("_crop");

        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("return-data", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 750);
        intent.putExtra("aspectY", 550);
        intent.putExtra("outputX", 750);
        intent.putExtra("outputY", 550);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    public Uri createCoverUri(String type) {
        //TODO 命名规范需要确定
        String filename = "zhangling" + type + System.currentTimeMillis() + ".jpg";
        String path = Environment.getExternalStorageDirectory() + File.separator + "zhanglingzhibo";
        File outputImage = new File(path, filename);
        try {
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (outputImage.exists()) {
                outputImage.delete();
            }
//            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("生成封面失败");
        }
        return Uri.fromFile(outputImage);
    }

    /**
     * 监听标题输入框
     *
     * @param s
     */
    @OnTextChanged(value = R.id.et_input_tilte, callback = AFTER_TEXT_CHANGED)
    void onTextChange(Editable s) {
        if (s.length() == 0) {
            tvPublish.setEnabled(false);
        } else {
            tvPublish.setEnabled(true);
        }
        if (s.length() > 10) {
            tvTip.setVisibility(View.VISIBLE);
            tvPublish.setEnabled(false);
        } else {
            tvTip.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 点击分享
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == false) {
            mCbLastChecked = null;
            return;
        }
        if (mCbLastChecked != null) {
            mCbLastChecked.setChecked(false);
        }

        mCbLastChecked = buttonView;
        //勾选
        switch (buttonView.getId()) {
            case R.id.cb_share_wx:
                break;
            case R.id.cb_share_circle:
                break;
            case R.id.cb_share_qq:
                break;
            case R.id.cb_share_qzone:
                break;
            case R.id.cb_share_wb:
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetUrl(String url, String roomNum) {
        //跳转之前，需要判断商汤是否可用
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                if (!STLicenseUtils.checkLicense(PublishSettingActivity.this)) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) PublishSettingActivity.this))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            ToastUtil.show("请检查License授权！");
                        } else {
                            //跳转直播界面
                            Intent intent = new Intent(PublishSettingActivity.this, PublisherActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("cover", mCoverUrl);
                            intent.putExtra("location", location);
                            intent.putExtra("title", etInput.getEditableText().toString());
                            intent.putExtra("roomnum", roomNum);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if (0 == code) {
            //拿到群聊id后，就可以取消监听了
            IMChatRoomMgr.getInstance().removeListener();
            if (cropUri == null) {
                mPresenter.createRoom(mCoverUrl, etInput.getEditableText().toString(), latitude,
                        longitude, location, msg);
            } else {
                mPresenter.createRoom(cropUri, etInput.getEditableText().toString(), latitude,
                        longitude, location, msg);
            }

        } else {


        }

    }

    @Override
    public void onSendMsgCallback(int code, TIMMessage timMessage) {

    }

    @Override
    public void onReceiveMsg(int type, UserEntity userInfo, String content) {

    }

    @Override
    public void onGroupDelete() {

    }

}
