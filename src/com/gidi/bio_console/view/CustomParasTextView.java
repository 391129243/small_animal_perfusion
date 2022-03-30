package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomParasTextView extends RelativeLayout {

	private TextView mTitleTxt;
	private TextView mSecTitleTxt;
	private TextView mLeftContentTxt;
	private TextView mRightContentTxt;
	private TextView mSecContentTxt;
	private TextView mUnitTxt;
	private TextView mDividerTxt;
	private String titleText;
	private String sectitleText;
	private String leftContentText;
	private String rightContentText;
	private String secondContentText;
	private String uintText;
	private int textColor;
	private float textSize;
	private boolean isContentVisible;
	private boolean isSecTitleVisible;
	private boolean isSecContentVisible;
	private Context mContext;
	

	public CustomParasTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initTypedArray(context, attrs);
		initView(context);
		
	}

	public CustomParasTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	private void initTypedArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomParasTextView);
		textColor =  mTypedArray.getColor(R.styleable.CustomParasTextView_textColor, Color.BLUE);       
		textSize  =  mTypedArray.getDimension(R.styleable.CustomParasTextView_content_text_size, dp2px(30.0f));  
		titleText = mTypedArray.getString(R.styleable.CustomParasTextView_title_text);
		sectitleText = mTypedArray.getString(R.styleable.CustomParasTextView_sectitleText);
		leftContentText = mTypedArray.getString(R.styleable.CustomParasTextView_leftcontent_text);
		rightContentText = mTypedArray.getString(R.styleable.CustomParasTextView_rightcontent_text);
		secondContentText = mTypedArray.getString(R.styleable.CustomParasTextView_secondcontent_text);
        uintText = mTypedArray.getString(R.styleable.CustomParasTextView_uint_text);
      
        isContentVisible = mTypedArray.getBoolean(R.styleable.CustomParasTextView_content_visible, true);
        isSecTitleVisible = mTypedArray.getBoolean(R.styleable.CustomParasTextView_sec_title_visible, false);
        isSecContentVisible = mTypedArray.getBoolean(R.styleable.CustomParasTextView_sec_content_visible, false);
        //获取资源后要及时回收
        mTypedArray.recycle();
	}

	private void initView(Context context){
		
		LayoutInflater.from(context).inflate(R.layout.layout_custom_textview, this, true);
		mTitleTxt = (TextView)findViewById(R.id.title_txt);
		mSecTitleTxt = (TextView)findViewById(R.id.second_title_txt);
		mLeftContentTxt = (TextView)findViewById(R.id.left_content_txt);
		mRightContentTxt = (TextView)findViewById(R.id.right_content_txt);
		mSecContentTxt = (TextView)findViewById(R.id.second_content_txt);
		mUnitTxt = (TextView)findViewById(R.id.unit_txt);
		mDividerTxt = (TextView)findViewById(R.id.middle_content_txt);
		mDividerTxt.setTextColor(textColor);
		mTitleTxt.setTextColor(textColor);
		mTitleTxt.setText(titleText);
		mUnitTxt.setText(uintText);
		mUnitTxt.setTextColor(textColor);
		mLeftContentTxt.setTextColor(textColor);
		mRightContentTxt.setTextColor(textColor);
		mSecContentTxt.setTextColor(textColor);
		mLeftContentTxt.setTextSize(textSize);
		mRightContentTxt.setTextSize(textSize);
		if(!isContentVisible){
			//不可见
			mLeftContentTxt.setVisibility(View.INVISIBLE);
			mRightContentTxt.setVisibility(View.INVISIBLE);
			mDividerTxt.setVisibility(View.INVISIBLE);
			
		}else{
			mLeftContentTxt.setVisibility(View.VISIBLE);
			mRightContentTxt.setVisibility(View.VISIBLE);
			mDividerTxt.setVisibility(View.VISIBLE);
		}
		
		if(isSecTitleVisible){
			mSecTitleTxt.setVisibility(View.VISIBLE);
			mSecTitleTxt.setText(sectitleText);
			mSecTitleTxt.setTextColor(textColor);
		}else{
			mSecTitleTxt.setVisibility(View.GONE);
		}
		
		if(isSecContentVisible){
			mSecContentTxt.setVisibility(View.VISIBLE);
			mSecContentTxt.setText(secondContentText);
			
		}else{
			mSecContentTxt.setVisibility(View.GONE);
		}
		
	}
	
	public void setLeftContentText(String leftcontenttext){
		this.leftContentText = leftcontenttext;
		mLeftContentTxt.setText(leftContentText);
	}
	
	public void setRightContentText(String rightcontenttext){
		this.rightContentText = rightcontenttext;
		mRightContentTxt.setText(rightContentText);
	}
	
	public void setSecondContentText(String secondcontenttext){
		this.secondContentText = secondcontenttext;
		mSecContentTxt.setText(secondContentText);
	}
	
	public void setContentVisible(boolean isVisible){
		if(isVisible){
			mLeftContentTxt.setVisibility(View.VISIBLE);
			mRightContentTxt.setVisibility(View.VISIBLE);
			mDividerTxt.setVisibility(View.VISIBLE);
		}else{
			mLeftContentTxt.setVisibility(View.INVISIBLE);
			mRightContentTxt.setVisibility(View.INVISIBLE);
			mDividerTxt.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setSecContentVisible(boolean isVisible){
		if(isVisible){
			mSecContentTxt.setVisibility(View.VISIBLE);

		}else{
			mSecContentTxt.setVisibility(View.GONE);
		}
	}
	
	public void setRightContextColor(int color){
		mRightContentTxt.setTextColor(color);
	}
	
	public void setLeftContextColor(int color){
		mLeftContentTxt.setTextColor(color);
	}
	

    private int dp2px(float dpValue) {
        final float scale = mContext.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
