package com.gidi.bio_console.kidney;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.view.CustomSeekBar;
import com.gidi.bio_console.view.CustomContentText;
import com.gidi.bio_console.view.SystemKeyBoardEditText;
import com.gidi.bio_console.view.SystemKeyBoardEditText.OnKeyBoardOutsideDismissListener;

/**预灌注
 * 设置泵速，泵速默认是0选中，点击0表示**/
public class KidneyPreperfusionFragment extends BaseFragment implements OnClickListener{

	private ImageView mEndBtn;
	private TextView mArtPreTitleTv;
	private TextView mVeinPreTitleTv;
	private TextView mArtFlowTitleTv;
	private TextView mVeinFlowTitleTv;
	
	private TextView mStopArtTxt;
	private TextView mStopVeinTxt;
	private TextView mArtLowSpeedTxt, mArtMidSpeedTxt, mArtHighSpeedTxt,mArtLHighSpeedTxt;
	private TextView mVeinLowSpeedTxt, mVeinMidSpeedTxt, mVeinHighSpeedTxt,mVeinLHighSpeedTxt;	
	private SystemKeyBoardEditText mCtmArtSpeedEt;
	private SystemKeyBoardEditText mCtmVeinSpeedEt;
	private SystemKeyBoardEditText mCustomTempEt;
	private CustomContentText mRArtSpeedTxt;
	private CustomContentText mRVeinSpeedTxt;
	private CustomContentText mRTempTxt;

	private TextView mZeroArtFImg;
	private TextView mZeroVeinImg;
	private TextView mZeroArtPImg;
	private TextView mZeroVeinPImg;

	private SerialMsgReceiver mReceiver;
	private CustomSeekBar mArtPreBar;
	private CustomSeekBar mVeinPreBar;
	private CustomSeekBar mArtFlowBar;
	private CustomSeekBar mVeinFlowBar;
	private TextView mMArtPreTxt;
	private TextView mMVeinPreTxt;
	private TextView mMArtFlowTxt;
	private TextView mMVeinFlowTxt;
	private RadioGroup mTempSetRg;
	private RadioGroup mTempModeRg;
	private RadioButton mHMP_Rb;
	private RadioButton mNMP_Rb;
	private CheckBox mCtrlTempCk;
	private RadioButton mHMPOneTempRb,mHMPTwoTempRb, mNMPOneTempRb, mNMPTwoTempRb;
	private LinearLayout mTempModeLayout;
	private LinearLayout mTempContentLayout;
	private int mTempMode = 0;//常温灌注
	private boolean isTempCtrl;
	private boolean isFirstCtrl = true;
    private OnKidneyPrePerfusionListener mListener;
	    
   public interface OnKidneyPrePerfusionListener{
		void compeletePerfusion();
		void sendTargetTemp(String temp, int tempmode);
		void sendVeinTarSpeedMsg(String veinspeed);
		void sendArtTarSpeedMsg(String artspeed);
		void sendZeroFlowMsg(int type);
		void sendZeroPressureMsg(int type);
		void closeTempCtrl();
	}
		
