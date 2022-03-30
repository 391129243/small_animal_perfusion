package com.gidi.bio_console.fragment;

import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.fragment.ctrl.SettingFragmentController;
import com.gidi.bio_console.fragment.setting.SettingAlertFragment;
import com.gidi.bio_console.fragment.setting.SettingAlertFragment.onSettingAlertListener;
import com.gidi.bio_console.fragment.setting.SettingPerfusionFragment;
import com.gidi.bio_console.fragment.setting.SettingPerfusionFragment.onSettingPerfusionListener;

public class SettingFragment extends BaseFragment implements onSettingPerfusionListener, onSettingAlertListener {

	private SettingFragmentController mSetController;
	private RadioGroup mSetRg_Tab;
	private RadioButton mPerfusion_rb;
	private boolean isFirstVisible = true;
	private SettingPerfusionFragment mSettingPerfusinFragment;
	private SettingAlertFragment mSettingAlertFragment;
	private onSettingListener onSettingListener;
	public interface onSettingListener{
		void onFinishSetPerfison(boolean isArtSet, boolean isVeinSet,boolean isArtModeChange, boolean isVeinModeChange);
		void onFinishSetSpeedLimit(boolean isArtSet,boolean isVeinSet);
		void onFinishSaveAlert();
		void onSetZeroPressure(int pumptype);
		void onSetZeroFlow(int pumptype);
	}
	
	public void settingListener(onSettingListener listener){
		this.onSettingListener = listener;
	}
	
		
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Log.i("SettingFragment", "---SettingFragment onHiddenChanged----" + hidden);
		if(hidden){
			//隐藏
			if(isFirstVisible){
				isFirstVisible = false;
			}else{
				if(null != mSetController){					
					mPerfusion_rb.setChecked(true);
					mSetController.showFragment(0);
				}
			}
		}else{
			if(null != mSetController){					
				mPerfusion_rb.setChecked(true);
				mSetController.showFragment(0);
			}
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != mSetController){
			mSetController.destoryController();
			mSetController = null;
		}
		//防止内存泄漏
		if(null != onSettingListener){
			onSettingListener = null;
		}

	}

	@Override 
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_setting;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		initVariables();
		mSetRg_Tab = (RadioGroup)rootView.findViewById(R.id.setting_rg_tab);
		mPerfusion_rb = (RadioButton)rootView.findViewById(R.id.setting_sub_title_perfusion_rb);
	}
	
	private void initVariables(){
		mSetController = SettingFragmentController.getInstance(this, R.id.setting_parent_container);
		mSetController.showFragment(0);
		mSettingPerfusinFragment = (SettingPerfusionFragment)mSetController.getFragment(0);
		mSettingAlertFragment = (SettingAlertFragment)mSetController.getFragment(1);
	}
	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mSetRg_Tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				switch (checkedId) {
				case R.id.setting_sub_title_perfusion_rb:
					mSetController.showFragment(0);
					break;
				case R.id.setting_sub_title_alert_rb:
					mSetController.showFragment(1);
					break;
					
				case R.id.setting_sub_title_system_rb:
					mSetController.showFragment(2);
					break;
				default:
					break;
				}
			}
		});
		mSettingPerfusinFragment.setOnSettingPerfusionListener(this);
		mSettingAlertFragment.setOnSettingAlertListener(this);
	}



	@Override
	public void onFinishSetPerfison(boolean isArtSet, boolean isVeinSet,boolean isArtModeChange, boolean isVeinModeChange) {
		// TODO Auto-generated method stub
		if(null != onSettingListener){
			onSettingListener.onFinishSetPerfison(isArtSet, isVeinSet,isArtModeChange,isVeinModeChange);
			
		}
	}


	/**
	 * @param pumptype : artery:0 vein:1
	 */
	@Override
	public void onSetZeroPressure(int pumptype) {
		// TODO Auto-generated method stub
		if(null != onSettingListener){
			onSettingListener.onSetZeroPressure(pumptype);
		}
	}


	/**
	 * @param pumptype : artery:0 vein:1
	 */
	@Override
	public void onSetZeroFlow(int pumptype) {
		// TODO Auto-generated method stub
		if(null != onSettingListener){
			onSettingListener.onSetZeroFlow(pumptype);
		}
	}

	/***修改了泵速限制值设置**/
	@Override
	public void onFinishSetLimitSpeed(boolean isArtSet, boolean isVeinSet) {
		// TODO Auto-generated method stub
		if(null != onSettingListener){
			onSettingListener.onFinishSetSpeedLimit(isArtSet, isVeinSet);
		}
	}


	@Override
	public void onFinishSaveAlert() {
		// TODO Auto-generated method stub
		if(null != onSettingListener){
			onSettingListener.onFinishSaveAlert();
		}
	}

}
