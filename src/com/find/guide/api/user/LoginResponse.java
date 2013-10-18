package com.find.guide.api.user;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-10-12 Time: AM11:09 To
 * change this template use File | Settings | File Templates.
 */
public class LoginResponse extends ResponseBase {

    @JsonProperty("userId")
    public int userId;

    @JsonProperty("userName")
    public String userName;

    @JsonProperty("mobile")
    public String mobile;

    @JsonProperty("gender")
    public int gender;

    @JsonProperty("userType")
    public int userType;

    @JsonProperty("headUrl")
    public String headUrl;

    @JsonProperty("userPassport")
    public UserPassport userPassport;

    public static class UserPassport {
        @JsonProperty("userId")
        public int userId;

        @JsonProperty("ticket")
        public String ticket;

        @JsonProperty("appId")
        public int appId;

        @JsonProperty("accountOrigin")
        public int accountOrigin;

        @JsonProperty("createTime")
        public long createTime;

        @JsonProperty("userSecretKey")
        public String userSecretKey;
    }

}
