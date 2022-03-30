package com.gidi.bio_console.view;

import com.gidi.bio_console.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PresetStatusView extends LinearLayout {

	private int image;
	private String text;
	private int textColor;
	private ImageView statusImg;
	private TextView statusTxt;
	
	public PresetStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initArrtrArray(context,attrs);
		initView(context);
	}
	
	private void initArrtrArray(Context context, AttributeSet attrs){
		 TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.SensorStatusView);
		 image = attrArray.getResourceId(R.styleable.SensorStatusView_status_img, R.drawable.disconnect);
		 text = attrArray.getString(R.styleable.SensorStatusView_content_text);
		 textColor = attrArray.getColor(R.styleable.SensorStatusView_content_text_color, R.color.white);
		 attrArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.layout_preset_status_view, this, true);
		statusImg = (ImageView)findViewById(R.id.status_img);
		statusTxt = (TextView)findViewById(R.id.status_txt);
		
		statusImg.setImageResource(image);
		statusTxt.setText(text);
		statusTxt.setTextColor(context.getResources().getColor(textColor));
	}
	
	public void setContentText(CharSequence charSequence){
		statusTxt.setText(charSequence);
	}
	
	public void setSensorImage(int resId){
		statusImg.setImageResource(resId);
	}
}
