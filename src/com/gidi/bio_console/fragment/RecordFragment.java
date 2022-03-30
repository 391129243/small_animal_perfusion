package com.gidi.bio_console.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.RadioGroup;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;

import com.gidi.bio_console.bean.SerialMessage;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.fragment.ctrl.RecordFragmentController;
import com.gidi.bio_console.fragment.record.RecordChartFragment;
import com.gidi.bio_console.fragment.record.RecordDataFragment;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;

public class RecordFragment extends BaseFragment{

	private static final String TAG = "RecordFragment";
	private RecordFragmentController controller;
	private RecordChartFragment mRecordChartFragment;
	private RecordDataFragment mRecordDataFragment;
	private ArrayList<SerialMessage> mRecordList;
	private SerialLogAsyncTask mSearchTask;
	private RadioGroup mRecordMode_rg;
	private String startPerfusionTime;//当前liver开始灌注的时间

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){
			long startTime = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
					.getLongValue(SharedConstants.START_PERFUSION_TIME, 0);
			startPerfusionTime = DateFormatUtil.formatFullDate(startTime);
			if(null == mSearchTask){
				mSearchTask = new SerialLogAsyncTask(this);
				mSearchTask.execute();
			}
			

		}else{
			cancelTask();
			if(null != mRecordList){
				mRecordList.clear();			
			}

		}
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != controller){
			controller.destoryController();
			controller = null;
		}
		cancelTask();
		clearList();		
	}


	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_record;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mRecordMode_rg =(RadioGroup)rootView.findViewById(R.id.record_rg_tab);
		//mExportTxt = (TextView)rootView.findViewById(R.id.record_history_output_txt);
		//mRecordModeCheck = (CheckBox)rootView.findViewById(R.id.perfusion_data_trend_checkbox);
		initVariables();
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		//mExportTxt.setOnClickListener(this);
		mRecordMode_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.record_perfusion_data_rb:
					controller.showFragment(0);
					
					break;

				case R.id.record_bile_rb:
					controller.showFragment(1);
					
					break;
				default:
					break;
				}
			}
		});
		
	}

	private void initVariables(){
		controller = RecordFragmentController.getInstance(this, R.id.record_parent_container);
		controller.showFragment(0);

		mRecordDataFragment =(RecordDataFragment) controller.getFragment(0);
		mRecordChartFragment =(RecordChartFragment) controller.getFragment(1);
		mRecordList = new ArrayList<SerialMessage>();
		long startTime = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getLongValue(SharedConstants.START_PERFUSION_TIME, 0);
		startPerfusionTime = DateFormatUtil.formatFullDate(startTime);
		Log.i(TAG, "startPerfusionTime---" + startPerfusionTime);
	}
	
	private void setRecordListToData(){
		if(null != mRecordDataFragment){
			mRecordDataFragment.setRecordList(mRecordList);
		}
	}
	
	
	private void setRecordListToChart(){
		if(null != mRecordChartFragment){
			mRecordChartFragment.setRecordList(mRecordList);
		}
	}
	
	
	private void cancelTask(){
		if(null != mSearchTask){
			mSearchTask .cancel(true);
			mSearchTask = null;
		}				
	}
	
	private void clearList(){
		if(null != mRecordList){
			mRecordList.clear();
			mRecordList = null;
		}		
		
	}
	
	/***
	 * 程序数据表
	 * @author 80657
	 *
	 */
	static class SerialLogAsyncTask extends AsyncTask<Void, Integer, ArrayList<SerialMessage>>{
		private WeakReference<RecordFragment> weakFragment;
		
		public SerialLogAsyncTask(RecordFragment fragment){
			weakFragment = new WeakReference<RecordFragment>(fragment);
		}	
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();		
			RecordFragment fragment = weakFragment.get();
			if(null != fragment){
				if(null != fragment.mRecordList){
					fragment.mRecordList.clear();	
				}				
			}
			
		}

		//后台进程的执行
		@Override
		protected ArrayList<SerialMessage> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			RecordFragment fragment = weakFragment.get();
			if(null != fragment){
				String liverNum = PreferenceUtil.getInstance(fragment.getActivity().getApplicationContext()).getStringValue(SharedConstants.LIVER_NUMBER, "");
				fragment.mRecordList = DatabaseMgr.getInstance(fragment.getActivity().getApplicationContext())
						.getLiverPerfusionFromStarttime(liverNum, fragment.startPerfusionTime);
				return fragment.mRecordList;
			}else{
				return null;
			}

			
		}

		@Override
		protected void onPostExecute(ArrayList<SerialMessage> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			RecordFragment fragment = weakFragment.get();
			if(null != fragment){
				fragment.setRecordListToData();
				fragment.setRecordListToChart();
				if(null != fragment.mSearchTask){
					fragment.mSearchTask .cancel(true);
					fragment.mSearchTask = null;
				}
			}
		}			
	}
	
	


}
