package com.gidi.bio_console.activity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.DateTimeView;

public class TimeSettingActivity extends BaseActivity implements OnClickListener {

	private static String TAG = "TimeSettingActivity";
	private Context mContext;
	private RelativeLayout mTitleLayout;
	private LinearLayout setTimeLayout;
	private LinearLayout setDateLayout;
	private LinearLayout setZoneLayout;
	private TextView dateTxt;
	private TextView timeTxt;
	private TextView zoneTxt;
	private TextView mTitleTxt;
	private ImageView mTitleImg;
	private CustomDialog mDatePickerDialog;
	private CustomDialog mTimePickerDialog;
	private DateTimeView mDateTimeText;	
	private UIHandler mUIHandler;
	private ImageView backImg;
	private Calendar calendarDate;
	private long total_Time = 0;
	private int mYear, mMonth, mDay;
	private int mHour, mMinute;	
	private AlarmManager am;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		initData();
		initUpdateSysTime();
	}
	
	
	/**
	 * 工作线程的销毁和停止在onDestroy执行的时机比较晚，因此在Actovity在onPause/onStop()结合isFinishing()的判断来执行
	 * 执行finish后先经过onpause-onStop-onDetroy
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(isFinishing()){
			if(null != mUIHandler){
				mUIHandler.removeCallbacksAndMessages(null);
				mUIHandler = null;
				runnable = null;
			}
			cancelDialog();
			calendarDate = null;
			am = null;
		}
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext = null;
	}
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_time_setting;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mTitleLayout = (RelativeLayout)findViewById(R.id.time_title_layout);
		setTimeLayout = (LinearLayout)findViewById(R.id.time_set_time_layout);
		setDateLayout = (LinearLayout)findViewById(R.id.time_set_date_layout);
		setZoneLayout = (LinearLayout)findViewById(R.id.time_set_zone_layout);
		setZoneLayout.setClickable(false);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text_s);
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon_s);
		mDateTimeText = (DateTimeView)mTitleLayout.findViewById(R.id.base_systime_txt_s);
		dateTxt = (TextView)findViewById(R.id.time_set_date_txt);
		timeTxt = (TextView)findViewById(R.id.time_set_time_txt);
		zoneTxt = (TextView)findViewById(R.id.time_set_zone_txt);
		backImg = (ImageView)findViewById(R.id.title_back_img);
		mTitleImg.setImageResource(R.drawable.function_title_set_time_img);
		mTitleTxt.setText(R.string.date_title);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		setTimeLayout.setOnClickListener(this);
		setDateLayout.setOnClickListener(this);
		setZoneLayout.setOnClickListener(this);
		backImg.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mContext = this;
		mUIHandler = new UIHandler(this);
		calendarDate = Calendar.getInstance();
		String timeZone = DateFormatUtil.getTimeZone();
		Log.i(TAG, "initData" + "timeZone---" + timeZone);
		TimeZone mTimeZone = TimeZone.getTimeZone(timeZone);
		calendarDate.setTimeZone(mTimeZone);
		zoneTxt.setText(timeZone);
		mYear = calendarDate.get(Calendar.YEAR);
		mMonth = calendarDate.get(Calendar.MONTH);//获取的值比实际月份少一天
		mDay = calendarDate.get(Calendar.DAY_OF_MONTH);
		mHour = calendarDate.get(Calendar.HOUR_OF_DAY);
		mMinute = calendarDate.get(Calendar.MINUTE);
		am = (AlarmManager)getSystemService(ALARM_SERVICE);   
		Log.i("TimeSettingActivity", "initData" + " mYear---" + mYear + " mMonth---" + mMonth + " mDay---" + mDay);
	}

	private void initUpdateSysTime(){
		mUIHandler.postDelayed(runnable, 1000);		
	}
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//TODO:延时要干的其他事情
			mUIHandler.sendEmptyMessage(Constants.MSG_UPDATE_TIME);
			mUIHandler.postDelayed(runnable, 1000);
		}
	};
	
	
	private void cancelDialog(){
		if(null != mDatePickerDialog){
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		if(null != mTimePickerDialog){
			mTimePickerDialog.dismiss();
			mTimePickerDialog = null;
		}
	}
	

	private void updateDate(int year,  int monthOfYear, int dayOfMonth){
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;		
		calendarDate.set(Calendar.YEAR, year);
		calendarDate.set(Calendar.MONTH, monthOfYear);
		calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		long when_data = calendarDate.getTimeInMillis();
		total_Time = when_data;
		if ((total_Time / 1000 )< Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(total_Time);
		}
		Log.i(TAG ,"updateDate---"+"year---" + year + " :" + " monthOfYear----"+ monthOfYear + " : " + "dayOfMonth---" + dayOfMonth);
	}
	
	private void updateTime(int hourOfDay, int minute){
		mHour = hourOfDay;
		mMinute = minute;
		calendarDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendarDate.set(Calendar.MINUTE, minute);
		calendarDate.set(Calendar.SECOND, 0);
		calendarDate.set(Calendar.MILLISECOND, 0);
		long when_time = calendarDate.getTimeInMillis();
		
		if (when_time / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when_time);
		}
		// TODO Auto-generated method stub
		System.out.println(hourOfDay + ":" + hourOfDay);
	}
	
	private void updateZone(){
		mYear = calendarDate.get(Calendar.YEAR);
		mMonth = calendarDate.get(Calendar.MONTH);//获取的值比实际月份少一天
		mDay = calendarDate.get(Calendar.DAY_OF_MONTH);
		mHour = calendarDate.get(Calendar.HOUR_OF_DAY);
		mMinute = calendarDate.get(Calendar.MINUTE);
		updateDate(mYear,mMonth,mDay);
		updateTime(mHour,mMinute);
	}
	
	private void showDatePickerDialog(int year, int month, int dayOfMonth){
		if(null != mDatePickerDialog){
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		if(null == mDatePickerDialog){
			mDatePickerDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pickView = mInflater.inflate(R.layout.dialog_datepicker_dialog, null);
			final DatePicker datePicker = (DatePicker)pickView.findViewById(R.id.date_picker);
			datePicker.init(year, month, dayOfMonth, new OnDateChangedListener() {				
				@Override
				public void onDateChanged(DatePicker view, int year, int month, int day) {
					// TODO Auto-generated method stub
					 calendarDate.set(year, month, day);
					 Log.i(TAG ,"showDatePickerDialog---" +"year--" + year + " month---" + month+" day---" +day);
				}
			});
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(pickView);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_choice_date));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Log.i(TAG, "datePicker.getYear()--" + datePicker.getYear() + " datePicker.getMonth()---" + datePicker.getMonth() + "datePicker.getDayOfMonth()---" + datePicker.getDayOfMonth());
					updateDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
					mDatePickerDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mDatePickerDialog.dismiss();
					mDatePickerDialog = null;
				}
			});
			mDatePickerDialog = builder.create();
			mDatePickerDialog.show();
		}
	}
	
	private void showTimePickerDialog(int hour,int minute){
		if(null != mTimePickerDialog){
			mTimePickerDialog.dismiss();
			mTimePickerDialog = null;
		}
		if(null == mTimePickerDialog){
			mTimePickerDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pickView = mInflater.inflate(R.layout.dialog_timepicker_dialog, null);
			final TimePicker timePicker = (TimePicker)pickView.findViewById(R.id.timer_picker);
			timePicker.setIs24HourView(true);
			timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
				
				@Override
				public void onTimeChanged(TimePicker view, final int hourOfDay,final int minute) {
					// TODO Auto-generated method stub
					calendarDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendarDate.set(Calendar.MINUTE, minute);
				}
			});
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(pickView);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_choice_time));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					//Log.i("TimeSettingActivity", "datePicker.getYear()--" + datePicker.getYear() + " datePicker.getMonth()---" + datePicker.getMonth() + "datePicker.getDayOfMonth()---" + datePicker.getDayOfMonth());
					updateTime(calendarDate.get(Calendar.HOUR_OF_DAY), calendarDate.get(Calendar.MINUTE));
					mTimePickerDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mTimePickerDialog.dismiss();
					mTimePickerDialog = null;
				}
			});
			mTimePickerDialog = builder.create();
			mTimePickerDialog.show();
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_back_img:
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			break;

		case R.id.time_set_time_layout:
			showTimePickerDialog(mHour, mMinute);
			break;
			
		case R.id.time_set_date_layout:
			showDatePickerDialog(mYear,mMonth,mDay);
			break;
	
		case R.id.time_set_zone_layout:
			Intent intent = new Intent(TimeSettingActivity.this,TimeZoneActivity.class);
			startActivityForResult(intent, 1);
			break;
			
		default:
			break;
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 2) {
			if (requestCode == 1) {
			String timezoneGMT_Id = data.getStringExtra("timezone");
			//设置结果显示框的显示数值
				//setTimeZone(timezoneId);
			    if (timezoneGMT_Id != null){
					am.setTimeZone(timezoneGMT_Id);
			    }
				mUIHandler.sendEmptyMessageDelayed(Constants.MSG_UPDATE_ZONE, 500);	
			}
		 }
	}


	/**
	 * 
	 * @author 80657
	 *
	 */
	public static class UIHandler extends Handler{
		
		WeakReference<TimeSettingActivity> mActivityReference;
			
		public UIHandler(TimeSettingActivity mActivity){
			mActivityReference = new WeakReference<TimeSettingActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final TimeSettingActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			switch (msg.what) {			
			case Constants.MSG_UPDATE_TIME:
				String sysTime = DateFormatUtil.getNowTime();
				String sysDate = DateFormatUtil.getNowDate();
				mActivity.timeTxt.setText(DateFormatUtil.getNowTimeHM());
				mActivity.dateTxt.setText(DateFormatUtil.getNowDate());
				mActivity.mDateTimeText.setDateText(sysDate);
				mActivity.mDateTimeText.setTimeText(sysTime);
				break;
			case Constants.MSG_UPDATE_ZONE:
				String timeZone = DateFormatUtil.getTimeZone();
				mActivity.zoneTxt.setText(timeZone);
				mActivity.updateZone();
				break;
			}
		}
		
	}
	
}
