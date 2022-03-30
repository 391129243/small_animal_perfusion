package com.gidi.bio_console.view;


import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomContentText extends RelativeLayout {

	private TextView mTitleTxt;
	private TextView mContentText;
	private RelativeLayout mBackgroudLayout;
	private String titleText;
	private String contentText;
	private int layout_bg;
	private int title_color;
	private int content_color;
	
	public CustomContentText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initAttrArray(context, attrs);
		initView(context);	
	}

	
	
	public CustomContentText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);	
	}


	private void initAttrArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomContentText);
		titleText = mTypedArray.getString(R.styleable.CustomContentText_title_text);
		contentText = mTypedArray.getString(R.styleable.CustomContentText_content_text);
		layout_bg = mTypedArray.getResourceId(R.styleable.CustomContentText_layout_bg,R.drawable.shape_gradient_artery_txt_bg);	
		content_color = mTypedArray.getColor(R.styleable.CustomContentText_content_text_color, R.color.white);
		title_color = mTypedArray.getColor(R.styleable.CustomContentText_title_text_color, R.color.white);
		mTypedArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.layout_custom_temp_content, this, true);
		mTitleTxt = (TextView)findViewById(R.id.custom_temp_title_txt);
		mContentText = (TextView)findViewById(R.id.custom_temp_num_txt);
		mBackgroudLayout = (RelativeLayout)findViewById(R.id.custom_temp_layout);
		mTitleTxt.setText(titleText);
		mContentText.setText(contentText);
		mTitleTxt.setTextColor(title_color);
		mContentText.setTextColor(content_color);
		mBackgroudLayout.setBackgroundResource(layout_bg);
	
	}
	
	public void setTitleText(String titletext){
		this.titleText = titletext;
		mTitleTxt.setText(titleText);
	}
	
	public void setContextText(String contenttext){
		this.contentText = contenttext;
		mContentText.setText(contentText);
	}
	
	
	public void setContentBackgroud(int resId){
		mBackgroudLayout.setBackgroundResource(resId);
	}
	
}
