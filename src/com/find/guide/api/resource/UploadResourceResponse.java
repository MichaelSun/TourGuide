package com.find.guide.api.resource;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class UploadResourceResponse extends ResponseBase {

    @JsonProperty("result")
    public String url;
}
