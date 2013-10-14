package com.find.guide.api.invite;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class InviteCancelResponse extends ResponseBase {

    @JsonProperty("result")
    public int result;
}
