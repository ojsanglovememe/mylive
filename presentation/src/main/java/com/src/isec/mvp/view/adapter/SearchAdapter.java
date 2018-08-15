package com.src.isec.mvp.view.adapter;


import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.src.isec.R;
import com.src.isec.config.GlideApp;
import com.src.isec.domain.entity.LiveEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HuXiangLiang
 * @name IsecLive
 * @class name：com.src.isec.mvp.view.adapter
 * @class 首页搜索列表适配器
 * @time 2018/4/24 0010 10:00
 * @change
 * @chang time
 * @class describe
 */

public class SearchAdapter extends BaseQuickAdapter<LiveEntity, BaseViewHolder> {

    private String keyWord;

    public SearchAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveEntity item) {
        helper.setText(R.id.tv_attention, item.isFollow() ? R.string.btn_attention_yes : R.string.btn_attention)
                .setText(R.id.tv_nick_name, getTextHighLightByMatcher(getKeyWord(), item.getNickname(), false))
                .setText(R.id.tv_id, getTextHighLightByMatcher(getKeyWord(), item.getId(), true))
                .addOnClickListener(R.id.tv_attention)
                .getView(R.id.tv_attention).setSelected(item.isFollow());

        GlideApp.with(mContext)
                .load(item.getCover())
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_default)
                .into((ImageView) helper.getView(R.id.iv_avatar));
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    /**
     * 字符串高亮显示部分文字
     * @param str1      要高亮显示的文字（输入的关键词）
     * @param str2      包含高亮文字的字符串
     */
    private SpannableString getTextHighLightByMatcher(String str1, String str2, boolean isID) {
        if(TextUtils.isEmpty(str2)){
            return new SpannableString(" ");
        }
        str2 = isID ? "ID：" + str2 : str2;
        SpannableString sp = new SpannableString(str2);
        if(TextUtils.isEmpty(str1)){
            return sp;
        }
        for (int i = 0 ; i < str1.length() ; i++){
            String s = String.valueOf(str1.charAt(i));
            // 正则匹配 忽略大小写
            Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(sp);
            // 查找下一个
            while (m.find()) {
                // 字符开始下标
                int start = m.start();
                // 字符结束下标
                int end = m.end();
                // 设置高亮
                sp.setSpan(new ForegroundColorSpan(Color.parseColor("#f65d79")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return sp;
    }
}
