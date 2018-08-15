package com.src.isec.config;

/**
 * @author sunmingchuan
 * @time 2018/4/9 11:13
 */

public interface Constants {
    /**
     * IM相关
     */
    int SDKAPPID = 1400082115;
    String ACCOUNTTYPE = "24837";



    /**
     * QQ社会化分享
     */
    String QQ_APP_ID="1106756289";

    /**
     * 微博社会化分享
     */
    String WEIBO_ID="4157576381";

    /***
     * 实名认证类型
     */
    String AUTH_TYPE = "authType";
    String AUTH_SUCCESS = "authSuccess";

    /**
     * 直播间字段
     */
    String PUBLISH_URL      = "publish_url";
    String ROOM_ID          = "room_id";
    String ROOM_TITLE       = "room_title";
    String COVER_PIC        = "cover_pic";
    String BITRATE          = "bitrate";
    String GROUP_ID         = "group_id";
    String PLAY_URL         = "play_url";
    String PUSHER_AVATAR    = "pusher_avatar";
    String PUSHER_ID        = "pusher_id";
    String PUSHER_NAME        = "pusher_name";
    String MEMBER_COUNT     = "member_count";
    String FILE_ID          = "file_id";
    String TIMESTAMP        = "timestamp";
    String ACTIVITY_RESULT  = "activity_result";
    String SHARE_PLATFORM   = "share_platform";

    //直播端右下角listview显示type
    int TEXT_TYPE           = 0;
    int MEMBER_ENTER        = 1;
    int MEMBER_EXIT         = 2;

    String TAB_TAG_ID = "tagId";
    String LIVE_TYPE = "liveType";
    int LIVE_TYPE_HOT = 1;
    int LIVE_TYPE_NEARBY = 2;

    /**热门直播列表***/
    String LIVE_TYPE_HOT_STR = "hot";
    /**附近直播列表***/
    String LIVE_TYPE_NEARBY_STR = "nearby";
    /**关注直播列表***/
    String LIVE_TYPE_FOLLOW = "follow";
    /**粉丝直播列表***/
    String LIVE_TYPE_FANS = "fans";
    /**推荐直播列表***/
    String LIVE_TYPE_RECOMMEND = "recommend";

    String NICK_NAME = "nickName";
    String LOGIN_MOBILE = "mobile";
    String BINDING_MOBILE = "bindingMobile";
    int REQUEST_CODE_NICK_NAME = 10;

    /***分页数量****/
    int PAGE_NUM = 20;


    /**
     * IM 互动消息类型
     */
    int IMCMD_PAILN_TEXT    = 1;   // 文本消息
    int IMCMD_ENTER_LIVE    = 2;   // 用户加入直播
    int IMCMD_EXIT_LIVE     = 3;   // 用户退出直播
    int IMCMD_DANMU         = 4;   // 弹幕消息
    int IMCMD_PRESENT       = 5;   // 礼物消息

    //IM登录失败重新尝试次数
    int IMLOGIN_COUNT = 3;


}
