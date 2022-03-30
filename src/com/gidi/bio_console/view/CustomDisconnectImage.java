package com.gidi.bio_console.view;


import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomDisconnectImage extends RelativeLayout{

	private int image;
	private ImageView imageView;
	private TextView topRightTxt;
	private TextView bottomRightTxt;
	private boolean isTopVisible;
	private boolean isButtomVisible;
	public CustomDisconnectImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initArrtrArray(context,attrs);  
		initView(context);
	}
	
	 private void initArrtrArray(Context context, AttributeSet attrs){
		 TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CusDisconnectImg);
		 image = attrArray.getResourceId(R.styleable.CusDisconnectImg_disconnect_img, R.drawable.disconnect);
		 isTopVisible = attrArray.getBoolean(R.styleable.CusDisconnectImg_top_txt_visible, false);
		 isButtomVisible = attrArray.getBoolean(R.styleable.CusDisconnectImg_bottom_txt_visible, false);
		 attrArray.recycle();
	 }

	 private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.layout_custom_disconnect_imageview, this, true);
		imageView = (ImageView)findViewById(R.id.disconnect_img);
		topRightTxt = (TextView)findViewById(R.id.artery_disconnect_txt);
		bottomRightTxt = (TextView)findViewById(R.id.vein_disconnect_txt);
		imageView.setBackgroundResource(image);
		if(isTopVisible){
			topRightTxt.setVisibility(View.VISIBLE);
		}else{
			topRightTxt.setVisibility(View.INVISIBLE);
		}
		
		if(isButtomVisible){
			bottomRightTxt.setVisibility(View.VISIBLE);
		}else{
			bottomRightTxt.setVisibility(View.INVISIBLE);
		}
	 }
	 
	 public void setTopRightTxtVisible(boolean isVisible){
		 isTopVisible = isVisible;
		 if(isVisible){
			topRightTxt.setVisibility(View.VISIBLE);
		 }else{
			topRightTxt.setVisibility(View.INVISIBLE);
		 }
	 }
	 
	 public void setBottomRightTxtVisible(boolean isVisible){
		 isButtomVisible = isVisible;
		 if(isVisible){
			bottomRightTxt.setVisibility(View.VISIBLE);
		 }else{
			bottomRightTxt.setVisibility(View.INVISIBLE);
		 }
	 }
}
