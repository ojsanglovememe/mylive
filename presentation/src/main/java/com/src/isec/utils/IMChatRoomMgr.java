package com.src.isec.utils;

import android.text.TextUtils;
import android.util.Log;

import com.src.isec.config.Constants;
import com.src.isec.data.utils.UserInfoManager;
import com.src.isec.domain.entity.UserEntity;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMMessagePriority;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.TXLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.utils
 * @class IM聊天室管理类
 * @time 2018/4/18 16:15
 * @change
 * @chang time
 * @class describe
 */

public class IMChatRoomMgr implements TIMMessageListener {

    public static final String TAG = IMChatRoomMgr.class.getSimpleName();
    private TIMConversation mGroupConversation;
    private TCChatRoomListener mTCChatRoomListener;
    private String mRoomId;
    private static String mUserId;

    private static IMFrequeControl mLikeFreque;
    private static IMFrequeControl mMsgFreque;

    private IMChatRoomMgr() {
        mLikeFreque = new IMFrequeControl();
        mLikeFreque.init(10, 1);

        mMsgFreque = new IMFrequeControl();
        mMsgFreque.init(20, 1);
    }

    //单例模式，静态内部类方式
    private static class IMChatRoomMgrHolder{
        private static IMChatRoomMgr instance = new IMChatRoomMgr();
    }

    public static IMChatRoomMgr getInstance(){
        return IMChatRoomMgrHolder.instance;
    }

    /**
     * 注入消息回调监听类
     * 需要实现并注入TCChatRoomListener才能获取相应消息回调
     *
     * @param listener
     */
    public void setMessageListener(TCChatRoomListener listener) {
        mTCChatRoomListener = listener;
        TIMManager.getInstance().addMessageListener(this);
    }

    /**
     * 退出房间
     */
    public void removeMsgListener() {
        mTCChatRoomListener = null;
        TIMManager.getInstance().removeMessageListener(this);
        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
    }

    public void removeListener(){
        mTCChatRoomListener = null;
        TIMManager.getInstance().removeMessageListener(this);
    }



    /**
     * 登录态检测
     * 在进行createRoom/joinGroup操作时检测用户是否处于登录态
     * @param
     */
    private boolean checkLoginState() {

       return IMMgrUtil.getIMLoginState();
    }

    /**
     * 主播创建直播群组接口
     */
    public void createGroup() {
            if(checkLoginState()) {
                //用户登录，创建直播间
                TIMGroupManager.getInstance().createAVChatroomGroup("TVShow", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.d(TAG, "create av group failed. code: " + code + " errmsg: " + msg);

                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(code, msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        Log.d(TAG, "create av group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(0, roomId);
                    }
                });
            }else {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(-1, "用户未登录IM");
            }
    }

