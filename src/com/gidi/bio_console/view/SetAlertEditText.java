package com.gidi.bio_console.view;

import java.math.BigDecimal;

import com.gidi.bio_console.R;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetAlertEditText extends RelativeLayout implements OnClickListener{
	private Context mContext;
	private TextView mTitleTxt;
	private ImageView mAddImg;
	private ImageView mSubImg;
	private SystemKeyBoardEditText mContentEt;
	private String contentNum;
	
	private int mode = MODE_FIRST_ARTERY_PRESSURE;
	public static final int MODE_FIRST_ARTERY_PRESSURE = 0;
	public static final int MODE_SECOND_VEIN_PRESSURE  = 1;
	public static final int MODE_THIRD_ARTERY_FLOW = 2;
	public static final int MODE_FORTH_VEIN_FLOW = 3;
	public static final int MODE_ARTERY_SPEED = 4;
	public static final int MODE_VEIN_SPEED = 5;

	private onNumChangeListener onNumChangelistener;
	public interface onNumChangeListener{
		void displayErrorMsg(int resId);
	}
	
	public void setOnNumChangeListener(onNumChangeListener listener){
		this.onNumChangelistener = listener;
	}
	
	
	public SetAlertEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		 this.mContext = context;
		 LayoutInflater.from(context).inflate(R.layout.module_include_setting_sub_add_item, this, true);
		 mAddImg = (ImageView)findViewById(R.id.setting_add_img);
		 mSubImg = (ImageView)findViewById(R.id.setting_sub_img);
		 mTitleTxt = (TextView)findViewById(R.id.setting_item_title);
		 mContentEt = (SystemKeyBoardEditText)findViewById(R.id.setting_num_content_edit);
		 mAddImg.setOnClickListener(this);
		 mSubImg.setOnClickListener(this);

		 mContentEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				int tempMode = PreferenceUtil.getInstance(mContext.getApplicationContext()).getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);
				if (editable.toString().isEmpty()){
					return;
				}  
			    String textString = editable.toString();
			    switch (mode) {
				case MODE_FIRST_ARTERY_PRESSURE:
					//热灌不超过100，冷灌不超过40
			        if(StringUtil.convertToFloat(textString, (float)0.0) > 100.0f){
						mContentEt.setText(100+"");
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_pressure);
						}
					}
					break;
					
				case MODE_SECOND_VEIN_PRESSURE:
					//热灌不超过14，冷灌不超过8
					float vein_pressure = StringUtil.convertToFloat(textString, 0.0f); 					
					if(tempMode == 0){
						if(vein_pressure > 14.0f){
							mContentEt.setText(14 + "");  
							if(null != onNumChangelistener){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_pressure_nmp);
							}
						}
						
					}else if(tempMode == 1){
						if(vein_pressure > 8.0f){
							mContentEt.setText(8 + "");  
							if(null != onNumChangelistener){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_pressure_hmp);
							}
						}
					}
			        
					break;
				case MODE_THIRD_ARTERY_FLOW:				
					if(StringUtil.convertToFloat(textString, (float)0.0) > 1000.0f){
						if(null != onNumChangelistener){
							mContentEt.setText(1000+"");
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_flow);
						}	
					}
					break;
					
				case MODE_FORTH_VEIN_FLOW:
					float vein_flow = StringUtil.convertToFloat(textString,0.0f);  				
					if(vein_flow > 2000.0f){
						mContentEt.setText(2000+"");		
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_flow);
						}	
					}
					break;
					
				case MODE_ARTERY_SPEED:
					int artSpeed = StringUtil.convertToInt(editable.toString(), 1500);
					if(artSpeed > 3000){
						mContentEt.setText(3000+"");
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_artery_speed);
						}
					}	
					
					break;
				case MODE_VEIN_SPEED:
					int veinSpeed = StringUtil.convertToInt(editable.toString(), 1200);
					if(veinSpeed > 3000){
						mContentEt.setText(3000+"");
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_speed);
						}
					}	
					
					break;
					
				default:
					break;
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				
			}
		});
		initAttrArray(context,attrs);  
	}

	private void initAttrArray(Context context, AttributeSet attrs){
    	TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.SetAlertEditText);
    	String content_text = attrArray.getString(R.styleable.SetAlertEditText_content_text);
    	int content_color = attrArray.getColor(R.styleable.SetAlertEditText_content_text_color, R.color.color_preset_artery_mode_normal);
    	String title_text = attrArray.getString(R.styleable.SetAlertEditText_title_text);
    	int title_color = attrArray.getColor(R.styleable.SetAlertEditText_title_text_color, R.color.color_set_edit_title);
    	
    	mTitleTxt.setText(title_text);
    	mTitleTxt.setTextColor(title_color);
    	mContentEt.setText(content_text);
    	mContentEt.setTextColor(getResources().getColor(content_color));
    	attrArray.recycle();
    }
	
	/**设置标题的名称**/
	public void setTitleText(String title){
		mTitleTxt.setText(title);
	}

	/**获取编辑框的内容**/
	public String getEditText(){
		String text = "";
		if(!StringUtil.isEmpty(mContentEt.getText().toString().trim())){
			text = mContentEt.getText().toString().trim();
		}
		return text;
	}
	
	public void setEditText(String text){
		if(text.length() == 6){
			String subtext = text.substring(0, 4);
			mContentEt.setText(subtext);
		}else{
			mContentEt.setText(text);
		}
		
	}

	public void setContentMode (int mode){
		this.mode = mode;
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_add_img:
			//如果加温度一次加0.5，如果加肝动脉目标压力一次加5.0，如果加门静脉一次加1.0
			contentNum = mContentEt.getText().toString().trim();
			int tempMode = PreferenceUtil.getInstance(mContext.getApplicationContext()).getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);
			String addText = "";
			int addNumText;
			switch (mode) {
			    case MODE_FIRST_ARTERY_PRESSURE:			    	
			    	addText = new BigDecimal(contentNum).setScale(1).add(new BigDecimal("5.0")) .toString();	
			    	if(!(StringUtil.convertToFloat(addText, (float)0.0) > 100.0f)){
						mContentEt.setText(addText);
						
					}else{
						mContentEt.setText("100.0");
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_pressure);
						}						
					}
			    					
				break;
				
			    case MODE_SECOND_VEIN_PRESSURE:
			    	//常温灌注，门静脉最大不能超过14，冷灌注不能超过8
			    	addText = new BigDecimal(contentNum).setScale(1).add(new BigDecimal("0.5")) .toString();	
			    	if(tempMode == 0){
			    		//常温灌注
			    		if(!(StringUtil.convertToFloat(addText, (float)0.0) > 14.0f)){
							mContentEt.setText(addText);
							
						}else{
							if(null != onNumChangelistener){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_pressure_nmp);
							}
							
						}
			    	}else if(tempMode == 1){
			    		if(!(StringUtil.convertToFloat(addText, (float)0.0) > 8.0f)){
							mContentEt.setText(addText);
							
						}else{
							if(null != onNumChangelistener){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_pressure_hmp);
							}
							
						}
			    	}
					
			    break;
			    
			    case MODE_THIRD_ARTERY_FLOW:
			    	addText  = new BigDecimal(contentNum).setScale(1).add(new BigDecimal("1.0")) .toString();			    	
			    	if(!(StringUtil.convertToFloat(addText, (float)0.0) > 1000)){
			    		setEditText(addText);
						
					}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_flow);
						}
						
					}
			    break;
			    
			    case MODE_FORTH_VEIN_FLOW:
			    	addText  = new BigDecimal(contentNum).setScale(1).add(new BigDecimal("1.0")) .toString();			    	
			    	if(!(StringUtil.convertToFloat(addText, (float)0.0) > 2000)){
			    		setEditText(addText);
						
					}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_flow);
						}
						
					}
			    	break;
			    

				//肝动脉泵速
			    case MODE_ARTERY_SPEED:
			    	addNumText = StringUtil.convertToInt(contentNum, 1500) + 10;
			    	addText = String.valueOf(addNumText);
			    	if(addNumText >= 1500 && addNumText <= 3000){
			    		mContentEt.setText(addText);
						
			    	}else{
			    		if(null != onNumChangelistener){
			    			if(addNumText < 1500){
			    				onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_artery_speed);
			    			}else if(addNumText > 3000){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_artery_speed);
			    			}
						}
			    	}
			    break;
			    case MODE_VEIN_SPEED:
			    	addNumText = StringUtil.convertToInt(contentNum, 1500)+ 10;
			    	addText = String.valueOf(addNumText);
			    	if(addNumText >= 1200 && addNumText <= 3000){
			    		mContentEt.setText(addText);
						
			    	}else{
			    		if(null != onNumChangelistener){
			    			if(addNumText < 1500){
			    				onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_vein_speed);
			    			}else if(addNumText > 3000){
								onNumChangelistener.displayErrorMsg(R.string.error_upper_limit_of_the_vein_speed);
			    			}
						}
			    	}
			    break;
			default:
				break;
			}
			break;
			
		case R.id.setting_sub_img:
			contentNum = mContentEt.getText().toString().trim();
			String subText = "";
			int subNum;
			int subNumText;
			switch (mode) {
			    case MODE_FIRST_ARTERY_PRESSURE:	
			    	contentNum = mContentEt.getText().toString().trim();
			    	subText = new BigDecimal(contentNum).setScale(1).subtract(new BigDecimal("5.0")) .toString();		
					if(StringUtil.convertToFloat(subText, (float)0.0) >= 0){
						mContentEt.setText(subText);
						
					}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_hepatic_artery_pressure);
						}
						
					}
					
				break;
			    case MODE_SECOND_VEIN_PRESSURE:
			    	contentNum = mContentEt.getText().toString().trim();
			    	subText = new BigDecimal(contentNum).setScale(1).subtract(new BigDecimal("0.5")) .toString();		
					if(StringUtil.convertToFloat(subText, (float)0.0) >= 0){
						mContentEt.setText(subText);
						
					}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_vein_pressure);
						}
						
					}
			    break;
			    case MODE_THIRD_ARTERY_FLOW:
			    	subText  = new BigDecimal(contentNum).setScale(1).subtract(new BigDecimal("1.0")) .toString();
			    	
			    	if(StringUtil.convertToFloat(subText, (float)0.0) >= 0){
						mContentEt.setText(subText);
						
					}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_hepatic_artery_flow);
						}
						
					}
			    break;
			    
			    case MODE_FORTH_VEIN_FLOW:
			    	subText  = new BigDecimal(contentNum).setScale(1).subtract(new BigDecimal("1.0")) .toString();
			    	if(StringUtil.convertToFloat(subText, (float)0.0)>=0){
			    		mContentEt.setText(subText);
			    		
			    	}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_vein_flow);
						}
						
					}
			    	
			    	break;
			    
			    case MODE_ARTERY_SPEED:
			    	subNum = StringUtil.convertToInt(contentNum, 1500);
			    	subNumText = subNum - 10;
			    	subText = String.valueOf(subNumText);
			    	if(subNumText >= 1500){
			    		mContentEt.setText(subText);
							    		
			    	}else{
						if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_artery_speed);
						}	
			    	}
			    break;
			    case MODE_VEIN_SPEED:
			    	subNum = StringUtil.convertToInt(contentNum, 1200);
			    	subNumText = subNum - 10;
			    	subText = String.valueOf(subNumText);
			    	if(subNumText >= 1200){
			    		mContentEt.setText(subText);									    		
			    	}else{
			    		if(null != onNumChangelistener){
							onNumChangelistener.displayErrorMsg(R.string.error_lower_limit_of_the_vein_speed);
						}
			    	}
			    break;
			
		default:
			break;
		}
			break;
		
		default:
			break;
		}
	}
	
	
}
