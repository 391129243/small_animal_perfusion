package com.gidi.bio_console.kidney;


import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;

import com.gidi.bio_console.view.CustomTempModeButton;
import com.gidi.bio_console.view.CustomTempModeButton.OnTempModeButtonClickListener;
import com.gidi.bio_console.view.SystemKeyBoardEditText;


public class KidneyPatternFragment extends BaseFragment implements OnClickListener{
	private static final String TAG = "PresetPatternFragment";
	private ImageView mNextBtn;
	private CustomTempModeButton mHMP_ModeBtn;//冷灌注
	private CustomTempModeButton mNMP_ModeBtn;//常温灌注
	private SystemKeyBoardEditText mCustomTempEt;
	private RadioGroup mTempSetRg;
	private TextView mTempTxt;
	private RadioButton mHMPOneTempRb,mHMPTwoTempRb, mNMPOneTempRb, mNMPTwoTempRb;

	/**左肾动脉搏动**/
	/**左肾动脉恒压**/
	/**左肾动脉恒流**/
	private RadioGroup mLAModeRg;//左肾动脉
	private RadioButton mLAPulsePImg,mLAConstPImg,mLAConstFImg;
	
	
	/**右肾动脉搏动**/
	/**右肾动脉恒压**/
	/**右肾动脉恒流**/
	private RadioGroup mRAModeRg;//左肾动脉
	private RadioButton mRAPulsePImg,mRAConstPImg,mRAConstFImg;
	private TextView mLAModeTitleTv;
	private TextView mLAdividerTv,	mLArtMaxTv, mLArtMinTv,	mLArtConstTv;//恒压显示压力数值
	private SystemKeyBoardEditText mLArtMinPEt, mLArtMaxPEt;
	private SystemKeyBoardEditText mLArtConstPEt;
	private SystemKeyBoardEditText mLArtConstFEt;//左肾动脉恒流
	private RelativeLayout mLAConstPressureLayout;
	private RelativeLayout mLAPulsePressureLayout;
	private LinearLayout mLAConstFlowLayout;
	private RadioGroup mLArtMaxSetPreRg,mLArtMinSetPreRg;
	private RadioGroup mLArtConstSetPreRg,mLArtConstSetFlowRg;
	
	
	private TextView mRAdividerTv,	mRArtMaxTv, mRArtMinTv,	mRArtConstTv;//右肾恒压显示压力数值
	private SystemKeyBoardEditText mRAMinPEt, mRAMaxPEt;//右肾动脉
	private SystemKeyBoardEditText mRArtConstPEt;//右肾动脉恒压
	private SystemKeyBoardEditText mRArtConstFEt;//右肾动恒流
	private RadioGroup mRArtMaxSetPreRg,mRArtMinSetPreRg;
	private RadioGroup mRAConstSetPreRg, mRAConstSetFlowRg;
	private RelativeLayout mRAConstPressureLayout;
	private RelativeLayout mRAPulsePressureLayout;
	private LinearLayout mRAConstFlowLayout;


	private TextView mRAModeTitle;


	private float mLArtMinTarPre,mLArtMaxTarPre;
	private float mLArtTarPre;
	private float mLArtTarFlow;
	
	private float mRArtMinTarPre,mRArtMaxTarPre;
	private float mRArtTarPre;
	private float mRArtTarFlow;
	
	/**0: normal 1: cold**/
	private int mTempMode = 0;
	/**默认肝动脉是搏动门静脉是恒压**/
	private int mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;
	private int mVeinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;


	
	private onKidneyPatternListener mOnPresetPatternListener;
	
	public interface onKidneyPatternListener{
		//左肾动脉恒压，右肾动脉恒压
		void LAConstPreRAConstPre(float mLArtTarPre, float mRArtTarPre);
		//左肾动脉恒压，右肾动脉恒流
		void LAConstPreRAConstFlow(float mLArtTarPre,float mRArtTarFlow);
		//左肾动脉恒压，右肾动脉搏动
		void LAConstPreRAPulsePre(float mLArtTarPre,float mRArtMinTarPre,float mRArtMaxTarPre);
		
		
		//左肾搏动，右肾恒压
		void LArtMinMaxPreRAConstPre(float mLArtMinTarPre,float mLArtMaxTarPre, float mRArtTarPre);
		//左肾搏动，右肾恒压
		void LArtMinMaxPreRAConstFlow(float mLArtMinTarPre,float mLArtMaxTarPre, float mRArtTarFlow);
		//左肾搏动，右肾搏动
		void LArtMinMaxPreRArtMinMaxPre(float mLArtMinTarPre,float mLArtMaxTarPre, float mRArtMinTarPre,float mRArtMaxTarPre);
		
		//左肾动脉恒流，右肾动脉恒压
		void LAConstFlowRAConstPre(float mLArtTarFlow, float mRArtTarPre);
		//左肾动脉恒六，右肾动脉恒流
		void LAConstFlowRAConstFlow(float mLArtTarFlow,float mRArtTarFlow);
		//左肾动脉恒流，右肾动脉搏动
		void LAConstFlowRAPulsePre(float mLArtTarFlow,float mRArtMinTarPre,float mRArtMaxTarPre);
	}
	
