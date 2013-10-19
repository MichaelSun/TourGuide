package com.find.guide.activity;

import com.find.guide.R;

import android.os.Bundle;
import android.widget.ListView;

public class GuideRecordActivity extends BaseActivity {
    
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
