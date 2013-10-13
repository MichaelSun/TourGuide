package com.find.guide.config;

import com.plugin.common.utils.UtilsConfig;

public class AppConfig {

	public static final boolean DEBUG = true;

	public static final String BMAP_KEY = "0510f3eedc28fff4537f878386d9223d";
	
	public static final String ROOT_CATEGORY = "error_tips";
    public static final String MSG_GET = "msg.get";
    public static final String SERVER_CODE = "code";
    
	public static final void LOGD(String msg) {
        if (DEBUG) {
            UtilsConfig.LOGD(msg);
        }
    }
	
}
