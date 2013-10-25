package com.find.guide.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.find.guide.R;
import com.find.guide.config.AppRuntime;
import com.find.guide.setting.SettingManager;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    protected ActionBar mActionbar;

    protected Fragment mPageFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    protected void init() {
        mActionbar = getActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setDisplayShowTitleEnabled(true);
        // mActionbar.setDisplayShowHomeEnabled(true);

        this.getWindow().setBackgroundDrawableResource(R.drawable.background);
    }

    protected void fragmentUpdate(int id, Fragment fragment) {
        if (fragment != null) {
            FragmentManager manager = this.getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(id, fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SettingManager.getInstance().getHasKickout()) {
            AppRuntime.sendKickoutIntent(getApplicationContext());
        }
        SettingManager.getInstance().setHasKickout(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
