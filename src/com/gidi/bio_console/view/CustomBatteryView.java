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

/**
 * 自定义电源控件
 * 显示电源状态和电量
 * @author 80657
 *
 */
public class CustomBatteryView extends RelativeLayout {

	private ImageView batteryImg;
	private TextView batteryLevelTxt;
	private ImageView chargingImg;
	
	public CustomBatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		 LayoutInflater.from(context).inflate(R.layout.layout_custom_battery, this, true);
		 batteryImg = (ImageView)findViewById(R.id.battery_img);
		 batteryLevelTxt = (TextView)findViewById(R.id.battery_level_txt);
		 chargingImg = (ImageView)findViewById(R.id.battery_charging_img);
		 init(context,attrs);
	}

	private void init(Context context, AttributeSet attrs){
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomBatteryView);
	    String content_text = attrArray.getString(R.styleable.CustomBatteryView_content_text);
	    int content_bg = attrArray.getResourceId(R.styleable.CustomBatteryView_content_image, R.drawable.battery_10);
	    int charging_img =  attrArray.getResourceId(R.styleable.CustomBatteryView_charging_img, R.drawable.battery_charging);
	    boolean isChargingVisible = attrArray.getBoolean(R.styleable.CustomBatteryView_charging_img_visible, false);
	    boolean isBatteryVisible = attrArray.getBoolean(R.styleable.CustomBatteryView_battery_level_visible, true);
	    batteryImg.setImageResource(content_bg);
	    chargingImg.setImageResource(charging_img);
	    batteryLevelTxt.setText(content_text);
	    if(isChargingVisible){
	    	chargingImg.setVisibility(View.VISIBLE);
	    }else{
	    	chargingImg.setVisibility(View.GONE);
	    }
	    
	    if(isBatteryVisible){
	    	batteryLevelTxt.setVisibility(View.VISIBLE);
	    }else{
	    	batteryLevelTxt.setVisibility(View.GONE);
	    }
	    attrArray.recycle();
	}
	
	/**设置充电图片**/
	public void setBatteryImage(int resId){
		batteryImg.setImageResource(resId);  
	}
	
	public void setChargingImage(int resId){
		chargingImg.setImageResource(resId);
	}
	
	/**设置电量文本**/
	public void setBatteryLevelText(String batterylevel){
		batteryLevelTxt.setText(batterylevel);
	}
	
	
	
	/**设置充电符号可见或不可见**/
	public void setChargingVisible(boolean isVisible){
		if(isVisible){
			chargingImg.setVisibility(View.VISIBLE);
		}else{
			chargingImg.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setBatteryLevelVisible(boolean isVisible){
		if(isVisible){
			batteryLevelTxt.setVisibility(View.VISIBLE);
		}else{
			batteryLevelTxt.setVisibility(View.INVISIBLE);
		}
	
	}
}