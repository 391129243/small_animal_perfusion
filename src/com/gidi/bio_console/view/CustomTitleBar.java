package com.gidi.bio_console.view;



import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.StringUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTitleBar extends RelativeLayout {

	private onTitleTextClickListener mListener;
	private onRightBarClickListener mRightBarListener;
	private TextView titleBarTitle;
	private TextView titleBarRight;
	
	public CustomTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.layout_custom_titlebar, this, true);
		titleBarTitle = (TextView)findViewById(R.id.title_txt);
		titleBarRight = (TextView)findViewById(R.id.title_bar_right);
		init(context,attrs);
	}
	
	private void init(Context context, AttributeSet attrs){
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar);
		int titleBarBacgroundColor = attrArray.getColor(R.styleable.CustomTitleBar_title_background_color, Color.BLACK);
		setBackgroundColor(titleBarBacgroundColor);
		String titleText = attrArray.getString(R.styleable.CustomTitleBar_title_txt);
		String titleRightText = attrArray.getString(R.styleable.CustomTitleBar_title_txt_right);
		int titleColor = attrArray.getColor(R.styleable.CustomTitleBar_title_text_color, Color.WHITE);
		int titleRightColor = attrArray.getColor(R.styleable.CustomTitleBar_title_txt_right_color, Color.WHITE);
		
		boolean rightTxtvisible = attrArray.getBoolean(R.styleable.CustomTitleBar_right_txt_visible, false);
		if(rightTxtvisible){
			titleBarRight.setVisibility(View.VISIBLE);
			if(!StringUtil.isEmpty(titleRightText)){
				titleBarRight.setText(titleRightText);
				titleBarRight.setTextColor(titleRightColor);
			}
		}else{
			titleBarRight.setVisibility(View.GONE);
		}
		
		titleBarTitle.setTextColor(titleColor);
		titleBarTitle.setText(titleText);
		attrArray.recycle();
		
		initListener();
		
	}
		
	private void initListener(){
		titleBarTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != mListener){
					mListener.onBackTextClick();
				}
			}
		});
		
		titleBarRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != mListener){
					mRightBarListener.onRightBarTextClick();
				}
			}
		});
	}
	
	public interface onTitleTextClickListener{
		public void onBackTextClick();
	}
	
	public interface onRightBarClickListener{
		public void onRightBarTextClick();
	}
	//设置监听器
	public void setOnTitleTextClickListener(onTitleTextClickListener listener){
		this.mListener = listener;
	}
	
	public void setOnRightTitleTextClickListener(onRightBarClickListener listener){
		this.mRightBarListener = listener;
	}

}
