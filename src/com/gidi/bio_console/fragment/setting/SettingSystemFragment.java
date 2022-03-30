package com.gidi.bio_console.fragment.setting;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.gidi.bio_console.R;
import com.gidi.bio_console.activity.TimeSettingActivity;
import com.gidi.bio_console.activity.WlanConnectActivity;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.view.CustomSystemSetItem;
import com.gidi.bio_console.view.SystemSettingItem;
import com.gidi.bio_console.view.SystemSettingItem.OnSystemSetItemClickListener;

public class SettingSystemFragment extends BaseFragment implements OnClickListener {

	private CustomSystemSetItem mBubbleStop;
	private boolean isBubbleStopOpen = false;
	private SystemSettingItem wifiSettingItem;
	private SystemSettingItem timeSettingItem;
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			
		}else{
			initDatas();
		}
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_setting_system;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mBubbleStop = (CustomSystemSetItem)rootView.findViewById(R.id.system_setting_bubble_stop_item);
		wifiSettingItem = (SystemSettingItem)rootView.findViewById(R.id.system_setting_wifi_item);
		timeSettingItem = (SystemSettingItem)rootView.findViewById(R.id.system_setting_time_item);
		initDatas();
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mBubbleStop.setOnClickListener(this);
		wifiSettingItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SettingSystemFragment", "wifi");
				if(null != getBaseActivity()){
					Intent intent = new Intent(getBaseActivity(), WlanConnectActivity.class);
					startActivity(intent);
				}

			}
		});
		timeSettingItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SettingSystemFragment", "time");
				if(null != getBaseActivity()){
					Intent intent = new Intent(getBaseActivity(), TimeSettingActivity.class);
					startActivity(intent);
				}

			}
		});
	}
	
	private void initDatas(){
		boolean isBubbleStop = PreferenceUtil.getInstance(getBaseActivity()).getBooleanValue(SharedConstants.SYSTEM_SETTING_BUBBLESTOP, false);
		if(!isBubbleStop){
			mBubbleStop.setCheck(false);
		}else {
			mBubbleStop.setCheck(true);
		}
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.system_setting_bubble_stop_item:
			if(!isBubbleStopOpen){
				mBubbleStop.setCheck(true);
				isBubbleStopOpen = true;			
			}else{
				mBubbleStop.setCheck(false);
				isBubbleStopOpen = false;
			}
			PreferenceUtil.getInstance(getBaseActivity()).setValueByName(SharedConstants.SYSTEM_SETTING_BUBBLESTOP, isBubbleStopOpen);
			break;

		default:
			break;
		}
	}

}
