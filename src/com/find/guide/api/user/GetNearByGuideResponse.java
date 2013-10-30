package com.find.guide.api.user;

import java.util.List;

import com.find.guide.user.TourGuide;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetNearByGuideResponse extends ResponseBase {

    @JsonProperty("result")
    public List<TourGuide> guides;

}
