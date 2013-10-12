package com.find.guide.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingManager {
    private static SettingManager mInstance;

    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private Editor mEditor;

    public static synchronized SettingManager getInstance() {
        if (mInstance == null) {
            mInstance = new SettingManager();
        }

        return mInstance;
    }

    private static final String SHARE_PREFERENCE_NAME = "setting_manager_share_pref";

    // 在Application中一定要调用
    public void init(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME, 0);
        mEditor = mSharedPreferences.edit();
    }

    private SettingManager() {
    }

    public void clearAll() {
        mEditor.remove(KEY_USER_ID);
        mEditor.remove(KEY_USER_NAME);
        mEditor.remove(KEY_USER_HEADER);
        mEditor.remove(KEY_USER_GENDER);
        mEditor.remove(KEY_USER_TYPE);
        mEditor.remove(KEY_USER_PHONE_NUM);
        mEditor.remove(KEY_TICKET);
        mEditor.remove(KEY_SECRET_KEY);
        mEditor.commit();

    }

    private static final String KEY_USER_ID = "key_user_id";

    public void setUserId(long userId) {
        mEditor.putLong(KEY_USER_ID, userId).commit();
    }

    public long getUserId() {
        return mSharedPreferences.getLong(KEY_USER_ID, -1);
    }

    private static final String KEY_USER_NAME = "key_user_name";

    public void setUserName(String name) {
        mEditor.putString(KEY_USER_NAME, name).commit();
    }

    public String getUserName() {
        return mSharedPreferences.getString(KEY_USER_NAME, "");
    }

    private static final String KEY_USER_HEADER = "key_user_header";

    public void setUserHeader(String headerUrl) {
        mEditor.putString(KEY_USER_HEADER, headerUrl).commit();
    }

    public String getUserHeader() {
        return mSharedPreferences.getString(KEY_USER_HEADER, "");
    }

    private static final String KEY_USER_GENDER = "key_user_gender";

    public static final int USER_GENDER_UNKNOWN = 0;
    public static final int USER_GENDER_MALE = 1;
    public static final int USER_GENDER_FEMALE = 2;

    public void setUserGender(int gender) {
        if ((gender != USER_GENDER_UNKNOWN) && (gender != USER_GENDER_MALE) && (gender != USER_GENDER_FEMALE)) {
            return;
        }
        mEditor.putInt(KEY_USER_GENDER, gender).commit();
    }

    public int getUserGender() {
        int gender = mSharedPreferences.getInt(KEY_USER_GENDER, USER_GENDER_UNKNOWN);
        if ((gender != USER_GENDER_UNKNOWN) && (gender != USER_GENDER_MALE) && (gender != USER_GENDER_FEMALE)) {
            gender = USER_GENDER_UNKNOWN;
            setUserGender(gender);
        }

        return gender;
    }

    private static final String KEY_USER_TYPE = "key_user_type";

    public void setUserType(int type) {
        mEditor.putInt(KEY_USER_TYPE, type).commit();
    }

    public int getUserType() {
        return mSharedPreferences.getInt(KEY_USER_TYPE, 0);
    }

    private static final String KEY_USER_PHONE_NUM = "key_user_phone_num";

    public void setUserPhoneNum(String phoneNum) {
        mEditor.putString(KEY_USER_PHONE_NUM, phoneNum).commit();
    }

    public String getUserPhoneNum() {
        return mSharedPreferences.getString(KEY_USER_PHONE_NUM, "");
    }

    private static final String KEY_TICKET = "key_ticket";

    public void setTicket(String ticket) {
        mEditor.putString(KEY_TICKET, ticket).commit();
    }

    public String getTicket() {
        return mSharedPreferences.getString(KEY_TICKET, "");
    }

    private static final String KEY_SECRET_KEY = "key_secret_key";

    public void setSecretKey(String secretKey) {
        mEditor.putString(KEY_SECRET_KEY, secretKey).commit();
    }

    public String getSecretKey() {
        return mSharedPreferences.getString(KEY_SECRET_KEY, "");
    }

    private static final String KEY_HAS_NEW_VERSION = "key_has_new_version";

    public void setHasNewVersion(boolean hasVersion) {
        mEditor.putBoolean(KEY_HAS_NEW_VERSION, hasVersion).commit();
    }

    public boolean getHasNewVersion() {
        return mSharedPreferences.getBoolean(KEY_HAS_NEW_VERSION, false);
    }

}
