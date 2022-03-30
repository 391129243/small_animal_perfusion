package com.gidi.bio_console.fragment.preset;


import android.text.Editable;
import android.util.Log;
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
import com.gidi.bio_console.view.CustomImageButton;
import com.gidi.bio_console.view.CustomTempModeButton;
import com.gidi.bio_console.view.CustomTempModeButton.OnTempModeButtonClickListener;
import com.gidi.bio_console.view.SystemKeyBoardEditText;
import com.gidi.bio_console.view.CustomImageButton.OnImageButtonClickListener;

public class PresetPatternFragment extends BaseFragment implements OnClickListener{
	private static final String TAG = "PresetPatternFragment";
	private ImageView mNextBtn;
	private CustomTempModeButton mHMP_ModeBtn;//冷灌注
	private CustomTempModeButton mNMP_ModeBtn;//常温灌注
	/**肝动脉搏动**/
	private CustomImageButton mAPulsePImg;
	/**肝动脉恒压**/
	private CustomImageButton mAConstPImg;
	
	/**门静脉恒压**/
	private CustomImageButton mVConstPImg;
	/**门静脉恒流**/
	private CustomImageButton mVConstFImg;
	private RadioButton mHMPOneTempRb,mHMPTwoTempRb, mNMPOneTempRb, mNMPTwoTempRb;
	private SystemKeyBoardEditText mVeinConstFEt;
	private SystemKeyBoardEditText mVeinConstPEt;
	private SystemKeyBoardEditText mArtMinPEt;
	private SystemKeyBoardEditText mArtMaxPEt;
	private SystemKeyBoardEditText mArtConstPEt;
	private SystemKeyBoardEditText mCustomTempEt;
	private RadioGroup mTempSetRg;
	private RadioGroup mVeinSetPreRg;
	private RadioGroup mVeinSetFlowRg;
	
	private RadioGroup mArtMaxSetPreRg;
	private RadioGroup mArtMinSetPreRg;
	private RadioGroup mArtConstSetPreRg;
	private TextView mTempTxt;
	private TextView mVeinTarTxt;//显示门静脉目标值
	private TextView mArtMaxTxt;
	private TextView mArtMinTxt;
	private TextView dividerTxt;
	private TextView mArtConstTxt;//恒压
	private TextView veinModeTitle;
	private LinearLayout mVeinPreLayout;
	private LinearLayout mVeinFlowLayout;
	private RelativeLayout mArtConstLayout;
	private RelativeLayout mArtPulseLayout;
	private float mArtMinTarPre;
	private float mArtMaxTarPre;
	private float mArtTarPre;
	private float mVeinTarPre;
	private float mVeinTarFlow;
	/**0: normal 1: cold**/
	private int mTempMode = 0;
	/**默认肝动脉是搏动门静脉是恒压**/
	private int mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;
	private int mVeinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;


	private onPresetPatternListener mOnPresetPatternListener;
	
	public interface onPresetPatternListener{
		void ArtPVeinP(float mArtPreTarget, float mVeinPreTarget);
		void ArtPVeinF(float mArtPreTarget,float mVeinFlowTarget);
		void ArtMinMaxPVeinP(float mArtMinPreTarget,float ArtMaxPreTarget, float mVeinPreTarget);
		void ArtMinMaxPVeinF(float mArtMinPreTarget,float ArtMaxPreTarget, float mVeinFlowTarget);
		
	}
	
