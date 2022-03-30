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

public class CustomSecParamText extends RelativeLayout{
	private TextView mTitleTxt;
	private TextView mContentTxt;
	private TextView mUnitTxt;
	private ImageView mUnitImg;
	private String titleText;
	private String secondContentText;
	private String uintText;
	private int textColor;
	private boolean isUnitImgVisible = false;
	private  int unitimg;

	private OnImageClickListener mListener;
	public interface OnImageClickListener{
		void onImageClick();
	}
	
	public void setOnImgClickListener(OnImageClickListener listener){
		this.mListener = listener;
	}

	public CustomSecParamText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initTypedArray(context, attrs);
		initView(context);
		initListener();
	}

	public CustomSecParamText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
		initListener();
	}
	
	private void initTypedArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PerfusionSecParamTextView);
		textColor =  mTypedArray.getColor(R.styleable.PerfusionSecParamTextView_text_color, Color.BLUE);             
		titleText = mTypedArray.getString(R.styleable.PerfusionSecParamTextView_title_text);
		secondContentText = mTypedArray.getString(R.styleable.PerfusionSecParamTextView_content_text);
        uintText = mTypedArray.getString(R.styleable.PerfusionSecParamTextView_uint_text);
        isUnitImgVisible = mTypedArray.getBoolean(R.styleable.PerfusionSecParamTextView_unit_img_visible, false);
        unitimg = mTypedArray.getResourceId(R.styleable.PerfusionSecParamTextView_unit_img, R.drawable.bile_zero);
        //textSize = mTypedArray.getDimensionPixelSize(R.styleable.PerfusionSecParamTextView_content_text_size, 32);
        //获取资源后要及时回收
        mTypedArray.recycle();
	}

	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.module_include_perfusion_left_second_param, this, true);
		mTitleTxt = (TextView)findViewById(R.id.second_param_title_txt);
		mContentTxt = (TextView)findViewById(R.id.second_param_content_txt);
		mUnitTxt = (TextView)findViewById(R.id.second_param_unit_txt);	
		mUnitImg = (ImageView)findViewById(R.id.second_param_unit_img);
		mUnitImg.setImageResource(unitimg);
		mTitleTxt.setTextColor(textColor);
		mTitleTxt.setText(titleText);
		if(isUnitImgVisible){
			mUnitImg.setVisibility(View.VISIBLE);
			mUnitTxt.setVisibility(View.GONE);
			mUnitTxt.setTextColor(context.getResources().getColor(R.color.color_perfusion_content_bg));
			mUnitImg.setImageResource(R.drawable.bile_zero);
			
		}else{
			mUnitTxt.setText(uintText);			
			mUnitTxt.setVisibility(View.VISIBLE);
			mUnitTxt.setTextColor(textColor);
			mUnitImg.setVisibility(View.GONE);
		}

		
		mContentTxt.setTextColor(textColor);
		
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
	
	public void setContentText(String contenttext){
		this.secondContentText = contenttext;
		mContentTxt.setText(secondContentText);
	}
		
}
