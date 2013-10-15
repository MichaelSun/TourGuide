package com.find.guide.model.help;

import android.content.Context;

public class GuideHelper {

    private Context mContext;

    public GuideHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public void destroy() {
        mContext = null;
    }
}
