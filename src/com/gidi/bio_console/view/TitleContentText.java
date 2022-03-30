package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleContentText extends RelativeLayout{

	private TextView mTitleTxt;
	private TextView mContentText;
	private String titleText;
	private String contentText;
	private int title_color;
	private int content_color;
	
	public TitleContentText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initAttrArray(context, attrs);
		initView(context);	
	}

	
	
	public TitleContentText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);	
	}


	private void initAttrArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleContentText);
		titleText = mTypedArray.getString(R.styleable.TitleContentText_title_text);
		contentText = mTypedArray.getString(R.styleable.TitleContentText_content_text);
		content_color = mTypedArray.getColor(R.styleable.TitleContentText_content_text_color, R.color.white);
		title_color = mTypedArray.getColor(R.styleable.TitleContentText_title_text_color, R.color.white);
		mTypedArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.layout_custom_title_content_text, this, true);
		mTitleTxt = (TextView)findViewById(R.id.custom_title_txt);
		mContentText = (TextView)findViewById(R.id.custom_content_txt);
		mTitleTxt.setText(titleText);
		mContentText.setText(contentText);
		mTitleTxt.setTextColor(title_color);
		mContentText.setTextColor(content_color);

	
	}
	
	
	public void setContentText(String contenttext){
		this.contentText = contenttext;
		mContentText.setText(contentText);
	}
	
	public String getContentText(){
		return mContentText.getText().toString().trim();
	}
	
}