    /**
     * 主播端退出前删除直播群组
     */
    public void deleteGroup() {

        sendMessage(Constants.IMCMD_EXIT_LIVE, "",TIMMessagePriority.High);

        if (mRoomId == null)
            return;

        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "delete av group failed. code: " + code + " errmsg: " + msg);
                mRoomId = null;
                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "delete av group succ. groupid: " + mRoomId);
                mRoomId = null;
                mTCChatRoomListener = null;
            }
        });
    }

    /**
     * 用户加入群组
     * @param roomId
     */
    public void joinGroup(final String roomId) {
        if(checkLoginState()) {
            //用户登录，加入房间
            mRoomId = roomId;
            TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "joingroup failed, code:" + i + ",msg:" + s);
                    mRoomId = null;
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onJoinGroupCallback(i, s);
                    else
                        Log.d(TAG, "mPlayerListener not init");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "joingroup success, groupid:" + roomId);
                    mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);

                    //加入群组成功后，发送加入消息
                    sendMessage(Constants.IMCMD_ENTER_LIVE, "",TIMMessagePriority.High);
                    if (null != mTCChatRoomListener) {
                        mTCChatRoomListener.onJoinGroupCallback(0, roomId);
                    } else {
                        Log.d(TAG, "mPlayerListener not init");
                    }
                }
            });
        }else {
            if (null != mTCChatRoomListener)
                mTCChatRoomListener.onJoinGroupCallback(-1, "用户未登录IM");
        }

    }

    /**
     * 观看者退出退出群组接口
     *
     * @param roomId 群组Id
     */
    public void quitGroup(final String roomId) {

        sendMessage(Constants.IMCMD_EXIT_LIVE, "",TIMMessagePriority.High);

        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "quitGroup failed, code:" + i + ",msg:" + s);

                mTCChatRoomListener = null;
                mGroupConversation = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "quitGroup success, groupid:" + roomId);
                mTCChatRoomListener = null;
                mGroupConversation = null;
            }
        });
    }

    /**
     * 发送文本聊天消息
     * @param msg
     */
    public void sendTextMessage(String msg,TIMMessagePriority messagePriority) {
        sendMessage(Constants.IMCMD_PAILN_TEXT, msg,messagePriority);
    }

    /**
     * 发送消息
     *
     * @param cmd   控制符（代表不同的消息类型）具体查看Contants.IMCMD开头变量
     * @param param 参数
     */
    private void sendMessage(int cmd, String param, TIMMessagePriority messagePriority) {

        sendMessage(cmd, param, messagePriority, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                TXLog.d("send cmd ", "error:" + s);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(-1, null);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0, timMessage);
            }
        });
    }

    /**
     * 发送消息
     * @param msg              TIM消息
     * @param timValueCallBack 发送消息回调类
     */
    private void sendTIMMessage(TIMMessage msg, TIMValueCallBack<TIMMessage> timValueCallBack) {
        if (mGroupConversation != null)
            mGroupConversation.sendMessage(msg, timValueCallBack);
    }

    private void sendMessage(int cmd, String param, TIMMessagePriority messagePriority, TIMValueCallBack<TIMMessage> timValueCallBack) {
        //获取本地用户信息
        UserInfoManager userInfoManager = UserInfoManager.getInstance();
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("userAction", cmd);
            sendJson.put("userId", userInfoManager.getUserId());
            sendJson.put("nickName", userInfoManager.getNickName());
            sendJson.put("headPic", userInfoManager.getAvatar());
            sendJson.put("msg", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();

        if (mGroupConversation != null)
            Log.i(TAG, "send cmd : " + cmd + "|" + cmds + "|" + mGroupConversation.toString());

        TIMMessage msg = new TIMMessage();

        msg.setPriority(messagePriority);//设置消息优先级

        TIMTextElem elem = new TIMTextElem();
        elem.setText(cmds);

        if (msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

        sendTIMMessage(msg, timValueCallBack);

    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        parseIMMessage(list);
        return false;
    }

    /**
     * 解析TIM消息列表
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {
        //TODO 重新整理一下消息类型的区分和处理
        if (list.size() > 0) {
            if (mGroupConversation != null)
                mGroupConversation.setReadMessage(list.get(0));
            Log.d(TAG, "parseIMMessage readMessage " + list.get(0).timestamp());
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = list.get(i);

            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();
                TIMUserProfile timUserProfile = currMsg.getSenderProfile();

                if (sendId.equals(mUserId)) {
                    TXLog.d(TAG, "recevie a self-msg type:" + type.name());
                    continue;
                }

                //定制消息 -- 已弃用（不支持敏感词过滤）
                if (type == TIMElemType.Custom) {
                    continue;
                }

                //消息超过限制，丢弃
                if (!mMsgFreque.canTrigger()) {
                    break;
                }

                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        //群被解散
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onGroupDelete();
                    }

                }

                //其他群消息过滤
                //getPeer会话标识，单聊情况下为对方帐号 identifier，群聊情况下为群组 ID。
                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null)
                    //对于群组消息，过滤非本群的消息
                    if (TIMConversationType.Group == currMsg.getConversation().getType()
                            && mRoomId != null
                            && !mRoomId.equals(currMsg.getConversation().getPeer()))
                    {
                        continue;
                    }

                //最后处理文本消息
                if (type == TIMElemType.Text) {

                    handleCustomTextMsg(elem, timUserProfile);
                }
            }
        }
    }

    /**
     * 处理定制消息 赞 加入 退出 弹幕 并执行相关回调
     * @param elem 消息体
     */
    private void handleCustomTextMsg(TIMElem elem, TIMUserProfile timUserProfile) {
        //TODO 加入送礼物时，json字符串要多一个字段，动画的文件名（如果用Lottie）
        try {

            if (elem.getType() != TIMElemType.Text)
                return;

            String jsonString = ((TIMTextElem) elem).getText();
            Log.i(TAG, "cumstom msg  " + jsonString);

            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = (int) json.get("userAction");
            String userId = (String) json.get("userId");
            String nickname = (String) json.get("nickName");
            nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
            String headPic = (String) json.get("headPic");
            UserEntity userEntity = new UserEntity();
            userEntity.setId(userId);
            userEntity.setNickname(nickname);
            userEntity.setAvatar(headPic);
            switch (action) {
                case Constants.IMCMD_ENTER_LIVE:
                case Constants.IMCMD_EXIT_LIVE:
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, userEntity, null);
                    break;
                case Constants.IMCMD_PAILN_TEXT:
                case Constants.IMCMD_DANMU:
                    String msg = (String) json.get("msg");
                    if (null != mTCChatRoomListener)
                        mTCChatRoomListener.onReceiveMsg(action, userEntity, msg);
                    break;
                default:
                    break;
            }

        } catch (ClassCastException e) {
            String senderId = timUserProfile.getIdentifier();
            String nickname = timUserProfile.getNickName();
            String headPic = timUserProfile.getFaceUrl();
            nickname = TextUtils.isEmpty(nickname) ? senderId : nickname;
            UserEntity userEntity = new UserEntity();
            userEntity.setId(senderId);
            userEntity.setNickname(nickname);
            userEntity.setAvatar(headPic);
            mTCChatRoomListener.onReceiveMsg(Constants.IMCMD_PAILN_TEXT,
                   userEntity, ((TIMTextElem) elem).getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取群组信息，主要是群成员数量
     */
    public void getRoomMembers() {
        //创建待获取信息的群组 ID 列表
        ArrayList<String> groupList = new ArrayList<String>();
        //创建回调
        TIMValueCallBack<List<TIMGroupDetailInfo>> cb = new TIMValueCallBack<List<TIMGroupDetailInfo>>() {

            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> infoList) { //参数中返回群组信息列表
                for (TIMGroupDetailInfo info : infoList) {
                    Timber.d("groupId: " + info.getGroupId()           //群组 ID
                            + " group name: " + info.getGroupName()              //群组名称
                            + " group owner: " + info.getGroupOwner()            //群组创建者帐号
                            + " group create time: " + info.getCreateTime()      //群组创建时间
                            + " group last info time: " + info.getLastInfoTime() //群组信息最后修改时间
                            + " group last msg time: " + info.getLastMsgTime()  //最新群组消息时间
                            + " group member num: " + info.getMemberNum());      //群组成员数量
                }
            }
        };
        //添加群组 ID;
        groupList.add(mRoomId);
        //获取群组详细信息
        TIMGroupManager.getInstance().getGroupDetailInfo(
                groupList, //需要获取信息的群组 ID 列表
                cb);       //回调

    }

    /**
     * 消息循环监听类
     */
    public interface TCChatRoomListener {

        /**
         * 加入群组回调
         *
         * @param code 错误码，成功时返回0，失败时返回相应错误码
         * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onJoinGroupCallback(int code, String msg);

        //void onGetGroupMembersList(int code, List<TIMUserProfile> result);

        /**
         * 发送消息结果回调
         *
         * @param code       错误码，成功时返回0，失败时返回相应错误码
         * @param timMessage 发送的TIM消息
         */
        void onSendMsgCallback(int code, TIMMessage timMessage);

        /**
         * 接受消息监听接口
         * 文本消息回调
         *
         * @param type     消息类型
         * @param userEntity 发送者信息
         * @param content  内容
         */
        void onReceiveMsg(int type, UserEntity userEntity, String content);

        /**
         * 群组删除回调，在主播群组解散时被调用
         */
        void onGroupDelete();
    }
}
