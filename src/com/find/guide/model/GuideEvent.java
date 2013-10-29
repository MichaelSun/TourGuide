package com.find.guide.model;

import java.io.Serializable;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

public class GuideEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8399816101665079796L;

    public static final int EVENT_STATUS_WAITING = 0;
    public static final int EVENT_STATUS_ACCEPTED = 1;
    public static final int EVENT_STATUS_REFUSED = 2;
    public static final int EVENT_STATUS_SATISFACTION = 3;
    public static final int EVENT_STATUS_CANCEL = 4;
    public static final int EVENT_STATUS_ACCEPTED_BY_OTHER = 5;

    public static final int SATISFACTION_GOOD = 1;
    public static final int SATISFACTION_BAD = 2;

    private int guideId;

    private long eventId;

    private int userId;

    private int eventStatus;

    private int satisfaction;

    private long createTime;

    private int eventType;

    private long startTime;

    private long endTime;

    private String scenic;

    private String userName;

    private String userHeadUrl;

    private String mobile;

    @JsonCreator
    public GuideEvent(@JsonProperty("guideId") int guideId, @JsonProperty("eventId") long eventId,
            @JsonProperty("userId") int userId, @JsonProperty("status") int eventStatus,
            @JsonProperty("satisfaction") int satisfaction, @JsonProperty("createTime") long createTime,
            @JsonProperty("eventType") int eventType, @JsonProperty("startTime") long startTime,
            @JsonProperty("endTime") long endTime, @JsonProperty("scenic") String scenic,
            @JsonProperty("userName") String userName, @JsonProperty("userHeadUrl") String userHeadUrl,
            @JsonProperty("mobile") String mobile) {
        this.guideId = guideId;
        this.eventId = eventId;
        this.userId = userId;
        this.eventStatus = eventStatus;
        this.satisfaction = satisfaction;
        this.createTime = createTime;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scenic = scenic;
        this.userName = userName;
        this.userHeadUrl = userHeadUrl;
        this.mobile = mobile;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHeadUrl() {
        return userHeadUrl;
    }

    public void setUserHeadUrl(String userHeadUrl) {
        this.userHeadUrl = userHeadUrl;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
