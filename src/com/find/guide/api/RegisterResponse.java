package com.find.guide.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class RegisterResponse extends ResponseBase {

    @JsonProperty("userId")
    public long userId;

    @JsonProperty("ticket")
    public String ticket;

    @JsonProperty("userSecretKey")
    public String userSecretKey;

}
