package com.gidi.bio_console.fragment.setting;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.utils.ToastUtils;
import com.gidi.bio_console.view.SetAlertEditText;
import com.gidi.bio_console.view.SetAlertEditText.onNumChangeListener;

public class SettingAlertFragment extends BaseFragment implements onNumChangeListener {

	private SetAlertEditText mArtMinPreEdit;
	private SetAlertEditText mArtMaxPreEdit;
	private SetAlertEditText mArtMinFlowEdit;
	private SetAlertEditText mArtMaxFlowEdit;
	private SetAlertEditText mVeinMinPreEdit;
	private SetAlertEditText mVeinMaxPreEdit;
	private SetAlertEditText mVeinMinFlowEdit;
	private SetAlertEditText mVeinMaxFlowEdit;
	private SetAlertEditText mArtLimitSpeedEdit;
	private SetAlertEditText mVeinLimitSpeedEdit;
	
	private Button mSaveBtn;
	private float mMinArtPre;
	private float mMaxArtPre;
	private float mMinArtFlow;
	private float mMaxArtFlow;
	private float mMinVeinPre;
	private float mMaxVeinPre;
	private float mMinVeinFlow;
	private float mMaxVeinFlow;
	private int mArtLimitSpeed;
	private int mVeinLimitSpeed;
	private boolean isFirstVisible = true;
	private boolean isArtSpeedSet = false;
	private boolean isVeinSpeedSet = false;
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private onSettingAlertListener onSettingAlertListener;
	public interface onSettingAlertListener{
		void onFinishSetLimitSpeed(boolean isArtSet, boolean isVeinSet);
		void onFinishSaveAlert();

	}
	