	public void setOnPresetPatternListener(onPresetPatternListener listener){
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
		return R.layout.module_fragment_preset_pattern;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mNextBtn = (ImageView)rootView.findViewById(R.id.pattern_setting_complete_btn);
		mAConstPImg = (CustomImageButton)rootView.findViewById(R.id.pattern_setting_artery_const_pressure_img);
		mAPulsePImg = (CustomImageButton)rootView.findViewById(R.id.pattern_setting_artery_pulse_pressure_img);
		mVConstPImg = (CustomImageButton)rootView.findViewById(R.id.pattern_setting_vein_const_pressure_img);
		mVConstFImg = (CustomImageButton)rootView.findViewById(R.id.pattern_setting_vein_const_flow_img);		
		mHMP_ModeBtn = (CustomTempModeButton)rootView.findViewById(R.id.pattern_setting_temp_mode_cold_btn);
		mNMP_ModeBtn = (CustomTempModeButton)rootView.findViewById(R.id.pattern_setting_temp_mode_normal_btn);	
		
		dividerTxt = (TextView)rootView.findViewById(R.id.pre_setting_art_divider_txt);
		mArtMinTxt = (TextView)rootView.findViewById(R.id.pre_setting_art_min_txt);
		mArtMaxTxt = (TextView)rootView.findViewById(R.id.pre_setting_art_max_txt);
		mArtConstTxt = (TextView)rootView.findViewById(R.id.pre_setting_art_const_txt);
		mArtMinPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_art_min_tarpressure_et);
		mArtMaxPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_art_max_tarpressure_et);
		mArtConstPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_art_const_tarpressure_et);
		mArtPulseLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_setting_artery_pulse_pressure_layout);
		mArtConstLayout = (RelativeLayout)rootView.findViewById(R.id.pattern_setting_artery_const_pressure_layout);
		mArtMaxSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_art_pulse_max_pre_radiogroup);
		mArtMinSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_art_pulse_min_pre_radiogroup);
		mArtConstSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_art_pulse_const_pre_radiogroup);
		
		
		veinModeTitle = (TextView)rootView.findViewById(R.id.pattern_setting_vein_title);
		mVeinTarTxt = (TextView)rootView.findViewById(R.id.pre_setting_vein_target_txt);
		mVeinSetPreRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_vein_pre_radiogroup);
		mVeinSetFlowRg = (RadioGroup)rootView.findViewById(R.id.pattern_set_vein_flow_radiogroup);
		mVeinConstFEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_vein_target_flow_et);
		mVeinConstPEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.pattern_setting_vein_target_pressure_et);
		mVeinPreLayout = (LinearLayout)rootView.findViewById(R.id.pattern_setting_vein_pressure_layout);
		mVeinFlowLayout = (LinearLayout)rootView.findViewById(R.id.pattern_setting_vein_flow_layout);
		
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
		
		mAConstPImg.setOnImageButtonClickListener(new OnImageButtonClickListener() {
			
			@Override
			public void CustomImageButtonClicked(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "artery_pressure");				
				mArtMode = Constants.LEFT_ARTERY_CONST_PRESSURE_MODE;
				mArtPulseLayout.setVisibility(View.GONE);
				mArtConstLayout.setVisibility(View.VISIBLE);
				mArtMaxTxt.setVisibility(View.GONE);
				mArtMinTxt.setVisibility(View.GONE);
				dividerTxt.setVisibility(View.GONE);
				mArtConstTxt.setVisibility(View.VISIBLE);
				mAConstPImg.setImageBtnResoure(R.drawable.artery_perfusion_const_mode_pressed,R.color.white);
				mAConstPImg.setBackgroudResId(R.drawable.module_preset_artery_pattern_pressed_bg);
				mAPulsePImg.setImageBtnResoure(R.drawable.artery_perfusion_pulse_mode_normal,R.color.color_preset_artery_mode_normal);
				mAPulsePImg.setBackgroudResId(0);
			}
		});
		mAPulsePImg.setOnImageButtonClickListener(new OnImageButtonClickListener() {
			
			@Override
			public void CustomImageButtonClicked(View v) {
				// TODO Auto-generated method stub
				mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;
				mArtPulseLayout.setVisibility(View.VISIBLE);
				mArtMaxTxt.setVisibility(View.VISIBLE);
				mArtMinTxt.setVisibility(View.VISIBLE);
				dividerTxt.setVisibility(View.VISIBLE);
				mArtConstLayout.setVisibility(View.GONE);
				mArtConstTxt.setVisibility(View.GONE);
				mAConstPImg.setImageBtnResoure(R.drawable.artery_perfusion_const_mode_normal,R.color.color_preset_artery_mode_normal);
				mAPulsePImg.setImageBtnResoure(R.drawable.artery_perfusion_pulse_mode_pressed,R.color.white);
				mAPulsePImg.setBackgroudResId(R.drawable.module_preset_artery_pattern_pressed_bg);
				mAConstPImg.setBackgroudResId(0);
				mAPulsePImg.setTextColor(R.color.white);
				
			}
		});
		
		mArtMaxSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_set_art_pulse_pre_one_rb:
				case R.id.pattern_set_art_pulse_pre_two_rb:
				case R.id.pattern_set_art_pulse_pre_three_rb:
				case R.id.pattern_set_art_pulse_pre_third_rb:
					setNormalView(mArtMaxPEt);
					String maxPre = radioButton.getText().toString();					
					mArtMaxTxt.setText(maxPre);
					break;
				default:
					break;
				}
			}
		});
		
		mArtMinSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_set_art_pulse_min_pre_one_rb:
				case R.id.pattern_set_art_pulse_min_pre_two_rb:
				case R.id.pattern_set_art_pulse_min_pre_three_rb:
				case R.id.pattern_set_art_pulse_min_pre_third_rb:
					setNormalView(mArtMinPEt);
					String minPre = radioButton.getText().toString();					
					mArtMinTxt.setText(minPre);
					break;
				default:
					break;
				}
			}
		});
		
		mArtConstSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int isCheck) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_set_art_const_pre_one_rb:
				case R.id.pattern_set_art_const_pre_two_rb:
				case R.id.pattern_set_art_const_pre_three_rb:
				case R.id.pattern_set_art_const_pre_third_rb:
					setNormalView(mArtConstPEt);
					String constPre = radioButton.getText().toString();					
					mArtConstTxt.setText(constPre);
					break;
				default:
					break;
				}
			}
		});
		
		mVeinSetPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				
				switch (id) {				
				case R.id.pattern_set_vein_pre_one_rb:
				case R.id.pattern_set_vein_pre_two_rb:
				case R.id.pattern_set_vein_pre_three_rb:
					String vein_const_Pre_1 = radioButton.getText().toString();
					setNormalView(mVeinConstPEt);				
					mVeinTarTxt.setText(vein_const_Pre_1);
					break;
				case R.id.pattern_set_vein_pre_third_rb:
					if(mTempMode == 1){
						//冷灌注
						displayToast(R.string.error_max_value_cold_perfusion_vein_target_pre);
						return;
					}else{
						String vein_const_Pre_2 = radioButton.getText().toString();
						setNormalView(mVeinConstPEt);											
						mVeinTarTxt.setText(vein_const_Pre_2);
					}
					break;
				default:
					break;
				}
			}
		});
		
		mVeinSetFlowRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkid) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.pattern_set_vein_flow_one_rb:
				case R.id.pattern_set_vein_flow_two_rb:
				case R.id.pattern_set_vein_flow_three_rb:
				case R.id.pattern_set_vein_flow_third_rb:
					setNormalView(mVeinConstFEt);
					String veinFlow = radioButton.getText().toString();					
					mVeinTarTxt.setText(veinFlow);
					break;
				default:
					break;
				}
			}
		});
		
		//门静脉恒流
		mVConstFImg.setOnImageButtonClickListener(new OnImageButtonClickListener() {
			
			@Override
			public void CustomImageButtonClicked(View v) {
				// TODO Auto-generated method stub
				mVeinMode = Constants.RIGHT_ARTERY_CONST_FLOW_MODE;
				mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				veinModeTitle.setText(R.string.portal_vein_target_flow);
				mVeinFlowLayout.setVisibility(View.VISIBLE);
				mVeinPreLayout.setVisibility(View.GONE);				
				mVConstFImg.setImageBtnResoure(R.drawable.vein_perfusion_const_flow_mode_pressed, R.color.white);
				mVConstFImg.setBackgroudResId(R.drawable.module_preset_vein_pattern_pressed_bg);
				mVConstPImg.setImageBtnResoure(R.drawable.vein_perfusion_const_mode_normal,R.color.color_preset_vein_mode_normal);
				mVConstPImg.setBackgroudResId(0);
			}
		});
		
		/**门静脉恒压**/
		mVConstPImg.setOnImageButtonClickListener(new OnImageButtonClickListener() {
			
			@Override
			public void CustomImageButtonClicked(View v) {
				// TODO Auto-generated method stub
				
				mVeinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;
				mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				veinModeTitle.setText(R.string.portal_vein_target_pressure);
				mVeinFlowLayout.setVisibility(View.GONE);
				mVeinPreLayout.setVisibility(View.VISIBLE);
				mVConstPImg.setImageBtnResoure(R.drawable.vein_perfusion_const_mode_pressed, R.color.white);
				mVConstPImg.setBackgroudResId(R.drawable.module_preset_vein_pattern_pressed_bg);
				mVConstFImg.setImageBtnResoure(R.drawable.vein_perfusion_const_flow_mode_normal,R.color.color_preset_vein_mode_normal);
				mVConstFImg.setBackgroudResId(0);
			}
		});
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
		mVeinConstFEt.setOnClickListener(this);
		mVeinConstFEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strveinConstFlow = editable.toString();
				float veinConstFlow = StringUtil.convertToFloat(strveinConstFlow, 0.0f);
				if(veinConstFlow > 1500.0f){
					displayToast(R.string.error_max_value_vein_target_flow);
					mVeinTarTxt.setText(1500 +"");
					return;
				}else{
					mVeinTarTxt.setText(editable);
				}

			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_flow = mVeinConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					
					mVeinTarTxt.setText(vein_const_flow);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mVeinTarTxt.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_flow = mVeinConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mVeinTarTxt.setText(vein_const_flow);
				}
			}
		});
		//门静脉恒压
		mVeinConstPEt.setOnClickListener(this);
		mVeinConstPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strveinConstPre = editable.toString();
				float veinConstPre = StringUtil.convertToFloat(strveinConstPre, 0.0f);
				if(mTempMode == 0){//常温灌注不高于14mmHg，低温灌注不高于8mmHg
					if(veinConstPre > 14.0f){
						displayToast(R.string.error_max_normal_target_pre_vein_max_tip);
						mVeinTarTxt.setText(14+"");
						return;
					}else{
						mVeinTarTxt.setText(editable);
					}
				}else if(mTempMode == 1){
					if(veinConstPre > 8.0f){
						displayToast(R.string.error_max_value_cold_perfusion_vein_target_pre);
						mVeinTarTxt.setText(8+"");
						return;
					}else{
						mVeinTarTxt.setText(editable);
					}
				}
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mVeinConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mVeinTarTxt.setText(vein_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mVeinConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mVeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mVeinTarTxt.setText(vein_const_pressure);
				}
			}
		});
		
		mArtMinPEt.setOnClickListener(this);
		mArtMinPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 100.0f){
					displayToast(R.string.error_target_pre_one_min_limit);
					mArtMinTxt.setText(100 + "");
				}else{
					mArtMinTxt.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_min_pressure = mArtMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mArtMinTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtMinTxt.setText(art_min_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_min_pressure = mArtMinPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mArtMinTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtMinTxt.setText(art_min_pressure);
				}
			}
		});
		
		mArtMaxPEt.setOnClickListener(this);
		mArtMaxPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 120.0f){
					displayToast(R.string.error_target_pre_one_max_limit);
					mArtMaxTxt.setText(120 + "");
				}else{
					mArtMaxTxt.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_max_pressure = mArtMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mArtMaxTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtMaxTxt.setText(art_max_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_max_pressure = mArtMaxPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mArtMaxTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtMaxTxt.setText(art_max_pressure);
				}
			}
		});
		
		mArtConstPEt.setOnClickListener(this);
		mArtConstPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float constTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);
				if(mTempMode == 0){
					if(constTarPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_artery_pre);
						mArtConstTxt.setText(100 + "");
						return;
					}else{
						mArtConstTxt.setText(editable);
					}
				}else if(mTempMode == 1){
					if(constTarPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_artery_pre);
						mArtConstTxt.setText(40 + "");
						return;
					}else{
						mArtConstTxt.setText(editable);
					}
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_const_pressure = mArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mArtConstTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtConstTxt.setText(art_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_const_pressure = mArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mArtConstTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mArtConstTxt.setText(art_const_pressure);
				}
			}
		});
		mNextBtn.setOnClickListener(this);		
	}

	private void initStates(){
		mVConstPImg.setImageBtnResoure(R.drawable.vein_perfusion_const_mode_pressed,R.color.white);
		mAPulsePImg.setImageBtnResoure(R.drawable.artery_perfusion_pulse_mode_pressed, R.color.white);
		mAPulsePImg.setBackgroudResId(R.drawable.module_preset_artery_pattern_pressed_bg);
		mVConstPImg.setBackgroudResId(R.drawable.module_preset_vein_pattern_pressed_bg);
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
			//肝动脉恒压的情况
			String mConstArtPre = mArtConstTxt.getText().toString().trim();
			mArtTarPre = StringUtil.convertToFloat(mConstArtPre, 0.0f);
			if(StringUtil.isEmpty(mConstArtPre)|| mConstArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_pre_one_null);
				return;
			}else if(mConstArtPre.endsWith(".")){
				displayToast(R.string.error_target_art_pre_format_error);
				return;
			}else{
				/**0: normal 1: cold**/
				if(mTempMode == 0){
					if(mArtTarPre > 100.0f){
						displayToast(R.string.error_max_value_normal_perfusion_target_const_artery_pre);
						return;
					}
				}else if(mTempMode == 1){
					if(mArtTarPre > 40.0f){
						displayToast(R.string.error_max_value_cold_perfusion_target_const_artery_pre);
						return;
					}
				}
			}
			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, mArtTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_MAX_ONE, mArtTarPre);
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			String mMinArtPre = mArtMinTxt.getText().toString().trim();
			String mMaxArtPre = mArtMaxTxt.getText().toString().trim();
			mArtMinTarPre = StringUtil.convertToFloat(mMinArtPre, 0.0f);
			mArtMaxTarPre = StringUtil.convertToFloat(mMaxArtPre, 0.0f);
			if(StringUtil.isEmpty(mMinArtPre)||mMinArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_pre_one_min_null);
				return;
			}else if(StringUtil.isEmpty(mMaxArtPre)||mMaxArtPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_pre_one_max_null);
				return;
			}else if(mArtMaxTarPre > 120.0f){
				displayToast(R.string.error_target_pre_one_max_limit);
				return;
			}else if(mArtMinTarPre >= 100.0f){
				displayToast(R.string.error_target_pre_one_min_limit);
				return;
			}else if(compare(mArtMinTarPre, mArtMaxTarPre)){
				displayToast(R.string.error_target_pre_one_min_max_compare);
				return;
			}else if(compareArtPulsePreDiff(mArtMinTarPre, mArtMaxTarPre)){
				displayToast(R.string.error_target_pre_one_min_max_difference);
				return;
			}

			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, mArtMinTarPre);
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, mArtMaxTarPre);
		}

		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.ARTERY_PERFUSION_MODE, mArtMode);
		if(mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			String mConstVeinPre = mVeinTarTxt.getText().toString().trim();
			mVeinTarPre = StringUtil.convertToFloat(mConstVeinPre, 0.0f);
			if(StringUtil.isEmpty(mConstVeinPre)|| mConstVeinPre.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_pre_two_null);
				return;
			}else if(mConstVeinPre.endsWith(".")){
				displayToast(R.string.error_target_vein_pre_format_error);
			}else{
				
				if(mTempMode == 0){
					if(compareNum(mConstVeinPre)){
						displayToast(R.string.error_max_normal_target_pre_vein_max_tip);
						return;
					}
				}else if(mTempMode == 1){
					if(mVeinTarPre > 8.0f){
						displayToast(R.string.error_max_value_cold_perfusion_vein_target_pre);
						return;
					}
				}
				 
			}			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, mVeinTarPre);
		}else if(mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			String mConstVeinflow = mVeinTarTxt.getText().toString().trim();
			mVeinTarFlow = StringUtil.convertToFloat(mConstVeinflow, 0.0f);
			//默认是ml
			if(StringUtil.isEmpty(mConstVeinflow)|| mConstVeinflow.equals(getResources().getString(R.string.string_null))){
				displayToast(R.string.error_target_flow_two_null);
				return;
			}else {
				if(mVeinTarFlow > 1500.0f){
					displayToast(R.string.error_max_value_vein_target_flow);
					return;
				}
			}
			
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, mVeinTarFlow);
		}
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.VEIN_PERFUSION_MODE, mVeinMode);
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.TARGET_TEMP, target_Temp);
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
			.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, mTempMode);
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			if(null != mOnPresetPatternListener){
				mOnPresetPatternListener.ArtPVeinP(mArtTarPre,mVeinTarPre);
			}
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			if(null != mOnPresetPatternListener){
				mOnPresetPatternListener.ArtMinMaxPVeinP(mArtMinTarPre,mArtMaxTarPre,mVeinTarPre);
			}
		}else if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			if(null != mOnPresetPatternListener){
				mOnPresetPatternListener.ArtPVeinF(mArtTarPre,mVeinTarFlow);
			}
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE && mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			if(null != mOnPresetPatternListener){
				mOnPresetPatternListener.ArtMinMaxPVeinF(mArtMinTarPre,mArtMaxTarPre,mVeinTarFlow);
			}
		}		

	}

	
	private boolean compareNum(String input){
		float value = StringUtil.convertToFloat(input, 0.0f);
		if(value > 14){
			return 	true;
		}
		return false;
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
		case R.id.pattern_setting_art_max_tarpressure_et:
			setSelectedView(mArtMaxPEt);
			mArtMaxSetPreRg.clearCheck();
			break;	
		case R.id.pattern_setting_art_min_tarpressure_et:
			setSelectedView(mArtMinPEt);
			mArtMinSetPreRg.clearCheck();
			break;			
		case R.id.pattern_setting_art_const_tarpressure_et:
			setSelectedView(mArtConstPEt);
			mArtConstSetPreRg.clearCheck();
			break;
		case R.id.pattern_setting_vein_target_pressure_et:
			setSelectedView(mVeinConstPEt);
			mVeinSetPreRg.clearCheck();
			break;
		case R.id.pattern_setting_vein_target_flow_et:
			setSelectedView(mVeinConstFEt);
			mVeinSetFlowRg.clearCheck();
			break;
		default:
			break;
		}
	}



}
