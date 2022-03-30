package com.gidi.bio_console.base;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

public abstract class BaseFragmentActivity extends FragmentActivity {
	private DatabaseMgr mDatabaseMgr = null;
	private SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private String liverNumber;
	private String kidneyNum;
	private int errorCode;
	//抽象函数用于继承
	protected abstract int getLayoutId();
	
	protected abstract void initView();
	
	protected abstract void initListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		mDatabaseMgr = DatabaseMgr.getInstance(getApplicationContext());
		switchLanguage(PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.LANGUAGE, "zh"));
		initView();
		initListener();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDatabaseMgr = null;
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	protected void switchLanguage(String language) {
		Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
       if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
        	 config.locale = Locale.SIMPLIFIED_CHINESE;
       }
        resources.updateConfiguration(config, dm);
        PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LANGUAGE, language);
    }

	/**
	 * Shielding back key
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            return true;  
        }
        return false;  
    }  

	
	//开个线程写入数据库
	public void insertAlarmMessage(int errorCode ,final String msgType ){		
		
		this.errorCode = errorCode;
		String perfusionType = PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
		if(perfusionType.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			liverNumber = PreferenceUtil.getInstance(getApplicationContext())
					.getStringValue(SharedConstants.LIVER_NUMBER, "");
			if(!StringUtil.isEmpty(liverNumber)){				
				new WorkerThread(this,Constants.LIVER_PERFUSION_STYSTEM).start();	
			}
		}else if(perfusionType.equals(Constants.KIDNEY_PERFUSION_STYSTEM)){
			kidneyNum = PreferenceUtil.getInstance(getApplicationContext())
					.getStringValue(SharedConstants.KIDNEY_NUM, "");
			if(!StringUtil.isEmpty(kidneyNum)){
				new WorkerThread(this,Constants.KIDNEY_PERFUSION_STYSTEM).start();	
			}
		}
							
	}
	
	private static class WorkerThread extends Thread{
    	WeakReference<BaseFragmentActivity> mActivityReference;
		String mPerfusionType;
		public WorkerThread(BaseFragmentActivity mActivity,String perfusionType){
			mActivityReference = new WeakReference<BaseFragmentActivity>(mActivity);
			this.mPerfusionType = perfusionType;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			final BaseFragmentActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			String time = mActivity.sdf.format(new Date(System.currentTimeMillis()));
			Log.i("BaseFragmentActivity", "insertAlarmMessage" + mActivity.errorCode);
			if(mPerfusionType.equals(Constants.LIVER_PERFUSION_STYSTEM)){
				mActivity.mDatabaseMgr.insertAlarmMsg(mActivity.liverNumber, (mActivity.errorCode +""),"error" ,time);
			}else if(mPerfusionType.equals(Constants.KIDNEY_PERFUSION_STYSTEM)){
				mActivity.mDatabaseMgr.insertAlarmMsg(mActivity.kidneyNum, (mActivity.errorCode +""),"error" ,time);
			}
				

		}
    }

}
