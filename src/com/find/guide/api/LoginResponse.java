package com.find.guide.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-12
 * Time: AM11:09
 * To change this template use File | Settings | File Templates.
 */
public class LoginResponse extends ResponseBase {

    @JsonProperty("userId")
    public long userId;

    @JsonProperty("ticket")
    public String ticket;

    @JsonProperty("userSecretKey")
    public String userSecretKey;

}
