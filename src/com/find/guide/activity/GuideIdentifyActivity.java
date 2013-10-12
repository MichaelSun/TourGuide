package com.find.guide.activity;

import com.find.guide.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

public class GuideIdentifyActivity extends BaseActivity implements OnClickListener {

    private ImageView mGuideCardIv;
    private EditText mGuideCardIdEt;
    private EditText mNameEt;
    private EditText mBirthdayEt;
    private EditText mBeGuideYearEt;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private Button mSubmitCheckBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_identify);
        initUI();

    }

    private void initUI() {
        mGuideCardIv = (ImageView) findViewById(R.id.guide_identify_guide_card_image);
        mGuideCardIdEt = (EditText) findViewById(R.id.guide_card_id_et);
        mNameEt = (EditText) findViewById(R.id.guide_identify_name_et);
        mBirthdayEt = (EditText) findViewById(R.id.guide_identify_birthday_et);
        mBeGuideYearEt = (EditText) findViewById(R.id.guide_identify_be_guide_year_et);
        mMaleRadio = (RadioButton) findViewById(R.id.guide_identify_male_radio);
        mFemaleRadio = (RadioButton) findViewById(R.id.guide_identify_female_radio);
        mSubmitCheckBtn = (Button) findViewById(R.id.guide_identify_submit_check_btn);

        mGuideCardIv.setOnClickListener(this);
        mSubmitCheckBtn.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.guide_identify_guide_card_image:
            selectPhoto();
            break;
        case R.id.guide_identify_submit_check_btn:
            submitCheck();
            break;
        }
    }
    
    private void selectPhoto() {
        
    }

    private void submitCheck() {

    }

}
