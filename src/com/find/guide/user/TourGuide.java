package com.find.guide.user;

import com.plugin.internet.core.json.JsonCreator;
import com.plugin.internet.core.json.JsonProperty;

/**
 * 导游信息
 * 
 */
public class TourGuide extends Tourist {

	/**
     * 
     */
	private static final long serialVersionUID = -3945287939503647433L;

	private String goodAtScenic;

	private long birthday;

	private int beGuideYear;

	private String guideCardUrl;

	private String guideCardId;

	private String location;

	private int city;

	private int evaluateScore;

	private int evaluateCount;

	private String travelAgency;

	@JsonCreator
	public TourGuide(@JsonProperty("userId") int userId,
			@JsonProperty("userName") String userName,
			@JsonProperty("mobile") String mobile,
			@JsonProperty("gender") int gender,
			@JsonProperty("userType") int userType,
			@JsonProperty("headUrl") String headUrl,
			@JsonProperty("goodAtScenic") String goodAtScenic,
			@JsonProperty("birthday") long birthday,
			@JsonProperty("beGuideYear") int beGuideYear,
			@JsonProperty("guideCardUrl") String guideCardUrl,
			@JsonProperty("guideCardId") String guideCardId,
			@JsonProperty("location") String location,
			@JsonProperty("city") int city,
			@JsonProperty("evaluateScore") int evaluateScore,
			@JsonProperty("evaluateCount") int evaluateCount,
			@JsonProperty("travelAgency") String travelAgency) {
		super(userId, userName, mobile, gender, userType, headUrl);
		this.goodAtScenic = goodAtScenic;
		this.birthday = birthday;
		this.beGuideYear = beGuideYear;
		this.guideCardUrl = guideCardUrl;
		this.guideCardId = guideCardId;
		this.location = location;
		this.city = city;
		this.evaluateScore = evaluateScore;
		this.evaluateCount = evaluateCount;
		this.travelAgency = travelAgency;
	}

	public String getGoodAtScenic() {
		return goodAtScenic;
	}

	public void setGoodAtScenic(String goodAtScenic) {
		this.goodAtScenic = goodAtScenic;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public int getBeGuideYear() {
		return beGuideYear;
	}

	public void setBeGuideYear(int beGuideYear) {
		this.beGuideYear = beGuideYear;
	}

	public String getGuideCardUrl() {
		return guideCardUrl;
	}

	public void setGuideCardUrl(String guideCardUrl) {
		this.guideCardUrl = guideCardUrl;
	}

	public String getGuideCardId() {
		return guideCardId;
	}

	public void setGuideCardId(String guideCardId) {
		this.guideCardId = guideCardId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public int getEvaluateScore() {
		return evaluateScore;
	}

	public void setEvaluateScore(int evaluateScore) {
		this.evaluateScore = evaluateScore;
	}

	public int getEvaluateCount() {
		return evaluateCount;
	}

	public void setEvaluateCount(int evaluateCount) {
		this.evaluateCount = evaluateCount;
	}

	public String getTravelAgency() {
		return travelAgency;
	}

	public void setTravelAgency(String travelAgency) {
		this.travelAgency = travelAgency;
	}

}
