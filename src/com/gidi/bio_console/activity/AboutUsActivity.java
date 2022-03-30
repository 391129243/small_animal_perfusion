package com.gidi.bio_console.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.utils.CommonUtil;
import com.gidi.bio_console.view.DateTimeView;

public class AboutUsActivity extends BaseActivity {

	private RelativeLayout mTitleLayout;
	private ImageView mTitleImg;
	private TextView mTitleTxt;
	private DateTimeView mSystimeTxt;
	private TextView versionTxt;
	private TextView productionDateTxt;
	private ImageView backImg;
	private static final String PRODUCTION_DATE = "2021.05.10";
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_aboutus;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		versionTxt = (TextView)findViewById(R.id.about_version_txt);
		productionDateTxt = (TextView)findViewById(R.id.about_production_txt);
		backImg = (ImageView)findViewById(R.id.title_back_img);
		mTitleLayout = (RelativeLayout)findViewById(R.id.about_guide_title_layout);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text_s);	
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon_s);
		mSystimeTxt = (DateTimeView)mTitleLayout.findViewById(R.id.base_systime_txt_s);	
		mSystimeTxt.setVisibility(View.INVISIBLE);
		mTitleImg.setImageResource(R.drawable.function_title_set_about_img);
		mTitleTxt.setText(R.string.module_aboutus_title);
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		backImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		String versionName = CommonUtil.getAppVersionName(getApplicationContext());
		versionTxt.setText(versionName);
		productionDateTxt.setText(PRODUCTION_DATE);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
