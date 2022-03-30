package com.gidi.bio_console.kidney;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.RadioGroup;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;

import com.gidi.bio_console.bean.KidneyInfoBean;
import com.gidi.bio_console.bean.SerialMessage;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;

/**
 * 肾脏灌注记录
 * @author 80657
 *
 */
public class KidneyRecordFragment extends BaseFragment{

	private static final String TAG = "KidneyRecordFragment";
	private ControllerKidneyRecord controller;
	private KidneyChartFragment mRecordChartFragment;
	private KidneyDataFragment mRecordDataFragment;
	private ArrayList<KidneyInfoBean> mRecordList;
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
		initVariables();
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
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
		controller = ControllerKidneyRecord.getInstance(this, R.id.record_parent_container);
		controller.showFragment(0);

		mRecordDataFragment =(KidneyDataFragment) controller.getFragment(0);
		mRecordChartFragment =(KidneyChartFragment) controller.getFragment(1);
		mRecordList = new ArrayList<KidneyInfoBean>();
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
	static class SerialLogAsyncTask extends AsyncTask<Void, Integer, ArrayList<KidneyInfoBean>>{
		private WeakReference<KidneyRecordFragment> weakFragment;
		
		public SerialLogAsyncTask(KidneyRecordFragment fragment){
			weakFragment = new WeakReference<KidneyRecordFragment>(fragment);
		}	
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();		
			KidneyRecordFragment fragment = weakFragment.get();
			if(null != fragment){
				if(null != fragment.mRecordList){
					fragment.mRecordList.clear();	
				}				
			}
			
		}

		//后台进程的执行
		@Override
		protected ArrayList<KidneyInfoBean> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			KidneyRecordFragment fragment = weakFragment.get();
			if(null != fragment){
				String kidneyNum = PreferenceUtil.getInstance(fragment.getActivity().getApplicationContext())
						.getStringValue(SharedConstants.KIDNEY_NUM, "");
				fragment.mRecordList = DatabaseMgr.getInstance(fragment.getActivity().getApplicationContext())
						.getKidneyPerfusionFromStarttime(kidneyNum, fragment.startPerfusionTime);
				return fragment.mRecordList;
			}else{
				return null;
			}

			
		}

		@Override
		protected void onPostExecute(ArrayList<KidneyInfoBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			KidneyRecordFragment fragment = weakFragment.get();
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
