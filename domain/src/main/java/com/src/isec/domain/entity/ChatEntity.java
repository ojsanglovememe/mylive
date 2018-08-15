package com.src.isec.domain.entity;

/**
 * @author sunmingchuan
 * @name IsecLive
 * @class name：com.src.isec.domain.entity
 * @class Im通讯消息实体类
 * @time 2018/4/16 11:18
 * @change
 * @chang time
 * @class describe
 */

public class ChatEntity {
    private String msgSendName;
    private String msgText;
    private int msgType;
    private int msgSendLevel;

    public ChatEntity() {
        // TODO Auto-generated constructor stub
    }

    public String getMsgSendName() {
        return msgSendName;
    }

    public void setMsgSendName(String msgSendName) {
        this.msgSendName = msgSendName;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgSendLevel() {
        return msgSendLevel;
    }

    public void setMsgSendLevel(int msgSendLevel) {
        this.msgSendLevel = msgSendLevel;
    }
}
