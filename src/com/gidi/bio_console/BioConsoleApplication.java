package com.gidi.bio_console;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.LanguageType;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.log.LogcatHelper;
import com.gidi.bio_console.mgr.SystemAlarmMgr;
import com.gidi.bio_console.service.BackService;
import com.gidi.bio_console.utils.CommonUtil;
import com.gidi.bio_console.utils.CrashHandler;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.ToastUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BioConsoleApplication extends Application {

	private static Context mContext;
	private static BioConsoleApplication mApplication;
	
	private List<Activity> mActivityList;
	
	private SystemAlarmMgr mSysAlarmManager;

	private boolean isStopPump = false;
	
	private boolean isTempCtrlOpen = false;
	/**肝动脉心跳消息**/
	private boolean isArteryHeartConnect = true;	
	/**门静脉心跳消息**/
	private boolean isVeinHeartConnect = true;
		
	private boolean mIsMusicPlaying = false;
	
	public static String mLiverNum;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		mApplication = this;
		mContext = getApplicationContext();

		mActivityList = new ArrayList<Activity>();

		mSysAlarmManager = getSysAlarmManager();
		DatabaseMgr.getInstance(getApplicationContext());
		//启动故障收集
		CrashHandler crashHandler = CrashHandler.getInstance(this);
		crashHandler.init(this);
		//启动日志收集功能
		LogcatHelper.getInstance(this).start(); 
		//首先判断服务是否已经启动
		Log.i("BioConsoleApplication", "--isServiceRunning--" + isServiceRunning("com.gidi.bio_console.service.BackService"));	
		if(!isServiceRunning("com.gidi.bio_console.service.BackService")){
			Intent intent = new Intent(getApplicationContext(), BackService.class);
			startService(intent);	
		}
		onLanguageChange();
		Log.i("BioConsoleApplication", "--BioConsoleApplication onCreate--");	
	}
	

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();		
		Intent intent = new Intent(getApplicationContext(), BackService.class);
		stopService(intent);
		LogcatHelper.getInstance(this).stop();
		DatabaseMgr.getInstance(getApplicationContext()).stopDB();
		ToastUtils.cancelToast();
	}

	/**增加Activity**/
	public void addActivity(Activity activity){
		if(!mActivityList.contains(activity)){
			mActivityList.add(activity);
		}
		Log.i("BioConsoleApplication", "mActivityList size" + mActivityList.size());
	}
	
	/**移除Activity**/
	public void removeActivity(Activity activity){
		if(mActivityList.contains(activity)){
			mActivityList.remove(activity);
			activity.finish();
		}
		Log.i("BioConsoleApplication", "mActivityList size" + mActivityList.size());
	}
	
	
	/**销毁所有的activity并杀死应用线程**/
	public void removeAllActivity(){
		// 通过循环，把集合中的所有Activity销毁
        for (Activity activity : mActivityList) {
            if (activity != null){
                activity.finish();
            }
        }
        finishApp();
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	/**销毁所有的Activity**/
	public void finishActivity(){
		for(Activity activity : mActivityList){
			if(null != activity){
				activity.finish();
				activity = null;
			}
		}
		
	}
	
	public void finishApp(){
		Log.i("BioConsoleApplication", "--finishApp--");		
		LogcatHelper.getInstance(this).stop();
		DatabaseMgr.getInstance(getApplicationContext()).stopDB();
		ToastUtils.cancelToast();
		Intent intent = new Intent(getApplicationContext(), BackService.class);
		mApplication.stopService(intent);
	}
		


	public static BioConsoleApplication getInstance(){
		return mApplication;
	}
	
	private SystemAlarmMgr getSysAlarmManager(){
		return SystemAlarmMgr.getInstance(mApplication);
	}
	
	public static Context getContext(){
		
		return mContext;
	}

	private boolean isServiceRunning(String className){
		ActivityManager myManager = (ActivityManager)mContext
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		if(!(runningService.size()>0)){
			return false;
		}
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals(className)) {
				return true;
			}
		}
		return false;
	}
		


	public void restoreVariables(){
		Log.i("SystemPresetActivity", "---restoreVariables--");	
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.IS_PRESSURE_ZERO_FIRST, true);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.IS_PERFUSION, false);
		
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);

		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MIN_ONE,(0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MAX_ONE, (100.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MIN_TWO, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_MAX_TWO, (14.0f));

		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_MAX_ONE, (1000.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_MIN_ONE, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_MAX_TWO,(2000.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_MIN_TWO, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_ARTERY_FLOW,(0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_VEIN_FLOW,(0.0f));

		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_ARTERY_SPEED, 0);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_VEIN_SPEED, 0);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LIVER_NUMBER, "");
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LIVER_WEIGHT, 0);
		//远程控制
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.REMOTECTRL_SERVER_URL, "");
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.REMOTECTRL_SERVER_DID, 0);
		//温度控制
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_TEMP, 37.0f);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TEMP_CTRL, false);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TEMP_PERFUSION_MODE, 0);
		
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, "");
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, "");
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.START_PERFUSION_TIME, 0);
		
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.SET_CUSTOM_ARTERY_SPEED, 0);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.SET_CUSTOM_VEIN_SPEED, 0);
			
		//PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LANGUAGE,  LanguageType.CHINESE.getLanguage());
	}
	
	public void restoreKidneyVariable(){

		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.KIDNEY_NUM, "");
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX,(0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, (0.0f));

		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_LEFT_KIDNEY_WARN_MAX, (1000.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_LEFT_KIDNEY_WARN_MIN, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_RIGHT_KIDNEY_WARN_MAX,(1000.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.FLOW_RIGHT_KIDNEY_WARN_MIN, (0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_LEFT_KIDNEY_WARN_MIN,(0.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_LEFT_KIDNEY_WARN_MAX,(100.0f));
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_RIGHT_KIDNEY_WARN_MIN, (0.0f));//恒流模式的门静脉目标流量
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.PRESSURE_RIGHT_KIDNEY_WARN_MAX, (100.0f));
		
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.SPEED_LEFT_KIDNEY_LIMIT, 1500);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.SPEED_RIGHT_KIDNEY_LIMIT, 1500);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LEFT_KIDNEY_WEIGHT, 0);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.RIGHT_KIDNEY_WEIGHT, 0);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LIVER_WEIGHT, 0);
	}

		
	public boolean isArteryHeartConnect() {
		return isArteryHeartConnect;
	}


	public void setArteryHeartConnect(boolean isArteryHeartConnect) {
		this.isArteryHeartConnect = isArteryHeartConnect;
	}


	public boolean isVeinHeartConnect() {
		return isVeinHeartConnect;
	}


	public void setVeinHeartConnect(boolean isVeinHeartConnect) {
		this.isVeinHeartConnect = isVeinHeartConnect;
	}

	public boolean isStopPump() {
		return isStopPump;
	}


	public void setStopPump(boolean isStopPump) {
		this.isStopPump = isStopPump;
	}


	
	public boolean isTempCtrlOpen() {
		return isTempCtrlOpen;
	}


	public void setTempCtrlOpen(boolean isTempCtrlOpen) {
		this.isTempCtrlOpen = isTempCtrlOpen;
	}


	public boolean IsMusicPlaying() {
		return mIsMusicPlaying;
	}

	public void setMusicPlaying(boolean mIsMusicPlaying) {
		this.mIsMusicPlaying = mIsMusicPlaying;
	}

	private void onLanguageChange() {
		String language = PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.LANGUAGE, LanguageType.CHINESE.getLanguage());
		CommonUtil.changeAppLanguage(this, language);
	}
}
