package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateTimeView extends LinearLayout {

	private TextView mDateTxt;
	private TextView mTimeTxt;	
	private String date;
	private String time;
	private int dateColor;
	private int timeColor;
	
	public DateTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initAttrArray(context,attrs);
		initView(context);	
	}

	
	
	public DateTimeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);	
	}
	

	private void initAttrArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DateTimeView);
		date = mTypedArray.getString(R.styleable.DateTimeView_title_text);
		time = mTypedArray.getString(R.styleable.DateTimeView_content_text);
		timeColor = mTypedArray.getColor(R.styleable.DateTimeView_content_text_color, context.getResources().getColor(R.color.white));
		dateColor = mTypedArray.getColor(R.styleable.DateTimeView_title_text_color, context.getResources().getColor(R.color.date_color));
		mTypedArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.module_include_layout_date_time, this, true);
		mDateTxt = (TextView)findViewById(R.id.date_txt);
		mTimeTxt = (TextView)findViewById(R.id.time_txt);
		mDateTxt.setText(date);
		mTimeTxt.setText(time);
		mDateTxt.setTextColor(dateColor);
		mTimeTxt.setTextColor(timeColor);
	}
	
	public void setDateText(String date_string){
		this.date = date_string;
		mDateTxt.setText(date);
	}
	
	public void setTimeText(String time_string){
		this.time = time_string;
		mTimeTxt.setText(time);
	}
}
