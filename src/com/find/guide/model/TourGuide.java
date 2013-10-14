package com.find.guide.model;

/**
 * 导游信息
 * 
 */
public class TourGuide extends Tourist {

    /**
     * 
     */
    private static final long serialVersionUID = -3945287939503647433L;

    public String goodAtScenic;

    public long birthday;

    public int beGuideYear;

    public String guideCardUrl;

    public String guideCardId;

    public String location;

    public int city;

    public TourGuide(int userId, String userName, String mobile, int gender, int userType, String headUrl,
            String goodAtScenic, long birthday, int beGuideYear, String guideCardUrl, String guideCardId,
            String location, int city) {
        super(userId, userName, mobile, gender, userType, headUrl);
        this.goodAtScenic = goodAtScenic;
        this.birthday = birthday;
        this.beGuideYear = beGuideYear;
        this.guideCardUrl = guideCardUrl;
        this.guideCardId = guideCardId;
        this.location = location;
        this.city = city;
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

}
