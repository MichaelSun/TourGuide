package com.find.guide.push;

import com.plugin.internet.core.json.JsonProperty;

public class Message {

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
