package com.find.guide.api.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.photo_me.mobile.android.config.AppConfig;
import com.photo_me.mobile.android.setting_manager.SettingManager;
import com.plugin.internet.core.InternetStringUtils;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.RequestBase;
import com.plugin.internet.core.RequestEntity;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.OptionalTicket;

import java.util.TreeMap;
import java.util.Vector;

public class PMRequestBase<T> extends RequestBase<T> {
    
    public static String BASE_API_URL = "http://118.186.218.43/api/";
    
    private static final String KEY_METHOD = "method";
    private static final String KEY_HTTP_METHOD = "httpMethod";
    
    @Override
    public RequestEntity getRequestEntity() throws NetWorkException {
        RequestEntity entity = super.getRequestEntity();
        return entity;
    }

    @Override
    public Bundle getParams() throws NetWorkException {
        Bundle params = super.getParams();
        
        Class<?> c = this.getClass();
        String ticket = null;
        String userSecret = null;
        // Method name
        boolean checkTicket = false;
        boolean noNeedTicket = false;
        if (c.isAnnotationPresent(NeedTicket.class)) {
            checkTicket = true;
            ticket = SettingManager.getInstance().getTicket();
            userSecret = SettingManager.getInstance().getSecretKey();
        } else if (c.isAnnotationPresent(NoNeedTicket.class)) {
            noNeedTicket = true;
        } else if (c.isAnnotationPresent(OptionalTicket.class)) {
            ticket = SettingManager.getInstance().getTicket();
            userSecret = SettingManager.getInstance().getSecretKey();
        } else {    //默认为NeedTicket
            checkTicket = true;
            ticket = SettingManager.getInstance().getTicket();
            userSecret = SettingManager.getInstance().getSecretKey();
        }
        
        if (checkTicket) {
            if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(userSecret)) {
                return null;
            }
        }
        
        String method = params.getString(KEY_METHOD);
        if (TextUtils.isEmpty(method)) {
            throw new RuntimeException("Method Name MUST NOT be NULL");
        }
        
        if (!method.startsWith("http://")) {    //method可填为 http://url/xxx?a=1&b=2 或  feed.gets
            method = BASE_API_URL + method.replace('.', '/');
        }

        if (!noNeedTicket && !TextUtils.isEmpty(ticket)) {
            params.putString("t", ticket);
        }
        
        String httpMethod = params.getString(KEY_HTTP_METHOD);
        params.remove(KEY_HTTP_METHOD);
        params.remove(KEY_METHOD);
        params.putString("app_id", "1001");
        params.putString("v", "1.0");   //TODO
        params.putString("call_id", String.valueOf(System.currentTimeMillis()));
        params.putString("gz", "compression");
        params.putString("sig", getSig(params, "9f738a3934abf88b1dca8e8092043fbd", noNeedTicket ? null : userSecret));
        params.putString(KEY_METHOD, method);
        params.putString(KEY_HTTP_METHOD, httpMethod);
        
        return params;
    }
    
    private String getSig (Bundle params,String appSecretKey, String userSecretKey) {
        if (params == null) {
            return null;
        }
        
        if (params.size() == 0) {
            return "";
        }
        
        
        TreeMap<String, String> sortParams = new TreeMap<String, String>();
        for (String key : params.keySet()) {
            sortParams.put(key, params.getString(key));
        }
        
        Vector<String> vecSig = new Vector<String>();
        for (String key : sortParams.keySet()) {
            String value = sortParams.get(key);
            vecSig.add(key + "=" + value);
        }
        
        String[] nameValuePairs = new String[vecSig.size()];
        vecSig.toArray(nameValuePairs);

        for (int i = 0; i < nameValuePairs.length; i++) {
            for (int j = nameValuePairs.length - 1; j > i; j--) {
                if (nameValuePairs[j].compareTo(nameValuePairs[j - 1]) < 0) {
                    String temp = nameValuePairs[j];
                    nameValuePairs[j] = nameValuePairs[j - 1];
                    nameValuePairs[j - 1] = temp;
                }
            }
        }
        StringBuffer nameValueStringBuffer = new StringBuffer();
        for (int i = 0; i < nameValuePairs.length; i++) {
            nameValueStringBuffer.append(nameValuePairs[i]);
        }
        nameValueStringBuffer.append(appSecretKey);
        if (!TextUtils.isEmpty(userSecretKey)) {
            nameValueStringBuffer.append(userSecretKey);
        }

//        if (AppConfig.DEBUG) {
//            for (int i = 0; i < nameValueStringBuffer.toString().length(); ) {
//                if (i + 1024 < nameValueStringBuffer.toString().length()) {
//                    Log.v("signa", nameValueStringBuffer.toString().substring(i, i + 1024));
//                } else {
//                    Log.v("signa", nameValueStringBuffer.toString().substring(i));
//                }
//                i = i + 1024;
//            }
//
//            AppConfig.LOGD("[[gtiSig]] sig raw : " + nameValueStringBuffer.toString());
//        }

        String sig = InternetStringUtils.MD5Encode(nameValueStringBuffer.toString());
        return sig;

    }

}
