package com.find.guide.api.guide;

import java.util.List;

import com.find.guide.guide.GuideEvent;
import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

public class GetHistoricalGuideEventsResponse extends ResponseBase {

    @JsonProperty("result")
    public List<GuideEvent> guideEvents;

}
