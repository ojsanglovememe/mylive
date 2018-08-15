package com.src.isec.mvp.view.implement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.src.isec.R;
import com.src.isec.base.BaseActivity;
import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.mvp.presenter.implement.EditPersonalInfoPresenter;
import com.src.isec.mvp.view.IEditPersonalInfoView;
import com.src.isec.mvp.view.custom.ClearEditText;
import com.src.isec.reactivex.rxbus.RxBus;
import com.src.isec.reactivex.rxbus.event.UpdatePersonalInfoEvent;

import butterknife.BindView;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.implement.fragment
 * @class 编辑昵称
 * @time 2018/4/10 0010 10:45
 * @change
 * @chang time
 * @class describe
 */
public class EditNickNameActivity extends BaseActivity<EditPersonalInfoPresenter> implements IEditPersonalInfoView {


    @BindView(R.id.et_nick_name)
    ClearEditText etNickName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_nick_name;
    }

    @Override
    protected void initialize(@Nullable Bundle savedInstanceState) {
        setActivityRightTitle(R.string.nick_name, R.string.btn_sure, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = etNickName.getText().toString().trim();
                if(TextUtils.isEmpty(nickName)){
                    showToast(R.string.input_nick_name);
                    return;
                }
                mPresenter.setNickName(nickName);
            }
        });

        String nickName = getIntent().getStringExtra(Constants.NICK_NAME);
        if(!TextUtils.isEmpty(nickName)){
            etNickName.setText(nickName);
            etNickName.setSelection(nickName.length());
        }
    }

    @Override
    public void initListener() {
        super.initListener();

    }

    @Override
    public void onNicknameSuccess(String nickname) {
        UpdatePersonalInfoEvent event = new UpdatePersonalInfoEvent();
        event.setNickname(nickname);
        RxBus.getInstance().send(event);
        UserInfoManager.getInstance().setNickName(nickname);
        Intent intent = getIntent();
        intent.putExtra(Constants.NICK_NAME, nickname);
        setResult(RESULT_OK, intent);
        finish();
    }
}