	public  void setOnSettingAlertListener(onSettingAlertListener listener){
		this.onSettingAlertListener = listener;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){
			
			initVariables();
		}else{
			if(isFirstVisible){
				isFirstVisible = false;
			}else{

				if(isArtSpeedSet || isVeinSpeedSet){
					if(null != onSettingAlertListener){
						onSettingAlertListener.onFinishSetLimitSpeed(isArtSpeedSet, isVeinSpeedSet);
					}
				}
				
				isArtSpeedSet = false;
				isVeinSpeedSet  = false;
			}
		}
	}

	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != onSettingAlertListener){
			onSettingAlertListener = null;
		}
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_setting_alert;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mArtMinPreEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_artery_pressure_edit);
		mArtMaxPreEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_max_artery_pressure_edit);
		mArtMinFlowEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_artery_flow_edit);
		mArtMaxFlowEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_max_artery_flow_edit);
		mVeinMinPreEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_vein_pressure_edit);
		mVeinMaxPreEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_max_vein_pressure_edit);
		mVeinMinFlowEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_vein_flow_edit);
		mVeinMaxFlowEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_max_vein_flow_edit);
		mArtLimitSpeedEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_artery_speed_edit);
		mVeinLimitSpeedEdit = (SetAlertEditText)rootView.findViewById(R.id.setting_alert_min_vein_speed_edit);		
		mSaveBtn = (Button)rootView.findViewById(R.id.setting_alert_save_btn);
		rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
                InputMethodManager manager = (InputMethodManager) getActivity()
                		.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                    if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus().getWindowToken()!=null){  
                      manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
                    }  
                 }  
               return false;
			}
		});
	}

	private void initVariables(){
		Log.i("SettingAlertFragment", "initVariables---");
		mMinArtPre = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext()).getFloatValue(SharedConstants.PRESSURE_MIN_ONE, 0.0f);
		mMaxArtPre = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext()).getFloatValue(SharedConstants.PRESSURE_MAX_ONE ,(100.0f));
		mMinArtFlow = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext()).getFloatValue(SharedConstants.FLOW_MIN_ONE, (0.0f));
		mMaxArtFlow = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext()).getFloatValue(SharedConstants.FLOW_MAX_ONE, (1000.0f));
		
		mMinVeinPre = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MIN_TWO, 0.0f);
		mMaxVeinPre = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MAX_TWO, (14.0f));
		mMinVeinFlow = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MIN_TWO, (0.0f));		
		mMaxVeinFlow = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MAX_TWO, (2000.0f));
		mArtLimitSpeed = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getIntValue(SharedConstants.SPEED_LEFT_KIDNEY_LIMIT, 1500);
		mVeinLimitSpeed = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getIntValue(SharedConstants.SPEED_RIGHT_KIDNEY_LIMIT, 1500);
		
		mArtMinPreEdit.setEditText(String.valueOf(mMinArtPre));
		mArtMaxPreEdit.setEditText(String.valueOf(mMaxArtPre));
		mVeinMinPreEdit.setEditText(String.valueOf(mMinVeinPre));
		mVeinMaxPreEdit.setEditText(String.valueOf(mMaxVeinPre));
		
		mArtMinFlowEdit.setEditText(String.valueOf(mMinArtFlow));
		mArtMaxFlowEdit.setEditText(String.valueOf(mMaxArtFlow));
		mVeinMinFlowEdit.setEditText(String.valueOf(mMinVeinFlow));
		mVeinMaxFlowEdit.setEditText(String.valueOf(mMaxVeinFlow));
		
		mArtLimitSpeedEdit.setEditText(String.valueOf(mArtLimitSpeed));
		mVeinLimitSpeedEdit.setEditText(String.valueOf(mVeinLimitSpeed));
		
	}
	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mArtMinPreEdit.setContentMode(SetAlertEditText.MODE_FIRST_ARTERY_PRESSURE);
		mArtMaxPreEdit.setContentMode(SetAlertEditText.MODE_FIRST_ARTERY_PRESSURE);
		mArtMinFlowEdit.setContentMode(SetAlertEditText.MODE_THIRD_ARTERY_FLOW);
		mArtMaxFlowEdit.setContentMode(SetAlertEditText.MODE_THIRD_ARTERY_FLOW);
		mArtLimitSpeedEdit.setContentMode(SetAlertEditText.MODE_ARTERY_SPEED);
		
		mVeinMinPreEdit.setContentMode(SetAlertEditText.MODE_SECOND_VEIN_PRESSURE);
		mVeinMaxPreEdit.setContentMode(SetAlertEditText.MODE_SECOND_VEIN_PRESSURE);
		mVeinMinFlowEdit.setContentMode(SetAlertEditText.MODE_FORTH_VEIN_FLOW);
		mVeinMaxFlowEdit.setContentMode(SetAlertEditText.MODE_FORTH_VEIN_FLOW);
		mVeinLimitSpeedEdit.setContentMode(SetAlertEditText.MODE_VEIN_SPEED);
		mArtMinPreEdit.setOnNumChangeListener(this);
		mArtMaxPreEdit.setOnNumChangeListener(this);
		mArtMinFlowEdit.setOnNumChangeListener(this);
		mArtMaxFlowEdit.setOnNumChangeListener(this);
		mVeinMinPreEdit.setOnNumChangeListener(this);
		mVeinMaxPreEdit.setOnNumChangeListener(this);
		mVeinMinFlowEdit.setOnNumChangeListener(this);		
		mVeinMaxFlowEdit.setOnNumChangeListener(this);		
		mArtLimitSpeedEdit.setOnNumChangeListener(this);		
		mVeinLimitSpeedEdit.setOnNumChangeListener(this);
				
		mSaveBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				saveAlertParams();
			}
		});
	}
	
	
	private void saveAlertParams(){

		float minArtPre = StringUtil.convertToFloat(mArtMinPreEdit.getEditText(), 0.0f);
		float maxArtPre = StringUtil.convertToFloat(mArtMaxPreEdit.getEditText(), 0.0f);
		float minArtFlow = StringUtil.convertToFloat(mArtMinFlowEdit.getEditText(), 0.0f);
		float maxArtFlow = StringUtil.convertToFloat(mArtMaxFlowEdit.getEditText(), 1000.0f);
		
		float minVeinPre = StringUtil.convertToFloat(mVeinMinPreEdit.getEditText(), 0.0f);
		float maxVeinPre = StringUtil.convertToFloat(mVeinMaxPreEdit.getEditText(), 0.0f);
		float minVeinFlow = StringUtil.convertToFloat(mVeinMinFlowEdit.getEditText(), 0.0f);
		float maxVeinFlow = StringUtil.convertToFloat(mVeinMaxFlowEdit.getEditText(), 2000.0f);
		
		int artSpeedLimit = StringUtil.convertToInt(mArtLimitSpeedEdit.getEditText(), 1500);
		int veinSpeedLimit = StringUtil.convertToInt(mVeinLimitSpeedEdit.getEditText(), 1200);
		
		if(minArtPre > maxArtPre){
			displayErrorMsg(R.string.error_pre_one_max_less_than_min);
			return;
		}else if(minVeinPre > maxVeinPre){
			displayErrorMsg(R.string.error_pre_two_max_less_than_min);
			return;
			
		}else if(minArtFlow > maxArtFlow){
			displayErrorMsg(R.string.error_flow_one_max_less_than_min);
			return;
		}else if(minVeinFlow > maxVeinFlow){
			displayErrorMsg(R.string.error_flow_two_max_less_than_min);
			return;
		}else if(maxArtPre > 100.f){
			displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_pressure);
			return;
		}else if(maxVeinPre > 14.f){
			displayErrorMsg(R.string.error_upper_limit_of_the_vein_pressure_nmp);
			return;
		}else if(maxArtFlow > 1000.0f){
			displayErrorMsg(R.string.error_upper_limit_of_the_hepatic_artery_flow);
			return;
		}else if(maxVeinFlow > 2000.0f){
			displayErrorMsg(R.string.error_upper_limit_of_the_vein_flow);
			return;
		}else if(artSpeedLimit > 3000){
			displayErrorMsg(R.string.error_upper_limit_of_the_artery_speed);			
		}else if(veinSpeedLimit > 3000){
			displayErrorMsg(R.string.error_upper_limit_of_the_vein_speed);			
		}else if(veinSpeedLimit < 1200){
			displayErrorMsg(R.string.error_lower_limit_of_the_vein_speed);			
		}else if(artSpeedLimit < 1500){			
			displayErrorMsg(R.string.error_lower_limit_of_the_artery_speed);
		}
		
		if(minArtPre != mMinArtPre){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_MIN_ONE, minArtPre);
		}
		
		if(maxArtPre != mMaxArtPre){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_MAX_ONE, maxArtPre);
		}
		
		if(minArtFlow != mMinArtFlow){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.FLOW_MIN_ONE, minArtFlow);
		}
		
		if(maxArtFlow != mMaxArtFlow){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.FLOW_MAX_ONE, maxArtFlow);
		}
		//vein
		if(minVeinPre != mMinVeinPre){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_MIN_TWO, minVeinPre);
		}
		
		if(maxVeinPre != mMaxVeinPre){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.PRESSURE_MAX_TWO, maxVeinPre);
		}
		
		if(minVeinFlow != mMinVeinFlow){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.FLOW_MIN_TWO, minVeinFlow);
		}
		
		if(maxVeinFlow != mMaxVeinFlow){
			PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.setValueByName(SharedConstants.FLOW_MAX_TWO, maxVeinFlow);
		}
		
		if(artSpeedLimit != mArtLimitSpeed){
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.SPEED_LEFT_KIDNEY_LIMIT, artSpeedLimit);			
		}
		
		if(artSpeedLimit > mArtLimitSpeed){
			isArtSpeedSet = true;
		}
		

		if(veinSpeedLimit != mVeinLimitSpeed){
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.LIMIT_SPEED_TWO, veinSpeedLimit);
		}
		
		if(veinSpeedLimit > mVeinLimitSpeed){
			isVeinSpeedSet = true;
		}
		ToastUtils.showToast(getActivity().getApplicationContext(), 
				getActivity().getResources().getString(R.string.set_save_sucess)
				, R.drawable.preset_btn_zero_enable);
		if(null != onSettingAlertListener){
			onSettingAlertListener.onFinishSaveAlert();
		}
		
	}

	@Override
	public void displayErrorMsg(int resId) {
		// TODO Auto-generated method stub
		displayToast(resId);
	}

}