	public void setOnKidneyPatternListener(onKidneyPatternListener listener){
		this.mOnPresetPatternListener = listener;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initStates();
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_kidney_pattern;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mNextBtn = (ImageView)rootView.findViewById(R.id.pattern_setting_complete_btn);
		mLAModeRg = (RadioGroup)rootView.findViewById(R.id.pattern_left_kidney_artery_mode_rg);
		mLAPulsePImg = (RadioButton)rootView.findViewById(R.id.pattern_left_kidney_artery_pulse_pressure_btn);
		mLAConstPImg = (RadioButton)rootView.findViewById(R.id.pattern_left_kidney_artery_const_pressure_btn);
		mLAConstFImg = (RadioButton)rootView.findViewById(R.id.pattern_left_kidney_artery_const_flow_btn);
		
		mRAModeRg = (RadioGroup)rootView.findViewById(R.id.pattern_right_kidney_artery_mode_rg);
		mRAPulsePImg = (RadioButton)rootView.findViewById(R.id.pattern_right_kidney_artery_pulse_pressure_img);
		mRAConstPImg = (RadioButton)rootView.findViewById(R.id.pattern_right_kidney_artery_const_pressure_img);
		mRAConstFImg = (RadioButton)rootView.findViewById(R.id.pattern_right_kidney_artery_const_flow_img);
		
		mHMP_ModeBtn = (CustomTempModeButton)rootView.findViewById(R.id.pattern_setting_temp_mode_cold_btn);
		mNMP_ModeBtn = (CustomTempModeButton)rootView.findViewById(R.id.pattern_setting_temp_mode_normal_btn);	
		
		mLAModeTitleTv = (TextView)rootView.findViewById(R.id.pre_left_artery_target_pressure_title_tv);
		mLAdividerTv = (TextView)rootView.findViewById(R.id.pre_left_kidney_art_divider_tv);
		mLArtMinTv = (TextView)rootView.findViewById(R.id.pre_left_kidney_art_min_tv);
		mLArtMaxTv = (TextView)rootView.findViewById(R.id.pre_left_kidney_art_max_tv);
		mLArtConstTv = (TextView)rootView.findViewById(R.id.pre_left_kidney_art_const_tv);
		mLArtMinPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_left_kidney_art_min_tarpressure_et);
		mLArtMaxPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_left_kidney_art_max_tarpressure_et);
		mLArtConstPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_left_kidney_art_const_tarpressure_et);
		mLArtConstFEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_left_artery_target_flow_et);
		mLAPulsePressureLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_left_kidney_artery_pulse_pressure_layout);
		mLAConstPressureLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_left_kidney_artery_const_pressure_layout);
		mLAConstFlowLayout = (LinearLayout)rootView.findViewById(R.id.pattern_left_artery_const_flow_layout);		
		mLArtMaxSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_left_kidney_art_pulse_max_pre_radiogroup);
		mLArtMinSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_left_kidney_art_pulse_min_pre_radiogroup);
		mLArtConstSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_left_kidney_art_pulse_const_pre_radiogroup);
		mLArtConstSetFlowRg = (RadioGroup)rootView.findViewById(R.id.pattern_left_artery_const_flow_radiogroup);

		
		mRAModeTitle = (TextView)rootView.findViewById(R.id.pre_right_kidney_target_title_tv);
		mRAdividerTv =(TextView)rootView.findViewById(R.id.pre_right_kidney_art_divider_tv);
		mRArtMaxTv  =(TextView)rootView.findViewById(R.id.pre_right_kidney_art_max_tv); 
		mRArtMinTv =(TextView)rootView.findViewById(R.id.pre_right_kidney_art_min_tv);
		mRArtConstTv=  (TextView)rootView.findViewById(R.id.pre_right_kidney_art_const_tv);
		mRArtMaxSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_right_kidney_art_pulse_max_pre_radiogroup);
		mRArtMinSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_right_kidney_art_pulse_min_pre_radiogroup);
		mRAConstSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_right_kidney_art_pulse_const_pre_radiogroup);
		mRAConstSetFlowRg = (RadioGroup)rootView.findViewById(R.id.pattern_right_artery_const_flow_radiogroup);
		mRAMinPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_right_kidney_art_min_tarpressure_et);
		mRAMaxPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_right_kidney_art_max_tarpressure_et);
		mRArtConstFEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_right_kidney_art_const_flow_et);
		mRArtConstPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_right_kidney_art_const_tarpressure_et);
		mRAPulsePressureLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_right_kidney_pulse_pressure_layout);
		mRAConstPressureLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_right_kidney_const_pressure_layout);
		mRAConstFlowLayout = (LinearLayout)rootView.findViewById(R.id.pattern_right_artery_const_flow_layout);	
		
		
		
		
		mHMPOneTempRb = (RadioButton)rootView.findViewById(R.id.pattern_set_temp_four_rb);
		mHMPTwoTempRb = (RadioButton)rootView.findViewById(R.id.pattern_set_temp_eight_rb);
		mNMPOneTempRb = (RadioButton)rootView.findViewById(R.id.pattern_set_temp_twenty_five_rb); 
		mNMPTwoTempRb = (RadioButton)rootView.findViewById(R.id.pattern_set_temp_thirdty_seven_rb);		
		mTempTxt = (TextView)rootView.findViewById(R.id.pre_setting_temp_txt);
		mTempSetRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_temp_radiogroup);
		mCustomTempEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_art_temp_et);
		mTempMode = PreferenceUtil.getInstance(getActivity().getApplicationContext()).getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);//常温
		if(mTempMode == 0){
			//常温灌注为点击状态，冷灌注未非点击状态
			mNMP_ModeBtn.setBackgroudResId(R.drawable.pattern_mode_normal_pressed_bg);
			mNMP_ModeBtn.setImageBtnResoure(R.drawable.temp_mode_pressed,R.color.white);
			mHMPOneTempRb.setVisibility(View.GONE);
			mHMPTwoTempRb.setVisibility(View.GONE);
			mNMPOneTempRb.setVisibility(View.VISIBLE);
			mNMPTwoTempRb.setVisibility(View.VISIBLE);
		}else{
			mHMP_ModeBtn.setImageResId(R.drawable.pattern_mode_cold_pressed_bg);
			mHMP_ModeBtn.setImageBtnResoure(R.drawable.temp_mode_pressed,R.color.white);
			mHMPOneTempRb.setVisibility(View.VISIBLE);
			mHMPTwoTempRb.setVisibility(View.VISIBLE);
			mNMPOneTempRb.setVisibility(View.GONE);
			mNMPTwoTempRb.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mTempSetRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_set_temp_four_rb:
				case R.id.pattern_set_temp_eight_rb:
				case R.id.pattern_set_temp_twenty_five_rb:
				case R.id.pattern_set_temp_thirdty_seven_rb:
					setNormalView(mCustomTempEt);
					String temperature = radioButton.getText().toString();					
					mTempTxt.setText(temperature);
					break;
				default:
					break;
				}
			}
		});
		//自定义温度的输入
		mCustomTempEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float temp = StringUtil.convertToFloat(editable.toString().trim(), (float)37.0);
				if(temp >  37.0f){
					editable.replace(0,editable.length(), "37");
					displayToast(R.string.error_target_temp_max_tip);
					mTempTxt.setText(37 + "");
					return;
				}else{
					mTempTxt.setText(temp+"");
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String target_temp = mCustomTempEt.getText().toString().trim();
				if(StringUtil.isEmpty(target_temp)){
					mTempTxt.setText(getResources().getString(R.string.string_null));
				}else{
					if(StringUtil.isDicimals(target_temp)){
						mTempTxt.setText(target_temp);
					}else{
						mTempTxt.setText(StringUtil.getInteger(target_temp));
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
				String target_temp = mCustomTempEt.getText().toString().trim();
				if(StringUtil.isEmpty(target_temp)){
					mTempTxt.setText(getResources().getString(R.string.string_null));
				}else{
					if(StringUtil.isDicimals(target_temp)){
						mTempTxt.setText(target_temp);
					}else{
						mTempTxt.setText(StringUtil.getInteger(target_temp));
					}
				}
			}
		});
		mCustomTempEt.setOnClickListener(this);
		
		//左肾动脉模式选择
		mLAModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
				// TODO Auto-generated method stub
				switch (checkId) {
				case R.id.pattern_left_kidney_artery_pulse_pressure_btn:
					mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;
					mLAModeTitleTv.setText(R.string.preset_pattern_left_target_artery_pressure);
					mLAPulsePressureLayout.setVisibility(View.VISIBLE);
					mLAConstFlowLayout.setVisibility(View.GONE);
					mLAConstPressureLayout.setVisibility(View.GONE);
					mLArtMaxTv.setVisibility(View.VISIBLE);
					mLArtMinTv.setVisibility(View.VISIBLE);
					mLAdividerTv.setVisibility(View.VISIBLE);			
					mLArtConstTv.setVisibility(View.GONE);
					break;
				case R.id.pattern_left_kidney_artery_const_pressure_btn:
					mArtMode = Constants.LEFT_ARTERY_CONST_PRESSURE_MODE;
					mLAModeTitleTv.setText(R.string.preset_pattern_left_target_artery_pressure);
					mLAPulsePressureLayout.setVisibility(View.GONE);
					mLAConstPressureLayout.setVisibility(View.VISIBLE);
					mLAConstFlowLayout.setVisibility(View.GONE);
					mLArtMaxTv.setVisibility(View.GONE);
					mLArtMinTv.setVisibility(View.GONE);
					mLAdividerTv.setVisibility(View.GONE);
					mLArtConstTv.setVisibility(View.VISIBLE);
					break;
				case R.id.pattern_left_kidney_artery_const_flow_btn:
					mArtMode = Constants.LEFT_ARTERY_CONST_FLOW_MODE;
					mLAModeTitleTv.setText(R.string.preset_pattern_left_target_artery_flow);
					mLAConstFlowLayout.setVisibility(View.VISIBLE);
					mLAPulsePressureLayout.setVisibility(View.GONE);
					mLAConstPressureLayout.setVisibility(View.GONE);
					mLArtMaxTv.setVisibility(View.GONE);
					mLArtMinTv.setVisibility(View.GONE);
					mLAdividerTv.setVisibility(View.GONE);
					mLArtConstTv.setVisibility(View.VISIBLE);
					break;
					
				default:
					break;
				}
			}
		});
		
	
		//左肾动脉最大值选择		
		mLArtMaxSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_left_kidney_art_pulse_pre_one_rb:
				case R.id.pattern_left_kidney_art_pulse_pre_two_rb:
				case R.id.pattern_left_kidney_art_pulse_pre_third_rb:
				case R.id.pattern_left_kidney_art_pulse_pre_fourth_rb:
					setNormalView(mLArtMaxPEt);
					String maxPre = radioButton.getText().toString();					
					mLArtMaxTv.setText(maxPre);
					break;
				default:
					break;
				}
			}
		});
		
		mLArtMinSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_left_kidney_art_pulse_min_pre_one_rb:
				case R.id.pattern_left_kidney_art_pulse_min_pre_two_rb:
				case R.id.pattern_left_kidney_art_pulse_min_pre_third_rb:
				case R.id.pattern_left_kidney_art_pulse_min_pre_fourth_rb:
					setNormalView(mLArtMinPEt);
					String minPre = radioButton.getText().toString();					
					mLArtMinTv.setText(minPre);
					break;
				default:
					break;
				}
			}
		});
		
		mLArtConstSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int isCheck) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_left_kidney_art_const_pre_one_rb:
				case R.id.pattern_left_kidney_art_const_pre_two_rb:
				
					setNormalView(mLArtConstPEt);
					String constPre = radioButton.getText().toString();					
					mLArtConstTv.setText(constPre);
					break;
				case R.id.pattern_left_kidney_art_const_pre_third_rb:
				case R.id.pattern_left_kidney_art_const_pre_fourth_rb:		
					if(mTempMode == 1){
						//冷灌注
						displayToast(R.string.error_max_value_cold_perfusion_target_const_left_kidney_artery_pre);
						return;
					}else{
						String left_const_Pre= radioButton.getText().toString();	
						setNormalView(mLArtConstPEt);
						mLArtConstTv.setText(left_const_Pre);
					}
				default:
					break;
				}
			}
		});
		
		mLArtConstSetFlowRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int isCheck) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_left_artery_const_flow_one_rb:
				case R.id.pattern_left_artery_const_flow_two_rb:
				case R.id.pattern_left_rtery_const_flow_third_rb:
				case R.id.pattern_left_rtery_const_flow_fourth_rb:
					setNormalView(mLArtConstFEt);
					String constPre = radioButton.getText().toString();					
					mLArtConstTv.setText(constPre);
					break;
				default:
					break;
				}
			}
		});
