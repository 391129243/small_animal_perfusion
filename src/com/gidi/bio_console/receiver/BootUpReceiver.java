package com.gidi.bio_console.receiver;


import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver{

	private static final String ACTION_BOOTUP = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(ACTION_BOOTUP)){
			Log.i("BioConsoleApplication", "--BootUpReceiver onCreate--");
			restoreVariables(context);
		}
		Intent endBootUpIntent = new Intent(BroadcastActions.ACTION_END_BOOTUP_SYSTEM);                   	             	
        LocalBroadcastManager.getInstance(context).sendBroadcast(endBootUpIntent);
		context.sendBroadcast(endBootUpIntent);
	}

	public void restoreVariables(Context context){
		boolean isCrash = PreferenceUtil.getInstance(context.getApplicationContext()).getBooleanValue(SharedConstants.IS_CRASH, false);
		Log.i("BioConsoleApplication", "- BootUpReceiver-restoreVariables--" + "isCrash---" + isCrash);
		if(!isCrash){
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.IS_PRESSURE_ZERO_FIRST, true);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.IS_PERFUSION, false);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.IS_CRASH, false);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
			

			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MIN_ONE,(0.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MAX_ONE, (100.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MIN_TWO, (0.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MAX_TWO, (14.0f));

			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_MAX_ONE, (1000.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_MIN_ONE, (0.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_MAX_TWO,(2000.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_MIN_TWO, (0.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_ARTERY_FLOW,(0.0f));
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_VEIN_FLOW,(0.0f));


			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_ARTERY_SPEED, 0);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_VEIN_SPEED, 0);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LIVER_NUMBER, "");
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LIVER_WEIGHT, 0);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.REMOTECTRL_SERVER_URL, "");
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.REMOTECTRL_SERVER_DID, 0);
			//温度控制、温度模式
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_TEMP, 37.0f);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TEMP_CTRL, false);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TEMP_PERFUSION_MODE, 0);
			
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, "");
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, "");
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.START_PERFUSION_TIME, 0);
			PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.IS_USB_PLUNG, false);
			
			restoreKidneyVariable(context);
		}
		
	}
	
	
	public void restoreKidneyVariable(Context context){

		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.KIDNEY_NUM, "");
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX,(0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, (0.0f));

		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_LEFT_KIDNEY_WARN_MAX, (1000.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_LEFT_KIDNEY_WARN_MIN, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_RIGHT_KIDNEY_WARN_MAX,(1000.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.FLOW_RIGHT_KIDNEY_WARN_MIN, (0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_LEFT_KIDNEY_WARN_MIN,(0.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_LEFT_KIDNEY_WARN_MAX,(100.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_RIGHT_KIDNEY_WARN_MIN, (0.0f));//恒流模式的门静脉目标流量
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.PRESSURE_RIGHT_KIDNEY_WARN_MAX, (100.0f));
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.SPEED_LEFT_KIDNEY_LIMIT, 1500);
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.SPEED_RIGHT_KIDNEY_LIMIT, 1500);
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.LEFT_KIDNEY_WEIGHT, 0);
		PreferenceUtil.getInstance(context.getApplicationContext()).setValueByName(SharedConstants.RIGHT_KIDNEY_WEIGHT, 0);
	}
}
