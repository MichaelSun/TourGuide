package com.find.guide.model;

import java.io.Serializable;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class GuideEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8399816101665079796L;

    @JsonProperty("guideId")
    private int guideId;

    @JsonProperty("eventId")
    private long eventId;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("eventStatus")
    private int eventStatus;

    @JsonProperty("satisfaction")
    private int satisfaction;

    @JsonProperty("createTime")
    private long createTime;

    @JsonProperty("eventType")
    private int eventType;

    @JsonCreator
    public GuideEvent(int guideId, long eventId, int userId, int eventStatus, int satisfaction, long createTime,
            int eventType) {
        this.guideId = guideId;
        this.eventId = eventId;
        this.userId = userId;
        this.eventStatus = eventStatus;
        this.satisfaction = satisfaction;
        this.createTime = createTime;
        this.eventType = eventType;
    }

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