/************************************右肾动脉设置**************************************************************/
	mRAModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
				// TODO Auto-generated method stub
				switch (checkId) {
				case R.id.pattern_right_kidney_artery_pulse_pressure_img:
					mVeinMode = Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE;
					mRAModeTitle.setText(R.string.preset_pattern_right_target_artery_pressure);
					mRAConstFlowLayout.setVisibility(View.GONE);
					mRAConstPressureLayout.setVisibility(View.GONE);	
					mRAPulsePressureLayout.setVisibility(View.VISIBLE);
					mRArtMaxTv.setVisibility(View.VISIBLE);
					mRArtMinTv.setVisibility(View.VISIBLE);
					mRAdividerTv.setVisibility(View.VISIBLE);			
					mRArtConstTv.setVisibility(View.GONE);
					break;
				case R.id.pattern_right_kidney_artery_const_pressure_img:
					mVeinMode = Constants. RIGHT_ARTERY_CONST_PRESSURE_MODE;
					
					mRAModeTitle.setText(R.string.preset_pattern_right_target_artery_pressure);
					mRAConstFlowLayout.setVisibility(View.GONE);
					mRAConstPressureLayout.setVisibility(View.VISIBLE);	
					mRAPulsePressureLayout.setVisibility(View.GONE);
					mRArtMaxTv.setVisibility(View.GONE);
					mRArtMinTv.setVisibility(View.GONE);
					mRAdividerTv.setVisibility(View.GONE);			
					mRArtConstTv.setVisibility(View.VISIBLE);
					break;

				case R.id.pattern_right_kidney_artery_const_flow_img:
					mVeinMode = Constants.RIGHT_ARTERY_CONST_FLOW_MODE;
					mRAModeTitle.setText(R.string.preset_pattern_right_target_artery_flow);
					mRAConstFlowLayout.setVisibility(View.VISIBLE);
					mRAConstPressureLayout.setVisibility(View.GONE);	
					mRAPulsePressureLayout.setVisibility(View.GONE);
					mRArtMaxTv.setVisibility(View.GONE);
					mRArtMinTv.setVisibility(View.GONE);
					mRAdividerTv.setVisibility(View.GONE);			
					mRArtConstTv.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		});
		
		
		
		mRArtMaxSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_right_kidney_art_pulse_pre_one_rb:
				case R.id.pattern_right_kidney_art_pulse_pre_two_rb:
				case R.id.pattern_right_kidney_art_pulse_pre_third_rb:
				case R.id.pattern_right_kidney_art_pulse_pre_fourth_rb:
					setNormalView(mRAMaxPEt);
					String maxPre = radioButton.getText().toString();					
					mRArtMaxTv.setText(maxPre);
					break;
				default:
					break;
				}
			}
		});
		mRArtMinSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_right_kidney_art_pulse_min_pre_one_rb:
				case R.id.pattern_right_kidney_art_pulse_min_pre_two_rb:
				case R.id.pattern_right_kidney_art_pulse_min_pre_third_rb:
				case R.id.pattern_right_kidney_art_pulse_min_pre_fourth_rb:
					setNormalView(mRAMinPEt);
					String minPre = radioButton.getText().toString();					
					mRArtMinTv.setText(minPre);
					break;
				default:
					break;
				}
			}
		});
		//有肾动脉恒压
		mRAConstSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				
				switch (id) {				
				case R.id.pattern_right_kidney_art_const_pre_one_rb:
				case R.id.pattern_right_kidney_art_const_pre_two_rb:				
					String vein_const_Pre_1 = radioButton.getText().toString();
					setNormalView(mRArtConstPEt);				
					mRArtConstTv.setText(vein_const_Pre_1);
					break;
				case R.id.pattern_right_kidney_art_const_pre_third_rb:
				case R.id.pattern_right_kidney_art_const_pre_fourth_rb:
					if(mTempMode == 1){
						//冷灌注
						displayToast(R.string.error_max_value_cold_perfusion_target_const_right_kidney_artery_pre);
						return;
					}else{
						String right_const_Pre = radioButton.getText().toString();
						setNormalView(mRArtConstPEt);											
						mRArtConstTv.setText(right_const_Pre);
					}
					break;
				default:
					break;
				}
			}
		});
		
		mRAConstSetFlowRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_right_artery_const_flow_one_rb:
				case R.id.pattern_right_artery_const_flow_two_rb:
				case R.id.pattern_right_artery_const_flow_third_rb:
				case R.id.pattern_right_artery_const_flow_fourth_rb:
					setNormalView(mRArtConstFEt);
					String veinFlow = radioButton.getText().toString();					
					mRArtConstTv.setText(veinFlow);
					break;
				default:
					break;
				}
			}
		});
		mRAMinPEt.setOnClickListener(this);
		mRAMinPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 100.0f){
					displayToast(R.string.error_target_right_kidney_pre_min_limit);
					mRArtMinTv.setText(100 + "");
				}else{
					mRArtMinTv.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_min_pressure = mRAMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mRArtMinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtMinTv.setText(art_min_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_min_pressure = mRAMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mRArtMinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtMinTv.setText(art_min_pressure);
				}
			}
		});
		
		mRAMaxPEt.setOnClickListener(this);
		mRAMaxPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 120.0f){
					displayToast(R.string.error_target_right_kidney_pre_max_limit);
					mRArtMaxTv.setText(120 + "");
				}else{
					mRArtMaxTv.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_max_pressure = mRAMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mRArtMaxTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtMaxTv.setText(art_max_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_max_pressure = mRAMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mRArtMaxTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtMaxTv.setText(art_max_pressure);
				}
			}
		});
		//右肾恒流编辑框
		mRArtConstFEt.setOnClickListener(this);
		mRArtConstFEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strveinConstFlow = editable.toString();
				float veinConstFlow = StringUtil.convertToFloat(strveinConstFlow, 0.0f);
				if(veinConstFlow > 1500.0f){
					displayToast(R.string.error_max_value_right_kidney_target_flow);
					mRArtConstTv.setText(1500 +"");
					return;
				}else{
					mRArtConstTv.setText(editable);
				}

			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_flow = mRArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mRArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					
					mRArtConstTv.setText(vein_const_flow);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mRArtConstTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_flow = mRArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mRArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtConstTv.setText(vein_const_flow);
				}
			}
		});
		//右肾动脉恒压
		mRArtConstPEt.setOnClickListener(this);
		mRArtConstPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strveinConstPre = editable.toString();
				float veinConstPre = StringUtil.convertToFloat(strveinConstPre, 0.0f);
				if(mTempMode == 0){//常温灌注不高于14mmHg，低温灌注不高于8mmHg
					if(veinConstPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_right_kidney_artery_pre);
						mRArtConstTv.setText(100+"");
						return;
					}else{
						mRArtConstTv.setText(editable);
					}
				}else if(mTempMode == 1){
					if(veinConstPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_right_kidney_artery_pre);
						mRArtConstTv.setText(40+"");
						return;
					}else{
						mRArtConstTv.setText(editable);
					}
				}
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mRArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mRArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtConstTv.setText(vein_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mRArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mRArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mRArtConstTv.setText(vein_const_pressure);
				}
			}
		});
		
