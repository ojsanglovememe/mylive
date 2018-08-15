package com.src.isec.mvp.view.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.src.isec.R;
import com.src.isec.utils.ToastUtil;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.custom
 * @class 直播间自定义的dialog，用来输入弹幕
 * @time 2018/4/12 11:01
 * @change
 * @chang time
 * @class describe
 */

public class InputTextMsgDialog extends Dialog {
    public interface OnTextSendListener {

        void onTextSend(String msg, boolean tanmuOpen);
    }
    private Button btnDanmu,btnSend;
    private LinearLayout mDanmuArea;
    private EditText messageTextView;
    private static final String TAG = InputTextMsgDialog.class.getSimpleName();
    private Context mContext;
    private InputMethodManager imm;
    private RelativeLayout rlDlg;
    private int mLastDiff = 0;
    private LinearLayout mConfirmArea;
    private OnTextSendListener mOnTextSendListener;
    private boolean mDanmuOpen = false;
//    private final String reg = "[`~@#$%^&*()-_+=|{}':;,/.<>￥…（）—【】‘；：”“’。，、]";
//    private Pattern pattern = Pattern.compile(reg);

    public InputTextMsgDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.dialog_input_text);

        messageTextView = findViewById(R.id.et_input_message);
        messageTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        //修改下划线颜色
        messageTextView.getBackground().setColorFilter(context.getResources().getColor(R.color.transparent), PorterDuff.Mode.CLEAR);
        btnDanmu =  findViewById(R.id.btn_danmu);
        btnSend = findViewById(R.id.btn_send);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        btnDanmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmuOpen = !mDanmuOpen;
                if (mDanmuOpen) {
                    btnDanmu.setBackgroundResource(R.drawable.danmu_btn_on);
                } else {
                    btnDanmu.setBackgroundResource(R.drawable.danmu_btn_off);
                }
            }
        });

        mDanmuArea = (LinearLayout) findViewById(R.id.ll_danmu_area);
        mDanmuArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmuOpen = !mDanmuOpen;
                if (mDanmuOpen) {
                    btnDanmu.setBackgroundResource(R.drawable.danmu_btn_on);
                } else {
                    btnDanmu.setBackgroundResource(R.drawable.danmu_btn_off);
                }
            }
        });

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case KeyEvent.KEYCODE_ENDCALL:
                    case KeyEvent.KEYCODE_ENTER:
                        String msg = messageTextView.getText().toString().trim();
                        if (!TextUtils.isEmpty(msg)) {
                            mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                            imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
//                            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                            messageTextView.setText("");
//                            dismiss();
                        } else {
                            ToastUtil.show(mContext.getResources().getString(R.string.empty_input));
                        }
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        dismiss();
                        return false;
                    default:
                        return false;
                }
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {

                    mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                    imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
//                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    messageTextView.setText("");
//                    dismiss();
                } else {
                    ToastUtil.show(mContext.getResources().getString(R.string.empty_input));
                }
                messageTextView.setText(null);
            }
        });

        mConfirmArea = (LinearLayout) findViewById(R.id.ll_confirm_area);
        mConfirmArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {

                    mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                    imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
//                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    messageTextView.setText("");
//                    dismiss();
                } else {
                    ToastUtil.show(mContext.getResources().getString(R.string.empty_input));
                }
                messageTextView.setText(null);
            }
        });

        rlDlg = (RelativeLayout) findViewById(R.id.rl_outside_view);
        rlDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.rl_inputdlg_view)
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    dismiss();
            }
        });

        final RelativeLayout rldlgview = findViewById(R.id.rl_inputdlg_view);

        rldlgview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Rect r = new Rect();
                //获取当前界面可视部分
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;

                if (heightDifference <= 0 && mLastDiff > 0){
//                    dismiss();
                }
                mLastDiff = heightDifference;
            }
        });
        rldlgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
//                dismiss();
            }
        });
    }

    public void setmOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 退出直播间时调用
     */
    public void release(){
        imm = null;
        mOnTextSendListener = null;
        mContext = null;
    }
}
