package com.gidi.bio_console.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.activity.PowerOnActivity;
import com.gidi.bio_console.constant.SharedConstants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler{
	
	private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bio_Console/Log/";
	private BioConsoleApplication mApplication;
	private static final String FILE_NAME = "crash";
	private static final String FILE_NAME_SUFFIX = ".txt";
	private boolean mIsRestartApp = true;
	
	private static CrashHandler mInstance;
	private UncaughtExceptionHandler mDefaultCrashHandler;
	private Context mContext;
	
	private CrashHandler(BioConsoleApplication application){
		this.mApplication = application;
	}
	
	/** 获取CrashHandler实例 */
	public static CrashHandler getInstance(BioConsoleApplication application){
		if(null == mInstance){
			mInstance = new CrashHandler(application);
		}
		return mInstance;
	}
	
	public void init(Context context){
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context.getApplicationContext();
		
	}
	

	/***
	 * 如果不使用系统默认的处理崩溃的方法，系统崩溃后，先保存崩溃日志
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try {
//			dumpExceptionToSDCard(ex);
//			mApplication.finishApp();
//			if(mDefaultCrashHandler != null){
//				mDefaultCrashHandler.uncaughtException(thread, ex);
//				
//			}else{
//				Process.killProcess(Process.myPid());
//			}
			if(!dumpExceptionToSDCard(ex) && null != mDefaultCrashHandler){
				//使用系统默认的处理崩溃的方法
				mDefaultCrashHandler.uncaughtException(thread, ex);
			}else{
				Log.i("BioConsoleApplication", "restart system");
				if (mIsRestartApp) { // 如果需要重启
	                Intent intent = new Intent(mContext.getApplicationContext(), PowerOnActivity.class);
	                AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	                //重启应用，得使用PendingIntent
	                PendingIntent restartIntent = PendingIntent.getActivity(
	                        mContext.getApplicationContext(), 0, intent,
	                        Intent.FLAG_ACTIVITY_NEW_TASK);
	                mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
	                        restartIntent); // 重启应用

	            }
				PreferenceUtil.getInstance(mContext.getApplicationContext())
					.setValueByName(SharedConstants.IS_CRASH, true);
				PreferenceUtil.getInstance(mContext.getApplicationContext())
					.setValueByName(SharedConstants.LIVER_NUMBER, BioConsoleApplication.mLiverNum);
	            // 结束应用
	            ((BioConsoleApplication) mContext.getApplicationContext()).removeAllActivity();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	/**handleException(throwable) 方法用于弹出 Toast 和收集 Crash 信息**/
	private boolean dumpExceptionToSDCard(Throwable ex) throws IOException{
		//sdcard
		Log.i("BioConsoleApplication", "dumpExceptionToSDCard");
		if(null == ex){
			return false;
		}
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return false;
		}
		
		File dir = new File(PATH);
		if(!dir.exists()){
			dir.mkdirs();
		}
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(new Date(current));
		File file = new File (PATH + DateFormatUtil.getFileName()+"_"+FILE_NAME + FILE_NAME_SUFFIX);
		if(!file.exists()){
			file.createNewFile();
		}
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println(time);
			dumpPhoneInfo(pw);
			pw.println();
			ex.printStackTrace(pw);
			pw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}

	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException{
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
		pw.print("App version: ");
		pw.print(pi.versionName); 
		pw.print(" - ");
		pw.println(pi.versionCode);
		
		//Android 
		pw.print("OS version: ");
		pw.print(Build.VERSION.RELEASE);
		pw.print(" - ");
		pw.println(Build.VERSION.SDK_INT);

		pw.print("Vendor: ");
		pw.println(Build.MANUFACTURER);
		

		pw.print("Model: ");
		pw.println(Build.MODEL);
						
	}
}
