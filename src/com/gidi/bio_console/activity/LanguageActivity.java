package com.gidi.bio_console.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.constant.LanguageType;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.CommonUtil;
import com.gidi.bio_console.utils.PreferenceUtil;


/***
 * 设置语言
 * @author 80657
 *
 */
public class LanguageActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout mTitleLayout;
	private ImageView mTitleImg;
	private TextView mTitleTxt;
	private TextView chineseTxt;
	private TextView englishTxt;
	private ImageView backImg;
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_language;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mTitleLayout = (RelativeLayout)findViewById(R.id.language_title_layout);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text_s);
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon_s);
		mTitleTxt.setText(R.string.language);
		mTitleImg.setImageResource(R.drawable.function_title_set_language_img);
		chineseTxt = (TextView)findViewById(R.id.language_chinese);
		englishTxt = (TextView)findViewById(R.id.language_english);
		backImg = (ImageView)findViewById(R.id.language_back_img);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		chineseTxt.setOnClickListener(this);
		englishTxt.setOnClickListener(this);
		backImg.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		String language = null;
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.language_chinese:
			language = LanguageType.CHINESE.getLanguage();
			changeLanguage(language);	
			break;
		case R.id.language_english:
			language = LanguageType.ENGLISH.getLanguage();
			changeLanguage(language);	
			break;
		case R.id.language_back_img:
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			break;
		default:
			break;
		}
		
			
	}
	
	private void changeLanguage(String newLanguage){
		CommonUtil.changeAppLanguage(getApplicationContext(),newLanguage);
		PreferenceUtil.getInstance(getApplicationContext())
			.setValueByName(SharedConstants.LANGUAGE, newLanguage);
		Intent intent = new Intent(this, SystemPresetActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		// 杀掉进程
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

}
