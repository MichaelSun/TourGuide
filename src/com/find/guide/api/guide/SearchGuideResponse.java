package com.find.guide.api.guide;

import java.util.List;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class SearchGuideResponse extends ResponseBase {

    @JsonProperty("result")
    public List<TourGuideView> guides;
}
