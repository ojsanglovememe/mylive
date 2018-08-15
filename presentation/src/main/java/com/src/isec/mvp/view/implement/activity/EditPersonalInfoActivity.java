package com.src.isec.mvp.view.implement.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.config.GlideApp;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.src.isec.mvp.model.MenuEntity;
import com.src.isec.mvp.presenter.implement.EditPersonalInfoPresenter;
import com.src.isec.mvp.view.IEditPersonalInfoView;
import com.src.isec.mvp.view.IView;
import com.src.isec.mvp.view.custom.ActionSheetView;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.UpdatePersonalInfoEvent;
import com.src.isec.utils.MatisseManager;
import com.src.isec.utils.RxLifecycleUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 个人信息编辑
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class EditPersonalInfoActivity extends BaseActivity<EditPersonalInfoPresenter> implements IEditPersonalInfoView {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.ll_content_view)
    LinearLayout llContentView;

    private MediaStoreCompat mediaStoreCompat;
    private Uri mCropUri;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    protected View injectStateView() {
        return llContentView;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        mediaStoreCompat = MatisseManager.getMediaStoreCompat(mActivity);
        setActivityTitle(R.string.edit_personal_info);
        mPresenter.onPersonalInfo();
    }

    @OnClick({R.id.rl_avatar, R.id.rl_nick_name, R.id.rl_gender})
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.rl_avatar:
                showTakePhotoDialog(new ActionSheetView.ActionSheetOperateListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOperateListener(MenuEntity menuEntity, int position) {
                        RxPermissions rxPermissions = new RxPermissions(EditPersonalInfoActivity.this);
                        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .compose(RxLifecycleUtils.bindToLifecycle((IView) EditPersonalInfoActivity.this))
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        if(aBoolean){
                                            switch (position){
                                                case 0: //拍照
                                                    if (mediaStoreCompat != null) {
                                                        mediaStoreCompat.dispatchCaptureIntent(mContext, MatisseManager.REQUEST_CODE_CAPTURE);
                                                    }
                                                    break;
                                                case 1: //相册取
                                                    MatisseManager.singleSquarePicture(mActivity);
                                                    break;
                                            }
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
                    }
                });
                break;
            case R.id.rl_nick_name:
                Intent intent = new Intent(mContext, EditNickNameActivity.class);
                intent.putExtra(Constants.NICK_NAME, tvNickName.getText());
                startActivityForResult(intent, Constants.REQUEST_CODE_NICK_NAME);
                break;
            case R.id.rl_gender:
                showGenderDialog(new ActionSheetView.ActionSheetOperateListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOperateListener(MenuEntity menuEntity, int position) {
                        mPresenter.setSex(position == 0 ? "1" : "2", menuEntity.getMenuTitle());
                    }
                });
                break;
        }
    }

    private void showTakePhotoDialog(ActionSheetView.ActionSheetOperateListener listener) {
        String[] operTitles = mContext.getResources().getStringArray(R.array.take_photo_text);
        List<MenuEntity> list = new ArrayList<>();
        for (String str : operTitles) {
            list.add(new MenuEntity(str));
        }
        ActionSheetView.showOperateCancelDialog(mContext, list, listener);
    }

    private void showGenderDialog(ActionSheetView.ActionSheetOperateListener listener) {
        String[] operTitles = mContext.getResources().getStringArray(R.array.gender_text);
        List<MenuEntity> list = new ArrayList<>();
        for (String str : operTitles) {
            list.add(new MenuEntity(str));
        }
        ActionSheetView.showOperateCancelDialog(mContext, list, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.REQUEST_CODE_NICK_NAME:
                    if (data == null) {
                        return;
                    }
                    String nickName = data.getStringExtra(Constants.NICK_NAME);
                    if (TextUtils.isEmpty(nickName)) {
                        return;
                    }
                    tvNickName.setText(nickName);
                    break;
                case MatisseManager.REQUEST_CODE_CHOOSEMATISSE: //相册取
                    if (data == null) {
                        return;
                    }
                    Uri cropUri = Matisse.obtainSingleResult(data);
                    if(cropUri == null){
                        return;
                    }
                    mPresenter.uploadAvatar(cropUri);
                    break;
                case MatisseManager.REQUEST_CODE_CAPTURE:   //拍照
                    Uri contentUri = mediaStoreCompat.getCurrentPhotoUri();
//                    String path = mediaStoreCompat.getCurrentPhotoPath();
                    mCropUri = MatisseManager.startImageCrop(mActivity, contentUri);
                    break;
                case MatisseManager.REQUEST_CODE_CROP:  //拍照裁剪
                    if (data == null) {
                        return;
                    }
                    if(mCropUri == null){
                        return;
                    }
                    mPresenter.uploadAvatar(mCropUri);
                    break;
            }
        }
    }

    @Override
    public void onPersonalInfoSuccess(UserEntity userEntity) {

        if(userEntity == null){
            return;
        }

        GlideApp.with(mContext)
                .load(userEntity.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .centerCrop()
                .into(ivAvatar);

        tvNickName.setText(userEntity.getNickname());
        tvGender.setText(TextUtils.equals("1", userEntity.getSex()) ? R.string.gender_male : R.string.gender_female);
    }

    @Override
    public void onSexSuccess(String sexStr) {
        tvGender.setText(sexStr);
    }

    @Override
    public void onAvatarSuccess(Uri uri) {
        UserInfoManager.getInstance().setAvatar(uri.getPath());
        UpdatePersonalInfoEvent event = new UpdatePersonalInfoEvent();
        event.setAvatar(uri.getPath());
        RxBus.getInstance().send(event);
        GlideApp.with(mContext).load(uri).placeholder(R.drawable.ic_default_avatar).into(ivAvatar);
    }

}
