package com.find.guide.model.helper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;

import com.find.guide.model.CityItem;

public class CityManager {

    private static final String TAG_ARRAY = "array";
    private static final String ARRAY_KEY = "key";
    private static final String TAG_CITY = "city";
    private static final String CITY_ID = "id";

    private boolean mLoaded;
    private Hashtable<Integer, CityItem> mIdTable;
    private Hashtable<String, List<CityItem>> mIndexTable;

    private static CityManager mInstance = new CityManager();

    private CityManager() {
        mIdTable = new Hashtable<Integer, CityItem>();
        mIndexTable = new Hashtable<String, List<CityItem>>();
    }

    public static CityManager getInstance() {
        return mInstance;
    }

    public CityItem getCityById(int id) {
        if (mIdTable != null) {
            return mIdTable.get(id);
        }
        return null;
    }

    public List<CityItem> getCitiesByIndex(String index) {
        if (mIndexTable != null) {
            return mIndexTable.get(index);
        }
        return null;
    }

    public Hashtable<String, List<CityItem>> getAllCities() {
        return mIndexTable;
    }

    public void clear() {
        mLoaded = false;
        mIdTable.clear();
        mIndexTable.clear();
    }

    public boolean loadXML(XmlPullParser parser) {
        if (mLoaded) {
            return mLoaded;
        }

        if (parser == null) {
            return mLoaded;
        }

        try {
            CityItem cityItem = null;
            List<CityItem> cityList = null;
            String array_key = null;
            String city_id = null;
            int i_city_id = 0;
            String city_name = null;
            boolean inArray = false;
            boolean inCity = false;
            int event;
            while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    String tag = parser.getName();

                    if (TAG_ARRAY.equals(tag)) {
                        inArray = true;
                        array_key = parser.getAttributeValue(null, ARRAY_KEY);
                    } else if (TAG_CITY.equals(tag)) {
                        inCity = true;
                        city_id = parser.getAttributeValue(null, CITY_ID);
                    }
                } else if (event == XmlPullParser.TEXT) {
                    city_name = parser.getText();
                } else if (event == XmlPullParser.END_TAG) {
                    if (inCity) {
                        inCity = false;

                        if (!TextUtils.isEmpty(city_id) && !TextUtils.isEmpty(city_name)) {
                            i_city_id = Integer.parseInt(city_id);
                            cityItem = new CityItem(city_name, array_key, i_city_id);
                            mIdTable.put(i_city_id, cityItem);
                            if (cityList == null) {
                                cityList = new ArrayList<CityItem>();
                            }
                            cityList.add(cityItem);
                        }

                        city_id = null;
                        city_name = null;
                    } else if (inArray) {
                        inArray = false;

                        if (cityList != null && !TextUtils.isEmpty(array_key)) {
                            mIndexTable.put(array_key, cityList);
                        }

                        cityList = null;
                        array_key = null;
                    }
                }
            }
            mLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLoaded;
    }

    @Override
    public String toString() {
        return "XMLTables [mLoaded=" + mLoaded + ", mIdTable=" + mIdTable + ", mIndexTable=" + mIndexTable + "]";
    }

}
