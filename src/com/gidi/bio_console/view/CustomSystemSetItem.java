package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomSystemSetItem extends RelativeLayout {

	private TextView mTitleTxt;
	private CheckBox mCheckBox;
	
	public CustomSystemSetItem(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public CustomSystemSetItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	
	
	public CustomSystemSetItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}



	private void initView(Context context){
		 View.inflate(context, R.layout.module_include_item_system_setting_layout, CustomSystemSetItem.this);
		 mTitleTxt = (TextView)findViewById(R.id.item_system_setting_title_txt);
		 mCheckBox = (CheckBox)findViewById(R.id.item_system_setting_check);
	}
	
	public boolean isCheck(){
		return mCheckBox.isChecked();
	}
	
	public void setCheck(boolean isCheck){
		mCheckBox.setChecked(isCheck);
	}
	
	public void setTitleText(CharSequence text){
		mTitleTxt.setText(text);
	}
}
