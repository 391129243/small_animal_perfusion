package com.gidi.bio_console.view;

import com.gidi.bio_console.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomParasIconView extends RelativeLayout{
	private TextView mTitleTxt;
	private TextView mLeftContentTxt;
	private TextView mRightContentTxt;
	private ImageView mUnitImg;
	private String titleText;
	private String rightContentText;
	private String leftContentText;
	private int titletextColor;
	private int contenttextColor;
	private boolean isUnitImgVisible = false;
	private  int unitimg;
	
	private OnImageClickListener mListener;
	public interface OnImageClickListener{
		void onImageClick();
	}
	

	public void setOnImgClickListener(OnImageClickListener onImageClickListener) {
		// TODO Auto-generated method stub
		this.mListener = onImageClickListener;
	}

	
	public CustomParasIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initTypedArray(context, attrs);
		initView(context);
		initListener();
	}

	public CustomParasIconView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
		initListener();
	}
	
	private void initTypedArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomParasIconView);
		titletextColor = mTypedArray.getColor(R.styleable.CustomParasIconView_title_text_color, Color.BLUE); 
		contenttextColor = mTypedArray.getColor(R.styleable.CustomParasIconView_title_text_color, Color.BLUE); 
		titleText = mTypedArray.getString(R.styleable.CustomParasIconView_title_text);
		rightContentText = mTypedArray.getString(R.styleable.CustomParasIconView_rightcontent_text);
		leftContentText = mTypedArray.getString(R.styleable.CustomParasIconView_leftcontent_text);
		isUnitImgVisible = mTypedArray.getBoolean(R.styleable.CustomParasIconView_unit_img_visible, true);
	    unitimg = mTypedArray.getResourceId(R.styleable.CustomParasIconView_unit_img, R.drawable.bile_zero);
	    mTypedArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.module_layout_custom_icon_param, this, true);
		mTitleTxt = (TextView)findViewById(R.id.title_txt);
		mLeftContentTxt = (TextView)findViewById(R.id.left_content_txt);
		mRightContentTxt = (TextView)findViewById(R.id.right_content_txt);
		mUnitImg = (ImageView)findViewById(R.id.unit_img);
		if(isUnitImgVisible){
			mUnitImg.setVisibility(View.VISIBLE);
			mUnitImg.setImageResource(R.drawable.bile_zero);
		}else{
			mUnitImg.setVisibility(View.GONE);
		}
		mTitleTxt.setTextColor(titletextColor);
		mLeftContentTxt.setTextColor(contenttextColor);
		mRightContentTxt.setTextColor(contenttextColor);		
	}
	
	private void initListener(){
		mUnitImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != mListener){
					mListener.onImageClick();
				}
			}

		});
	}
	
	public void setLeftContentText(String leftcontenttext){
		this.leftContentText = leftcontenttext;
		mLeftContentTxt.setText(leftContentText);
	}
	
	public void setRightContentText(String rightcontenttext){
		this.rightContentText = rightcontenttext;
		mRightContentTxt.setText(rightContentText);
	}
	
}
