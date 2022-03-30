package com.gidi.bio_console.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.AlarmLogAdapter;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.AlarmMsgEnity;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.view.CustomDialog;

public class AlarmFragment extends BaseFragment {

	private TextView searchText;
	private ListView mAlarmListView;
	private ArrayList<AlarmMsgEnity> mAlarmMsgList;
	private AlarmLogAdapter mAdapter;
	private AlarmLogAsyncTask mAlarmLogAsyncTask;
	private DatabaseMgr mDatabaseMgr;
	private CustomDialog mDatePickerDialog;
	private String searchTime;
	private int year,month,day;

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_alarm;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		searchText = (TextView)rootView.findViewById(R.id.date_select_date_txt);
		mAlarmListView = (ListView)rootView.findViewById(R.id.alarm_log_listview);
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		initVariable();
		//点击查询
		searchText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String endDate= searchText.getText().toString().trim();
				year = DateFormatUtil.getYear(endDate);
				month = DateFormatUtil.getMonth(endDate);
				day = DateFormatUtil.getDay(endDate);
				showDatePickerDialog(year,month,day);
			}
		});
	}

	private void initVariable(){
		long startTime = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getLongValue(SharedConstants.START_PERFUSION_TIME, 0);
		searchTime = DateFormatUtil.formatDate(startTime);
		mAlarmMsgList = new ArrayList<AlarmMsgEnity>();
		mAlarmMsgList.clear();		
		mDatabaseMgr = DatabaseMgr.getInstance(getActivity().getApplicationContext());
		mAdapter = new AlarmLogAdapter(getActivity().getApplicationContext());
		mAlarmListView.setAdapter(mAdapter);	
		if(null == mAlarmLogAsyncTask){
			mAlarmLogAsyncTask = new AlarmLogAsyncTask(this);
			mAlarmLogAsyncTask.execute();
		}
		searchText.setText(searchTime);
	}
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			cancelTask();
		}else{
			if(null == mAlarmLogAsyncTask){
				mAlarmLogAsyncTask = new AlarmLogAsyncTask(this);
				mAlarmLogAsyncTask.execute();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != mAlarmMsgList){
			mAlarmMsgList.clear();
			mAlarmMsgList = null;
		}	
		cancelDialog();
		cancelTask();
	}


	public void setAlarmLogList(ArrayList<AlarmMsgEnity> list) {
		// TODO Auto-generated method stub
		Log.i("AlarmFragment", "---setAlarmLogList---" + list.size());
		if(null != mAlarmMsgList){
			if(null != mAdapter){
				mAdapter.setAlarmList(mAlarmMsgList);
				mAdapter.notifyDataSetChanged();							
			}
		}		
	}
	
	private void showDatePickerDialog(int year, int month, int dayOfMonth){
		if(null != mDatePickerDialog){
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		if(null == mDatePickerDialog){
			mDatePickerDialog = new CustomDialog(getActivity());
			LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pickView = mInflater.inflate(R.layout.dialog_datepicker_dialog, null);
			final DatePicker datePicker = (DatePicker)pickView.findViewById(R.id.date_picker);
			datePicker.init(year, month, dayOfMonth, new OnDateChangedListener() {
				
				@Override
				public void onDateChanged(DatePicker view, int year, int month, int day) {
					// TODO Auto-generated method stub
					 Calendar calendar = Calendar.getInstance();
					 calendar.set(year, month, day);					
				}
			});
			CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
			builder.setContentView(pickView);
			builder.setTitle(getActivity().getResources().getString(R.string.dialog_title_choice_date));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Log.i("AlarmFragment", "Year--" + datePicker.getYear() + " month---" + datePicker.getMonth() + "day---" + datePicker.getDayOfMonth());
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
	
	private void updateDate(final int year, final int month, final int day){
		this.year = year;
		this.month = month + 1;
		this.day = day;
		searchText.setText((String.format("%04d-%02d-%02d",this.year,this.month,this.day)));
		this.searchTime = searchText.getText().toString().trim();
		Log.i("AlarmFragment", "updateDate---searchTime" + searchTime);
		if(null == mAlarmLogAsyncTask){
			mAlarmLogAsyncTask = new AlarmLogAsyncTask(this);
			mAlarmLogAsyncTask.execute();
		}
	}
		
	
	private void cancelTask(){
		if(null != mAlarmLogAsyncTask){
			mAlarmLogAsyncTask.cancel(true);
			mAlarmLogAsyncTask = null;
		}
	}
	
	private void cancelDialog(){
		if(null != mDatePickerDialog){
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
	}
	
	public static class AlarmLogAsyncTask extends AsyncTask<Void, Integer, ArrayList<AlarmMsgEnity>>{
		 
		private WeakReference<AlarmFragment> weakfrag;
        public AlarmLogAsyncTask(AlarmFragment fragment){
        	weakfrag = new WeakReference<AlarmFragment>(fragment);
        }
              
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			final AlarmFragment fragment = weakfrag.get(); 
			super.onPreExecute();
			if(null == fragment){
				return;
			}
			fragment.mAlarmMsgList.clear();
		}

		//后台进程的执行
		@Override
		protected ArrayList<AlarmMsgEnity> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			final AlarmFragment fragment = weakfrag.get(); 
			if(null == fragment){
				return null;
			}
			fragment.mAlarmMsgList = fragment.mDatabaseMgr.getAlarmMsgFromTime(fragment.searchTime);
			Log.i("AlarmFragment", "AlarmLogAsyncTask---searchTime" + fragment.searchTime);
			return fragment.mAlarmMsgList;
			

		}

		@Override
		protected void onPostExecute(ArrayList<AlarmMsgEnity> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);		
			final AlarmFragment fragment = weakfrag.get(); 
			if(null != fragment.mAlarmMsgList){
				if(fragment.mAlarmMsgList.size()>0){
					fragment.setAlarmLogList(fragment.mAlarmMsgList);
				}
			}	
			fragment.cancelTask();
		}	
	}
}
