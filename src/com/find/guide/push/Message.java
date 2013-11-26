package com.find.guide.push;

import com.plugin.internet.core.json.JsonProperty;

public class Message {

    /**
     * 预约消息 user->guide
     */
    public static final int MSG_TYPE_INVITE = 1;
    /**
     * 广播消息 user->guide
     */
    public static final int MSG_TYPE_BROADCAST = 2;
    /**
     * 预约被接受 guide->user
     */
    public static final int MSG_TYPE_ACCEPTED = 3;
    /**
     * 预约被拒绝 guide->user
     */
    public static final int MSG_TYPE_REFUSED = 4;
    /**
     * 被评价 user->guide
     */
    public static final int MSG_TYPE_EVALUATED = 5;
    /**
     * 发起聊天 user->guide || guide->user
     */
    public static final int MSG_TYPE_CHAT = 6;

    @JsonProperty("id")
    public long id;

    @JsonProperty("fromId")
    public int fromId;

    @JsonProperty("toId")
    public int toId;

    @JsonProperty("type")
    public int type;

    @JsonProperty("content")
    public String content;

    @JsonProperty("createTime")
    public long createTime;

    @JsonProperty("msgExt")
    public MsgExt msgExt;

    public static class MsgExt {
        @JsonProperty("fromName")
        public String fromName;

        @JsonProperty("fromHeadUrl")
        public String fromHeadUrl;
    }
    
}