/*******************************************************************************************************/		

		mHMP_ModeBtn.setOnTempModeBtnClickListener(new OnTempModeButtonClickListener() {
			
			@Override
			public void CustomTempModeButtonClicked(View v) {
				// TODO Auto-generated method stub
				mHMP_ModeBtn.setBackgroudResId(R.drawable.pattern_mode_cold_pressed_bg);
				mHMP_ModeBtn.setImageBtnResoure(R.drawable.temp_mode_pressed,R.color.white);
				mNMP_ModeBtn.setBackgroudResId(R.drawable.pattern_mode_normal_bg_selector);
				mNMP_ModeBtn.setImageBtnResoure(R.drawable.temp_normal_mode,R.color.color_text_temp_normal);
				mTempMode = 1;
				mHMPOneTempRb.setVisibility(View.VISIBLE);
				mHMPTwoTempRb.setVisibility(View.VISIBLE);
				mNMPOneTempRb.setVisibility(View.GONE);
				mNMPTwoTempRb.setVisibility(View.GONE);
				//冷灌注
				PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
			}
		});
		mNMP_ModeBtn.setOnTempModeBtnClickListener(new OnTempModeButtonClickListener() {
			
			@Override
			public void CustomTempModeButtonClicked(View v) {
				// TODO Auto-generated method stub
				mNMP_ModeBtn.setBackgroudResId(R.drawable.pattern_mode_normal_pressed_bg);
				mNMP_ModeBtn.setImageBtnResoure(R.drawable.temp_mode_pressed,R.color.white);
				mHMP_ModeBtn.setBackgroudResId(R.drawable.pattern_mode_cold_bg_selector);
				mHMP_ModeBtn.setImageBtnResoure(R.drawable.temp_cold_mode,R.color.color_text_temp_cold);
				mTempMode = 0;
				mHMPOneTempRb.setVisibility(View.GONE);
				mHMPTwoTempRb.setVisibility(View.GONE);
				mNMPOneTempRb.setVisibility(View.VISIBLE);
				mNMPTwoTempRb.setVisibility(View.VISIBLE);
				PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
			}
		});
		
		
		mLArtMinPEt.setOnClickListener(this);
		mLArtMinPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 100.0f){
					displayToast(R.string.error_target_left_kidney_pre_min_limit);
					mLArtMinTv.setText(100 + "");
				}else{
					mLArtMinTv.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_min_pressure = mLArtMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mLArtMinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtMinTv.setText(art_min_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_min_pressure = mLArtMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mLArtMinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtMinTv.setText(art_min_pressure);
				}
			}
		});
		
		mLArtMaxPEt.setOnClickListener(this);
		mLArtMaxPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 120.0f){
					displayToast(R.string.error_target_left_kidney_pre_max_limit);
					mLArtMaxTv.setText(120 + "");
				}else{
					mLArtMaxTv.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_max_pressure = mLArtMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mLArtMaxTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtMaxTv.setText(art_max_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_max_pressure = mLArtMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mLArtMaxTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtMaxTv.setText(art_max_pressure);
				}
			}
		});
		
		mLArtConstPEt.setOnClickListener(this);
		mLArtConstPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float constTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);
				if(mTempMode == 0){
					if(constTarPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_left_kidney_artery_pre);
						mLArtConstTv.setText(100 + "");
						return;
					}else{
						mLArtConstTv.setText(editable);
					}
				}else if(mTempMode == 1){
					if(constTarPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_left_kidney_artery_pre);
						mLArtConstTv.setText(40 + "");
						return;
					}else{
						mLArtConstTv.setText(editable);
					}
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_const_pressure = mLArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mLArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtConstTv.setText(art_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_const_pressure = mLArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mLArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtConstTv.setText(art_const_pressure);
				}
			}
		});
		
		mLArtConstFEt.setOnClickListener(this);
		mLArtConstFEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strleftArtConstFlow = editable.toString();
				float leftArtConstFlow = StringUtil.convertToFloat(strleftArtConstFlow, 0.0f);
				if(leftArtConstFlow > 1500.0f){
					displayToast(R.string.error_max_value_left_kidney_target_flow);
					mLArtConstTv.setText(1500 +"");
					return;
				}else{
					mLArtConstTv.setText(editable);
				}

			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String leftArtConstFlow = mLArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(leftArtConstFlow)){
					mLArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					
					mLArtConstTv.setText(leftArtConstFlow);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mLArtConstTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_flow = mLArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mLArtConstTv.setText(getResources().getString(R.string.string_null));
				}else{
					mLArtConstTv.setText(vein_const_flow);
				}
			}
		});
		mNextBtn.setOnClickListener(this);		
	}

	private void initStates(){
		mRAPulsePImg.setChecked(true);
		mLAPulsePImg.setChecked(true);
		
	}
	


	/**保存肝动脉灌注模式**/
	private void savePerfusionModeSetting(){
		String temp = mTempTxt.getText().toString().trim();
		float target_Temp = StringUtil.convertToFloat(mTempTxt.getText().toString().trim(), 0.0f);
		if(StringUtil.isEmpty(temp)||temp.equals(getResources().getString(R.string.string_null))){
			displayToast(R.string.error_target_temp_empty);
			return;
		}else{
			if(target_Temp > 37){
				displayToast(R.string.error_target_temp_max_tip);
				return;
			}
		}
				
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			//左肾动脉恒压的情况
			String mConstArtPre = mLArtConstTv.getText().toString().trim();
			mLArtTarPre = StringUtil.convertToFloat(mConstArtPre, 0.0f);
			if(StringUtil.isEmpty(mConstArtPre)|| mConstArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_left_kidney_pre_null);
				return;
			}else if(mConstArtPre.endsWith("."))
			{
				displayToast(R.string.error_target_left_kieney_pre_value_format_error);
				return;
			}else{
				/**0: normal 1: cold**/
				if(mTempMode == 0){
					if(mLArtTarPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_left_kidney_artery_pre);
						return;
					}
				}else if(mTempMode == 1){
					if(mLArtTarPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_left_kidney_artery_pre);
						return;
					}
				}
			}
			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, mLArtTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_LEFT_KIDNEY_WARN_MAX, mLArtTarPre);
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			//左肾动脉搏动模式
			String mMinArtPre = mLArtMinTv.getText().toString().trim();
			String mMaxArtPre = mLArtMaxTv.getText().toString().trim();
			mLArtMinTarPre = StringUtil.convertToFloat(mMinArtPre, 0.0f);
			mLArtMaxTarPre = StringUtil.convertToFloat(mMaxArtPre, 0.0f);
			if(StringUtil.isEmpty(mMinArtPre)||mMinArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_left_kidney_pre_min_null);
				return;
			}else if(StringUtil.isEmpty(mMaxArtPre)||mMaxArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_left_kidney_pre_max_null);
				return;
			}else if(mLArtMaxTarPre > 120.0f){
				displayToast(R.string.error_target_left_kidney_pre_max_limit);
				return;
			}else if(mLArtMinTarPre >= 100.0f){
				displayToast(R.string.error_target_left_kidney_pre_min_limit);
				return;
			}else if(compare(mLArtMinTarPre, mLArtMaxTarPre)){
				displayToast(R.string.error_target_left_kidney_pre_min_max_compare);
				return;
			}else if(compareArtPulsePreDiff(mLArtMinTarPre, mLArtMaxTarPre)){
				displayToast(R.string.error_target_left_kidney_pre_min_max_difference);
				return;
			}

			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, mLArtMinTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, mLArtMaxTarPre);
		}else if(mArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE){
			String mConstArtflow = mLArtConstTv.getText().toString().trim();
			mLArtTarFlow = StringUtil.convertToFloat(mConstArtflow, 0.0f);
			//默认是ml
			if(StringUtil.isEmpty(mConstArtflow)|| mConstArtflow.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_left_kidney_flow_null);
				return;
			}else {
				if(mLArtTarFlow > 1500.0f){
					displayToast(R.string.error_max_value_left_kidney_target_flow);
					return;
				}
			}
			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, mLArtTarFlow);

		}

		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, mArtMode);
		if(mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			//右肾恒压
			String mConstVeinPre = mRArtConstTv.getText().toString().trim();
			mRArtTarPre = StringUtil.convertToFloat(mConstVeinPre, 0.0f);
			if(StringUtil.isEmpty(mConstVeinPre)|| mConstVeinPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_right_kidney_pre_null);
				return;
			}else if(mConstVeinPre.endsWith(".")){
				displayToast(R.string.error_target_right_kieney_pre_value_format_error);
				return;
			}else{
				
				if(mTempMode == 0){
					if(mRArtTarPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_right_kidney_artery_pre);
						return;
					}
				}else if(mTempMode == 1){
					if(mRArtTarPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_right_kidney_artery_pre);
						return;
					}
				}
				 
			}			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, mRArtTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_RIGHT_KIDNEY_WARN_MAX, mRArtTarPre);
		}else if(mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			//右肾恒流
			String mConstVeinflow = mRArtConstTv.getText().toString().trim();
			mRArtTarFlow = StringUtil.convertToFloat(mConstVeinflow, 0.0f);
			//默认是ml
			if(StringUtil.isEmpty(mConstVeinflow)|| mConstVeinflow.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_right_kidney_flow_null);
				return;
			}else {
				if(mRArtTarFlow > 1500.0f){
					displayToast(R.string.error_max_value_right_kidney_target_flow);
					return;
				}
			}
			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, mRArtTarFlow);
		}else if(mVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
			//右肾动脉搏动模式
			String mRMinArtPre = mRArtMinTv.getText().toString().trim();
			String mRMaxArtPre = mRArtMaxTv.getText().toString().trim();
			mRArtMinTarPre = StringUtil.convertToFloat(mRMinArtPre, 0.0f);
			mRArtMaxTarPre = StringUtil.convertToFloat(mRMaxArtPre, 0.0f);
			if(StringUtil.isEmpty(mRMinArtPre)||mRMinArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_right_kidney_pre_min_null);
				return;
			}else if(StringUtil.isEmpty(mRMaxArtPre)||mRMaxArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_right_kidney_pre_max_null);
				return;
			}else if(mRArtMaxTarPre > 120.0f){
				displayToast(R.string.error_target_right_kidney_pre_max_limit);
				return;
			}else if(mRArtMinTarPre >= 100.0f){
				displayToast(R.string.error_target_right_kidney_pre_min_limit);
				return;
			}else if(compare(mRArtMinTarPre, mRArtMaxTarPre)){
				displayToast(R.string.error_target_right_kidney_pre_min_max_compare);
				return;
			}else if(compareArtPulsePreDiff(mRArtMinTarPre, mRArtMaxTarPre)){
				displayToast(R.string.error_target_right_kidney_pre_min_max_difference);
				return;
			}

			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, mRArtMinTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, mRArtMaxTarPre);
		}
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, mVeinMode);
		
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.TARGET_TEMP, target_Temp);
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
			.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
		if(null != mOnPresetPatternListener){
			if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
				//左肾恒压，右肾恒压
				mOnPresetPatternListener.LAConstPreRAConstPre(mLArtTarPre,mRArtTarPre);			
			}else if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
				//左肾恒压，右肾恒流
				mOnPresetPatternListener.LAConstPreRAConstFlow(mLArtTarPre,mRArtTarFlow);			
			}else if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
				//左肾恒压，右肾搏动
				mOnPresetPatternListener.LAConstPreRAPulsePre(mLArtTarPre,mRArtMinTarPre,mRArtMaxTarPre);			
			}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
				//左肾搏动，右肾恒压
				mOnPresetPatternListener.LArtMinMaxPreRAConstPre(mLArtMinTarPre,mLArtMaxTarPre,mRArtTarPre);
			}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
				//左肾搏动，右肾恒流
				mOnPresetPatternListener.LArtMinMaxPreRAConstFlow(mLArtMinTarPre,mLArtMaxTarPre,mRArtTarFlow);				
			}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
				//左肾搏动，右肾搏动
				mOnPresetPatternListener.LArtMinMaxPreRArtMinMaxPre(mLArtMinTarPre,mLArtMaxTarPre,mRArtMinTarPre,mRArtMaxTarPre);				
			}else if(mArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE && mVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
				//左肾恒流，右肾搏动
				mOnPresetPatternListener.LAConstFlowRAPulsePre(mLArtTarFlow,mRArtMinTarPre,mRArtMaxTarPre);				
			}else if(mArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
				//左肾恒流，右肾恒压
				mOnPresetPatternListener.LAConstFlowRAConstPre(mLArtTarFlow,mRArtTarPre);				
			}else if(mArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
				//左肾恒流，右肾搏动
				mOnPresetPatternListener.LAConstFlowRAConstFlow(mLArtTarFlow,mRArtTarFlow);				
			}			

		}
		
	}

	
	
	private boolean compare(float minValue, float maxValue){
		if(minValue > maxValue){
			return 	true;
		}
		return false;
	}
	
	private boolean compareArtPulsePreDiff(float minValue, float maxValue){
		if((maxValue - minValue) > 40){
			return 	true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pattern_setting_complete_btn:
			//首先判断温度是否为空
			savePerfusionModeSetting();			
			break;
		case R.id.pattern_setting_art_temp_et:
			//自定义被点击，则其余的选中状态清零
			setSelectedView(mCustomTempEt);
			mTempSetRg.clearCheck();
			break;
		case R.id.pattern_left_kidney_art_max_tarpressure_et:
			setSelectedView(mLArtMaxPEt);
			mLArtMaxSetPreRg.clearCheck();
			break;	
		case R.id.pattern_left_kidney_art_min_tarpressure_et:
			setSelectedView(mLArtMinPEt);
			mLArtMinSetPreRg.clearCheck();
			break;			
		case R.id.pattern_setting_art_const_tarpressure_et:
			setSelectedView(mLArtConstPEt);
			mLArtConstSetPreRg.clearCheck();
			break;
		case R.id.pattern_setting_vein_target_pressure_et:
			setSelectedView(mRArtConstPEt);
			mRAConstSetPreRg.clearCheck();
			break;
		case R.id.pattern_setting_vein_target_flow_et:
			setSelectedView(mRArtConstFEt);
			mRAConstSetFlowRg.clearCheck();
			break;
		default:
			break;
		}
	}



}