	public void setOnKidneyPrePerfusionListener(OnKidneyPrePerfusionListener listener){
		this.mListener = listener;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isTempCtrl = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getBooleanValue(SharedConstants.TEMP_CTRL, false);
		if(isTempCtrl){
			mCtrlTempCk.setChecked(true);
			mTempContentLayout.setVisibility(View.VISIBLE);
			mTempModeLayout.setVisibility(View.VISIBLE);
		}else{
			
			mCtrlTempCk.setChecked(false);
			mTempContentLayout.setVisibility(View.GONE);
			mTempModeLayout.setVisibility(View.GONE);
		}

	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver();
		mListener = null;
		Log.i("PreperfusionFragment", "---onDestroy---");
	}


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i("PreperfusionFragment", "---onDestroyView---");
	}


	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_kidney_preset_perfusion;
	}

	
	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mArtPreTitleTv = (TextView)rootView.findViewById(R.id.pre_perfusion_artery_pre_title_tv);
		mVeinPreTitleTv = (TextView)rootView.findViewById(R.id.pre_perfusion_vein_title_tv);
		mArtFlowTitleTv = (TextView)rootView.findViewById(R.id.pre_perfusion_artery_flow_title_tv);
		mVeinFlowTitleTv = (TextView)rootView.findViewById(R.id.pre_perfusion_vein_flow_title_tv);
		
		mStopArtTxt = (TextView)rootView.findViewById(R.id.set_stop_artery_speed_text);
		mStopVeinTxt = (TextView)rootView.findViewById(R.id.set_stop_vein_speed_text);
		mArtLowSpeedTxt = (TextView)rootView.findViewById(R.id.set_low_artery_speed_text); 
		mArtMidSpeedTxt = (TextView)rootView.findViewById(R.id.set_middle_artery_speed_text); 
		mArtHighSpeedTxt = (TextView)rootView.findViewById(R.id.set_high_artery_speed_text);
		mArtLHighSpeedTxt = (TextView)rootView.findViewById(R.id.set_large_high_artery_speed_text);
		mVeinLowSpeedTxt = (TextView)rootView.findViewById(R.id.set_low_vein_speed_text); 
		mVeinMidSpeedTxt = (TextView)rootView.findViewById(R.id.set_middle_vein_speed_text); 
		mVeinHighSpeedTxt = (TextView)rootView.findViewById(R.id.set_high_vein_speed_text); 
		mVeinLHighSpeedTxt = (TextView)rootView.findViewById(R.id.set_large_high_vein_speed_text); 
		mCtmArtSpeedEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.set_custom_artery_speed_text);
		mCtmVeinSpeedEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.set_custom_vein_speed_text);
		mRArtSpeedTxt = (CustomContentText)rootView.findViewById(R.id.artery_real_speed_txt);
		mRVeinSpeedTxt = (CustomContentText)rootView.findViewById(R.id.vein_real_speed_txt);
		mRTempTxt = (CustomContentText)rootView.findViewById(R.id.artery_pump_real_temp_txt);

		mArtPreBar = (CustomSeekBar)rootView.findViewById(R.id.pre_perfusion_pressure_one_seekbar);
		mArtFlowBar = (CustomSeekBar)rootView.findViewById(R.id.pre_perfusion_artery_flow_seekbar);
		mVeinPreBar = (CustomSeekBar)rootView.findViewById(R.id.pre_perfusion_vein_pressure_seekbar);
		mVeinFlowBar = (CustomSeekBar)rootView.findViewById(R.id.pre_perfusion_vein_flow_seekbar);
		mMArtPreTxt = (TextView)rootView.findViewById(R.id.pre_perfusion_real_artery_pressure_tv);
		mMArtFlowTxt = (TextView)rootView.findViewById(R.id.pre_perfusion_vein_artery_flow_tv);
		mMVeinPreTxt = (TextView)rootView.findViewById(R.id.pre_perfusion_real_vein_pre_txt);
		mMVeinFlowTxt = (TextView)rootView.findViewById(R.id.pre_perfusion_vein_flow_tv);
		mZeroArtFImg = (TextView)rootView.findViewById(R.id.pre_perfusion_art_zero_img);
		mZeroVeinImg =  (TextView)rootView.findViewById(R.id.pre_perfusion_vein_zero_img);
		mZeroArtPImg = (TextView)rootView.findViewById(R.id.pre_perfusion_art_pressure_zero_img);
		mZeroVeinPImg = (TextView)rootView.findViewById(R.id.pre_perfusion_vein_zero_pre_img);
		mEndBtn = (ImageView)rootView.findViewById(R.id.complete_btn);
		mTempSetRg = (RadioGroup)rootView.findViewById(R.id.set_temp_radiogroup);
		mTempModeRg = (RadioGroup)rootView.findViewById(R.id.temp_mode_rg);
		mCtrlTempCk = (CheckBox)rootView.findViewById(R.id.temp_control_checkbox);
		mTempContentLayout = (LinearLayout)rootView.findViewById(R.id.temp_content_layout);
		mTempModeLayout = (LinearLayout)rootView.findViewById(R.id.temp_mode_layout);
		mHMP_Rb = (RadioButton)rootView.findViewById(R.id.temp_hmp_rb);
		mNMP_Rb = (RadioButton)rootView.findViewById(R.id.temp_nmp_rb);
		mCustomTempEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.set_custom_temp_text);
		mHMPOneTempRb = (RadioButton)rootView.findViewById(R.id.set_temp_four_rb);
		mHMPTwoTempRb = (RadioButton)rootView.findViewById(R.id.set_temp_eight_rb);
		mNMPOneTempRb  = (RadioButton)rootView.findViewById(R.id.set_temp_twenty_five_rb);
		mNMPTwoTempRb  = (RadioButton)rootView.findViewById(R.id.set_temp_thirdty_seven_rb);
		initVariable();
		registerReceiver();
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mZeroArtFImg.setOnClickListener(this);
		mZeroVeinImg.setOnClickListener(this);
		mStopArtTxt.setOnClickListener(this);
		mStopVeinTxt.setOnClickListener(this);
		mArtLowSpeedTxt.setOnClickListener(this);
		mArtMidSpeedTxt.setOnClickListener(this);
		mArtHighSpeedTxt.setOnClickListener(this);
		mArtLHighSpeedTxt.setOnClickListener(this);
		mVeinLowSpeedTxt.setOnClickListener(this);
		mVeinMidSpeedTxt.setOnClickListener(this); 
		mVeinHighSpeedTxt.setOnClickListener(this);
		mVeinLHighSpeedTxt.setOnClickListener(this);
		mCtmArtSpeedEt.setOnClickListener(this);
		mCtmVeinSpeedEt.setOnClickListener(this);
		mEndBtn.setOnClickListener(this);
		mCustomTempEt.setOnClickListener(this);
		mZeroArtPImg.setOnClickListener(this);
		mZeroVeinPImg.setOnClickListener(this);
		mTempSetRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.set_temp_four_rb:
				case R.id.set_temp_eight_rb:
				case R.id.set_temp_twenty_five_rb:
				case R.id.set_temp_thirdty_seven_rb:
					if(radioButton.isChecked()){
						setNormalView(mCustomTempEt);
						mCustomTempEt.setText("");
						String temperature = radioButton.getText().toString();					
						if(null != mListener){		
							mListener.sendTargetTemp(temperature, mTempMode);						
						}
					}

					break;
				default:
					break;
				}
			}
		});
		
		mTempModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				switch (radiogroup.getCheckedRadioButtonId()) {
				case R.id.temp_hmp_rb:
					mTempMode = 1;
					mHMPOneTempRb.setVisibility(View.VISIBLE);
					mHMPTwoTempRb.setVisibility(View.VISIBLE);
					mNMPOneTempRb.setVisibility(View.GONE);
					mNMPTwoTempRb.setVisibility(View.GONE);
					mRTempTxt.setContentBackgroud(R.drawable.shape_gradient_cold_perfusion_mode_bg);
					if(null != mListener){
						mListener.closeTempCtrl();
					}
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
					break;
				case R.id.temp_nmp_rb:
					mTempMode = 0;//常温灌注
					mNMPOneTempRb.setVisibility(View.VISIBLE);
					mNMPTwoTempRb.setVisibility(View.VISIBLE);
					mHMPOneTempRb.setVisibility(View.GONE);
					mHMPTwoTempRb.setVisibility(View.GONE);
					mRTempTxt.setContentBackgroud(R.drawable.shape_gradient_artery_txt_bg);
					if(null != mListener){
						mListener.closeTempCtrl();
					}
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
					break;
				default:
				// TODO Auto-generated method stub
				}
			}
		});
		
		mCtrlTempCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.TEMP_CTRL, true);
					mTempContentLayout.setVisibility(View.VISIBLE);
					mTempModeLayout.setVisibility(View.VISIBLE);
					if(isFirstCtrl){
						isFirstCtrl = false;
					}else{
						if(null != mListener){
							float last_temp = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
									.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);	
							mListener.sendTargetTemp(String.valueOf(last_temp), mTempMode);
						}
					}
				}else{
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.TEMP_CTRL, false);
					mTempContentLayout.setVisibility(View.GONE);
					mTempModeLayout.setVisibility(View.GONE);
					if(null != mListener){
						mListener.closeTempCtrl();
					}
				}
			}
		});	
		
		/**setting the target temperature**/
		mCustomTempEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float temp = StringUtil.convertToFloat(editable.toString(), 37.0f);
				if(temp > 37.0f){
					editable.replace(0,editable.length(), "37");
					displayToast(R.string.error_target_temp_max_tip);						
					return;
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				float mLast_TarTemp = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);
				String temperature = mCustomTempEt.getText().toString();
				if(!StringUtil.isEmpty(temperature)|| !temperature.endsWith(".")){
					float tarTemp = StringUtil.convertToFloat(temperature, 37.0f);
					if(tarTemp > 37.0f){
						displayToast(R.string.error_target_temp_max_tip);	
						mCustomTempEt.setText(37 +"");
						return;
					}else if(tarTemp <0){
						displayToast(R.string.error_target_temp_min_tip);	
						mCustomTempEt.setText(R.string.string_null);
						return;
					}
					if((mLast_TarTemp != tarTemp)){
						PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
							.setValueByName(SharedConstants.TARGET_TEMP, tarTemp);
						if(null != mListener){		
							mListener.sendTargetTemp(temperature, mTempMode);						
						}					
					}
				}
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
		mCustomTempEt.setOutSideDissmissListener(new OnKeyBoardOutsideDismissListener() {
			
			@Override
			public void OnKedboardOutDismiss() {
				// TODO Auto-generated method stub
				String temperature = mCustomTempEt.getText().toString();
				if(!mHMPOneTempRb.isChecked()&&!mHMPTwoTempRb.isChecked()
						&& !mNMPOneTempRb.isChecked()&& !mNMPTwoTempRb.isChecked() 
						&& !temperature.equals("---")){
					float mLast_TarTemp = PreferenceUtil.getInstance(getActivity().getApplicationContext())						
							.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);	
					
					if(!StringUtil.isEmpty(temperature)|| !temperature.endsWith(".")){
						float tarTemp = StringUtil.convertToFloat(temperature, 37.0f);
						if(tarTemp > 37.0f){
							displayToast(R.string.error_target_temp_max_tip);	
							mCustomTempEt.setText(37 +"");
							return;
						}else if(tarTemp <0){
							displayToast(R.string.error_target_temp_min_tip);	
							mCustomTempEt.setText(R.string.string_null);
							return;
						}
						if((mLast_TarTemp != tarTemp)){
							PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
								.setValueByName(SharedConstants.TARGET_TEMP, tarTemp);
							if(null != mListener){	
								mListener.sendTargetTemp(temperature, mTempMode);						
							}					
						}
					}					
				}
			}
		});
		
		mCtmArtSpeedEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String speedString = editable.toString().trim();
				int speed = StringUtil.convertToInt(speedString, 0);
				if(speed > 3000){
					editable.replace(0,editable.length(), "3000");
					displayToast(R.string.error_upper_set_artery_speed);
					return;
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String speed = mCtmArtSpeedEt.getText().toString().trim();
				if(StringUtil.isEmpty(speed)){
					if(null != getActivity()){
						displayToast(R.string.error_speed_is_null);
					}
				}else{	
					if(StringUtil.convertToInt(speed, 0) < 400){
						displayToast(R.string.error_input_lower_speed);
					}else{
						PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
							.setValueByName(SharedConstants.SET_CUSTOM_ARTERY_SPEED, StringUtil.convertToInt(speed, 0));
						setCustomArtSpeed(speed);
					}
					
				}
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
		mCtmArtSpeedEt.setOutSideDissmissListener(new OnKeyBoardOutsideDismissListener() {
			
			@Override
			public void OnKedboardOutDismiss() {
				// TODO Auto-generated method stub
				String speed = mCtmArtSpeedEt.getText().toString().trim();
				if(StringUtil.isEmpty(speed)){
					if(null != getActivity()){
						displayToast(R.string.error_speed_is_null);
					}
				}else{	
					if(StringUtil.convertToInt(speed, 0) < 400){
						displayToast(R.string.error_input_lower_speed);
					}else{
						PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
							.setValueByName(SharedConstants.SET_CUSTOM_ARTERY_SPEED, StringUtil.convertToInt(speed, 0));
						setCustomArtSpeed(speed);							
					}
		
				}
			}
		});
		
		mCtmVeinSpeedEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String speedString = editable.toString().trim();
				int speed = StringUtil.convertToInt(speedString, 0);
				if(speed > 3000){
					editable.replace(0,editable.length(), "3000");
					displayToast(R.string.error_upper_set_vein_speed);
					return;
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String speed = mCtmVeinSpeedEt.getText().toString().trim();
				if(StringUtil.isEmpty(speed)){
					if(null != getActivity()){
						displayToast(R.string.error_speed_is_null);
					}
				}else{	
					if(StringUtil.convertToInt(speed, 0) < 400){
						displayToast(R.string.error_input_lower_speed);
					}else{
						PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
							.setValueByName(SharedConstants.SET_CUSTOM_VEIN_SPEED, StringUtil.convertToInt(speed, 0));
						setCustomVeinSpeed(speed);
					}					
				}

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
		//设置区域外键盘消失的监听
		mCtmVeinSpeedEt.setOutSideDissmissListener(new OnKeyBoardOutsideDismissListener() {
			
			@Override
			public void OnKedboardOutDismiss() {
				// TODO Auto-generated method stub
				
				String speed = mCtmVeinSpeedEt.getText().toString().trim();
				if(StringUtil.isEmpty(speed)){
					if(null != getActivity()){
						displayToast(R.string.error_speed_is_null);
					}
				}else{	
					if(StringUtil.convertToInt(speed, 0) < 400){
						displayToast(R.string.error_input_lower_speed);
					}else{
						PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
							.setValueByName(SharedConstants.SET_CUSTOM_VEIN_SPEED, StringUtil.convertToInt(speed, 0));
						setCustomVeinSpeed(speed);
					}
					
				}
			}
		});
	}
	
	private void initVariable(){
		setSelectedView(mStopArtTxt);
		setSelectedView(mStopVeinTxt);

		mTempMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);//常温
		if(mTempMode == 0){
			//常温灌注为点击状态，冷灌注未非点击状态
			mNMP_Rb.setChecked(true);
			mHMP_Rb.setChecked(false);
			mNMPOneTempRb.setVisibility(View.VISIBLE);
			mNMPTwoTempRb.setVisibility(View.VISIBLE);
			mHMPOneTempRb.setVisibility(View.GONE);
			mHMPTwoTempRb.setVisibility(View.GONE);
			mRTempTxt.setContentBackgroud(R.drawable.shape_gradient_artery_txt_bg);
			
		}else{
			mNMP_Rb.setChecked(false);
			mHMP_Rb.setChecked(true);
			mNMPOneTempRb.setVisibility(View.GONE);
			mNMPTwoTempRb.setVisibility(View.GONE);
			mHMPOneTempRb.setVisibility(View.VISIBLE);
			mHMPTwoTempRb.setVisibility(View.VISIBLE);
			mRTempTxt.setContentBackgroud(R.drawable.shape_gradient_cold_perfusion_mode_bg);
		}
					
	}
	
	/**注册压力和流量的广播**/
	private void registerReceiver(){
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_SREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_SREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_TREAL);
		mReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, mIntentFilter);		
	}

	
	private void unregisterReceiver(){
		if(null != mReceiver){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
			mReceiver = null;			
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.set_stop_artery_speed_text:
		case R.id.set_low_artery_speed_text:
		case R.id.set_middle_artery_speed_text:
		case R.id.set_high_artery_speed_text:
		case R.id.set_large_high_artery_speed_text:
			Log.i("PreperfusionFragment", "--v.toString().trim()--" + ((TextView)v).getText().toString());
			clrAllArtSelectedBg();
			setSelectedView((TextView)v);
			mCtmArtSpeedEt.setVisibility(View.VISIBLE);
			mCtmArtSpeedEt.setText("");

			if(null != mListener){
				mListener.sendArtTarSpeedMsg(((TextView)v).getText().toString());
			}
			break;
			
		case R.id.set_stop_vein_speed_text:
		case R.id.set_low_vein_speed_text:
		case R.id.set_middle_vein_speed_text:
		case R.id.set_high_vein_speed_text:
		case R.id.set_large_high_vein_speed_text:
			Log.i("PreperfusionFragment", "--v.toString().trim()--" + ((TextView)v).getText().toString());
			clrAllVeinSelectedBg();
			setSelectedView((TextView)v);
			mCtmVeinSpeedEt.setVisibility(View.VISIBLE);
			mCtmVeinSpeedEt.setText("");
			if(null != mListener){
				mListener.sendVeinTarSpeedMsg(((TextView)v).getText().toString());
			}
			break;
			
		case R.id.complete_btn:
			if(null != mListener){
				mListener.compeletePerfusion();
			}
			break;
			
		case R.id.set_custom_artery_speed_text:
			clrAllArtSelectedBg();
			setSelectedView(mCtmArtSpeedEt);
			mCtmArtSpeedEt.setText("");
			break;
			
		case R.id.set_custom_vein_speed_text:
			clrAllVeinSelectedBg();
			setSelectedView(mCtmVeinSpeedEt);
			mCtmVeinSpeedEt.setText("");
			break;
			
		case R.id.pre_perfusion_art_zero_img:
			setAreryFlowZero();
			break;
			
		case R.id.pre_perfusion_vein_zero_img:
			setVeinFlowZero();
			break;
			
		case R.id.pre_perfusion_art_pressure_zero_img:
			setArtPreZero();
			break;
			
		case R.id.pre_perfusion_vein_zero_pre_img:
			setVeinPreZero();
			break;
			
		case R.id.set_custom_temp_text:
			setSelectedView(mCustomTempEt);
			mTempSetRg.clearCheck();
			mCustomTempEt.setText("");
			break;
			
		default:
			break;
		}
	}
	

	private void clrAllArtSelectedBg(){
		setNormalView(mStopArtTxt);
		setNormalView(mArtLowSpeedTxt);
		setNormalView(mArtMidSpeedTxt);
		setNormalView(mArtHighSpeedTxt);
		setNormalView(mArtLHighSpeedTxt);
		setNormalView(mCtmArtSpeedEt);
	}
	
	private void clrAllVeinSelectedBg(){
		setNormalView(mStopVeinTxt);
		setNormalView(mVeinLowSpeedTxt);
		setNormalView(mVeinMidSpeedTxt);
		setNormalView(mVeinHighSpeedTxt);
		setNormalView(mVeinLHighSpeedTxt);
		setNormalView(mCtmVeinSpeedEt);
	}
	

	/**
	 * 下发自定义肝动脉泵速
	 * @param artSpeed
	 */
	private void setCustomArtSpeed(String artSpeed){
		
		if(null != mListener){
			mListener.sendArtTarSpeedMsg(artSpeed);
			setSelectedView(mCtmArtSpeedEt);
			mCtmArtSpeedEt.setText(String.valueOf(artSpeed));
		}
				
	}

	private void setCustomVeinSpeed(String veinSpeed){
		
		if(null != mListener){
			mListener.sendVeinTarSpeedMsg(veinSpeed);
			setSelectedView(mCtmVeinSpeedEt);
			mCtmVeinSpeedEt.setText(String.valueOf(veinSpeed));
		}		
	}
	
	private void setAreryFlowZero(){
		if(null != mListener){
			mListener.sendZeroFlowMsg(1);
		}
	}
	
	private void setVeinFlowZero(){
		if(null != mListener){
			mListener.sendZeroFlowMsg(2);
		}
	}
	
	private void setArtPreZero(){
		if(null != mListener){
			mListener.sendZeroPressureMsg(1);
		}
	}
	
	private void setVeinPreZero(){
		if(null != mListener){
			mListener.sendZeroPressureMsg(2);
		}
	}
	
	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();			
			
			if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
				//肝动脉实时压力
				if(null != mArtPreBar){
					String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);
					float Preal_one = 0.0f;
					if(!StringUtil.isEmpty(pump1_Preal)){					
						Preal_one = StringUtil.convertToFloat(pump1_Preal, 0.0f);												
					}
					mArtPreBar.setProgress((int)Preal_one);
				}
				
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
				if(null != mArtFlowBar){
					String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);
					float FReal_one = 0.0f;
					if(!StringUtil.isEmpty(pump1_Freal) 
							&& !pump1_Freal.equals("error1")
							&& !pump1_Freal.equals("error2")){
						FReal_one = StringUtil.convertToFloat(pump1_Freal,0.0f);									
					}
					mArtFlowBar.setProgress((int)FReal_one);
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN)){
				if(null != mMArtPreTxt){
					String pump1_PMean = intent.getStringExtra(SerialMsgConstant.ARTERY_PMEAN); 
					mMArtPreTxt.setText(pump1_PMean);											
				}

			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN)){
				if(null != mMArtFlowTxt){
					String pump1_Fmean = intent.getStringExtra(SerialMsgConstant.ARTERY_FMEAN);
					if(!StringUtil.isEmpty(pump1_Fmean)){	
						if(pump1_Fmean.equals("error1")||pump1_Fmean.equals("error2")){
							mMArtFlowTxt.setText(pump1_Fmean);	
						}else{
							int artery_Fmean = StringUtil.IgnoreUnities(StringUtil.convertToInt(pump1_Fmean, 0));
							mMArtFlowTxt.setText(artery_Fmean + "");	
						}
					
					}							
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN)){
				//平均压
				if(null != mMVeinPreTxt && null != mVeinPreBar){
					String pump2_Pmean = intent.getStringExtra(SerialMsgConstant.VEIN_PMEAN);
					float mMeanP = StringUtil.convertToFloat(pump2_Pmean, 0.0f);
					if(!StringUtil.isEmpty(pump2_Pmean)){					
						mMVeinPreTxt.setText(pump2_Pmean);					
						
					}else{
						mMVeinPreTxt.setText(+ R.string.string_null);	
					}
					mVeinPreBar.setProgress((int)mMeanP);
				}	
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN)){
				if(null != mMVeinFlowTxt){
					String pump2_Fmean = intent.getStringExtra(SerialMsgConstant.VEIN_FMEAN);
					if(!StringUtil.isEmpty(pump2_Fmean)){	
						if(pump2_Fmean.equals("error1")||pump2_Fmean.equals("error2")){
							mMVeinFlowTxt.setText(pump2_Fmean);	
						}else{
							int vein_Fmean = StringUtil.IgnoreUnities(StringUtil.convertToInt(pump2_Fmean, 0));
							mMVeinFlowTxt.setText(vein_Fmean + "");	
						}
					
					}
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
				if(null != mVeinFlowBar){
					String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
					float FReal_two = 0.0f;
					if(!StringUtil.isEmpty(pump2_Freal) 
							&& !pump2_Freal.equals("error1")
							&& !pump2_Freal.equals("error2")){
						FReal_two = StringUtil.convertToFloat(pump2_Freal,0.0f);													
					}
					mVeinFlowBar.setProgress((int)FReal_two);
				}				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_SREAL)){
				if(null != mRArtSpeedTxt){
					String pump1_Sreal = intent.getStringExtra(SerialMsgConstant.ARTERY_SPEED);
					mRArtSpeedTxt.setContextText(pump1_Sreal);
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_SREAL)){
				if(null != mRVeinSpeedTxt){
					String pump2_Sreal = intent.getStringExtra(SerialMsgConstant.VEIN_SPEED);
					mRVeinSpeedTxt.setContextText(pump2_Sreal);
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_TREAL)){
				if(null != mRTempTxt){
					String temp = intent.getStringExtra(SerialMsgConstant.ARTERY_TEMP);
					if(null != temp){
						mRTempTxt.setContextText(temp);
					}
				}
			}
			

		}		
	}





}
