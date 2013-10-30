package com.find.guide.invite;

import java.io.Serializable;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class InviteEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7422912511389682539L;

    public static final int EVENT_TYPE_ONE = 1;
    public static final int EVENT_TYPE_BROADCASR = 2;

    public static final int EVENT_STATUS_BOOKING = 0;
    public static final int EVENT_STATUS_CANCELED = 1;
    public static final int EVENT_STATUS_ACCEPTED = 2;
    public static final int EVENT_STATUS_REFUSED = 3;
    public static final int EVENT_STATUS_EVALUATE = 4;

    public static final int SATISFACTION_HAVENOT = 0;
    public static final int SATISFACTION_GOOD = 1;
    public static final int SATISFACTION_BAD = 2;

    private long eventId;

    private int userId;

    private int guideId;

    private int eventType;

    private int eventStatus;

    private long startTime;

    private long endTime;

    private String scenic;

    private long createTime;

    private int satisfaction;

    private String guideName;

    private String guideHeadUrl;

    private String mobile;

    @JsonCreator
    public InviteEvent(@JsonProperty("eventId") long eventId, @JsonProperty("userId") int userId,
            @JsonProperty("guideId") int guideId, @JsonProperty("eventType") int eventType,
            @JsonProperty("eventStatus") int eventStatus, @JsonProperty("startTime") long startTime,
            @JsonProperty("endTime") long endTime, @JsonProperty("scenic") String scenic,
            @JsonProperty("createTime") long createTime, @JsonProperty("satisfaction") int satisfaction,
            @JsonProperty("guideName") String guideName, @JsonProperty("guideHeadUrl") String guideHeadUrl,
            @JsonProperty("mobile") String mobile) {
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
        this.guideName = guideName;
        this.guideHeadUrl = guideHeadUrl;
        this.mobile = mobile;
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

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public String getGuideHeadUrl() {
        return guideHeadUrl;
    }

    public void setGuideHeadUrl(String guideHeadUrl) {
        this.guideHeadUrl = guideHeadUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
