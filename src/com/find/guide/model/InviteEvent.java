package com.find.guide.model;

import java.io.Serializable;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class InviteEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7422912511389682539L;

    public static final int EVENT_TYPE_ONE = 0;
    public static final int EVENT_TYPE_BROADCASR = 1;

    public static final int EVENT_STATUS_BOOKING = 0;
    public static final int EVENT_STATUS_CANCELED = 1;
    public static final int EVENT_STATUS_ACCEPTED = 2;
    public static final int EVENT_STATUS_REFUSED = 3;
    public static final int EVENT_STATUS_EVALUATE = 4;

    public static final int SATISFACTION_HAVENOT = 0;
    public static final int SATISFACTION_GOOD = 1;
    public static final int SATISFACTION_BAD = 2;

    @JsonProperty("eventId")
    private long eventId;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("guideId")
    private int guideId;

    @JsonProperty("eventType")
    private int eventType;

    @JsonProperty("eventStatus")
    private int eventStatus;

    @JsonProperty("startTime")
    private long startTime;

    @JsonProperty("endTime")
    private long endTime;

    @JsonProperty("scenic")
    private String scenic;

    @JsonProperty("createTime")
    private long createTime;

    @JsonProperty("satisfaction")
    private int satisfaction;

    @JsonCreator
    public InviteEvent(long eventId, int userId, int guideId, int eventType, int eventStatus, long startTime,
            long endTime, String scenic, long createTime, int satisfaction) {
        this.eventId = eventId;
        this.userId = userId;
        this.guideId = guideId;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scenic = scenic;
        this.createTime = createTime;
        this.satisfaction = satisfaction;
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

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getScenic() {
        return scenic;
    }

    public void setScenic(String scenic) {
        this.scenic = scenic;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
