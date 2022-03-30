package com.gidi.bio_console.fragment;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.BloodGasBean;
import com.gidi.bio_console.bean.BloodGasSamplingBean;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.fragment.bloodgas.BloodGasChartFragment;
import com.gidi.bio_console.fragment.bloodgas.BloodGasDataFragment;
import com.gidi.bio_console.fragment.bloodgas.BloodGasDataFragment.OnBloodGasDataChangeListener;
import com.gidi.bio_console.fragment.ctrl.BloodGasFragmentController;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;


public class BloodGasFragment extends BaseFragment implements OnBloodGasDataChangeListener {

	private static final String TAG = "BloodGasFragment";
	private RadioGroup rg_tab;
	private BloodGasFragmentController controller;
	private UIHandler mUIHandler;
	private DatabaseMgr mDatabaseMgr;
	private BloodGasDataFragment mBloodGasDataFragment;
	private BloodGasChartFragment mBloodGasChartFragment;
	private ArrayList<BloodGasBean> mBloodGasList;
	private ArrayList<BloodGasSamplingBean> mBloodGasTimeList;
	private String searchTime = "";
	private ArrayList<String> mCheckedTimeList;
	private UpdateDataAsyncTask mUpdateBloodGasTask;
	private SaveDataAsyncTask mSaveBloodGasTask;
	private SearchDataAsyncTask mSearchAsyncTask;
	private SearchTimeAsyncTask mSearchTimeTask;
	private boolean isEditing = false;
	
	private static final int MSG_CANCEL_SAVE_ASYNCTASK = 100;
	private static final int MSG_CANCEL_SEARCH_ASYNCTASK = 101;
	private static final int MSG_CANCEL_SEARCH_TIME_ASYNCTASK = 102;
	private static final int MSG_CANCEL_UPDATE_BLOODGAS_TASK = 103;
	
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			cancelAsnycTask();
		}else{
			exeSeachBloodGasTask();
			exeSearchSampleTimeTask();	
		}
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cancelAsnycTask();
		releaseList();
		if(null != controller){
			controller.destoryController();
			controller = null;
		}

		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}

	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_bloodgas;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		rg_tab = (RadioGroup)rootView.findViewById(R.id.rg_tab);
		initVariables();
	}

	private void initVariables(){
		mUIHandler = new UIHandler(this);
		mDatabaseMgr = DatabaseMgr.getInstance(getActivity().getApplicationContext());
		controller = BloodGasFragmentController.getInstance(this, R.id.blood_gas_parent_container);
		controller.showFragment(0);	
		mBloodGasDataFragment = (BloodGasDataFragment)controller.getFragment(0);
		mBloodGasChartFragment = (BloodGasChartFragment)controller.getFragment(1);
		mBloodGasList = new ArrayList<BloodGasBean>();
		mCheckedTimeList = new ArrayList<String>();
		mBloodGasTimeList = new ArrayList<BloodGasSamplingBean>();
		searchTime = DateFormatUtil.getNowDate();

	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		if(mBloodGasDataFragment != null){
			mBloodGasDataFragment.setOnBloodGasDataChangeListener(this); 
		
		}
		rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.blood_gas_data_rb:
					controller.showFragment(0);				
					break;
					
				case R.id.blood_gas_chart_rb:
					controller.showFragment(1);					
					break;
					
				default:
					break;
				}
			}
		});		
	}
	
	/**执行查询血气数据**/
	private void exeSeachBloodGasTask(){
		if(null == mSearchAsyncTask){
			mSearchAsyncTask = new SearchDataAsyncTask(this);
			mSearchAsyncTask.execute();
		}
	}
	
	/**执行查询血气采集时间**/
	public void exeSearchSampleTimeTask(){
		if(null == mSearchTimeTask){
			mSearchTimeTask = new SearchTimeAsyncTask();
			mSearchTimeTask.execute();
		}
	}
	
	public void exeUpdateBloodGasTask(BloodGasBean bean){
		if(null == mUpdateBloodGasTask){
			Log.i(TAG, "exeUpdateBloodGasTask");
			mUpdateBloodGasTask = new UpdateDataAsyncTask(this,bean);
			mUpdateBloodGasTask.execute();
		}
	}

	/**保存血气值**/
	@Override
	public void onSaveBloodGas(BloodGasBean bloodGasBean) {
		// TODO Auto-generated method stub	
		boolean compare = false;
		if(mCheckedTimeList.size()>0){
			for(int i= 0; i< mCheckedTimeList.size(); i++){
				String lastSampleTime = mCheckedTimeList.get(i);
				if(lastSampleTime.equals(bloodGasBean.getSampleTime())){					
					if(isEditing){
						//有相同的采集时间的列表里，如果是编辑的就继续udate
						exeUpdateBloodGasTask(bloodGasBean);
						return;
					}else{
						displayToast(R.string.error_sample_time);
						compare = true;
						return;	
					}
			
				}			
			} 
			Log.i(TAG, "-- bloodGasBean.getSampleTime()---" + bloodGasBean.getSampleTime());
			if(!compare){
				mCheckedTimeList.add(bloodGasBean.getSampleTime());				
				if(null == mSaveBloodGasTask){
					mSaveBloodGasTask = new SaveDataAsyncTask(bloodGasBean);
					mSaveBloodGasTask.execute(bloodGasBean);
				}
				
			}

		}else if(mCheckedTimeList.size()==0){
			mCheckedTimeList.add(bloodGasBean.getSampleTime());
			if(null == mSaveBloodGasTask){
				mSaveBloodGasTask = new SaveDataAsyncTask(bloodGasBean);
				mSaveBloodGasTask.execute(bloodGasBean);
			}
		}
	}
	
	/**根据血气采样时间和肝脏id删除血气记录**/
	@Override
	public void onDeleteBloodGas(BloodGasBean bloodGasBean) {
		// TODO Auto-generated method stub
		String sampleTime = bloodGasBean.getSampleTime();
		String liver_Num = PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
				.getStringValue(SharedConstants.LIVER_NUMBER, "");
		Log.i(TAG, "--onDeleteBloodGas---" + "liver_num" + liver_Num + "sampleTime " + sampleTime);
		int result = mDatabaseMgr.deleteBloodGasResult(liver_Num, sampleTime);
		Log.i(TAG, "--onDeleteBloodGas--result-" + result);
		if(result > 0){
			for(int i= 0; i< mCheckedTimeList.size(); i++){
				String lastSampleTime = mCheckedTimeList.get(i);
				if(lastSampleTime.equals(bloodGasBean.getSampleTime())){
					mCheckedTimeList.remove(lastSampleTime);
				}
			}
			exeSeachBloodGasTask();
		}
		
	}


	@Override
	public void onEditBloodGas() {
		// TODO Auto-generated method stub
		isEditing = true;		
	}


	/**采样血气时间列表**/
	private void setBloodGasSampleTimeList(){
		Log.i(TAG, "---setBloodGasSampleTimeList----" + mBloodGasTimeList.size());
		if(null != mBloodGasDataFragment){
			mBloodGasDataFragment.setBloodGasSampleTimeList(mBloodGasTimeList);
		}
	}

	/**图表界面的数据**/
	private void setBloodGasList(ArrayList<BloodGasBean> list){		
		this.mBloodGasList.clear();
		this.mBloodGasList.addAll(list);
		Log.i(TAG, "---setBloodGasList----" + mBloodGasList.size());
		if(null != mBloodGasChartFragment){
			mBloodGasChartFragment.setBloodGasList(mBloodGasList);
		}
	}
	
	/**数据界面的数据**/
	private void setBloodGasListToData(ArrayList<BloodGasBean> list){
		this.mBloodGasList.clear();
		this.mBloodGasList.addAll(list);
		Log.i(TAG, "---setBloodGasListToData----" + mBloodGasList.size());
		if(null != mBloodGasDataFragment){
			mBloodGasDataFragment.setBloodGasList(mBloodGasList);
		}
	}
	
	private void cancelAsnycTask(){
		if(null != mSearchAsyncTask){
			mSearchAsyncTask.cancel(true);
			mSearchAsyncTask = null;
		}
		
		if(null != mSearchTimeTask){
			mSearchTimeTask.cancel(true);
			mSearchTimeTask = null;
		}
		
		if(null != mUpdateBloodGasTask){
			mUpdateBloodGasTask.cancel(true);
			mUpdateBloodGasTask = null;
		}
	}
	
	private void releaseList(){
		if(null != mCheckedTimeList){
			mCheckedTimeList.clear();
			mCheckedTimeList = null;
		}
		
		if(null != mBloodGasList){
			mBloodGasList.clear();
			mBloodGasList = null;
		}
		
		if(null != mBloodGasTimeList){
			mBloodGasTimeList.clear();
			mBloodGasTimeList = null;
		}
	}
	
	private class SaveDataAsyncTask extends AsyncTask<BloodGasBean, Integer, Long>{

		private BloodGasBean mBloodGasBean;
		
		public SaveDataAsyncTask(BloodGasBean bloodGasBean){
			this.mBloodGasBean = bloodGasBean;
		}
		
		@Override
		protected Long doInBackground(BloodGasBean... params) {
			// TODO Auto-generated method stub
			long result = mDatabaseMgr.saveBloodGasResult(mBloodGasBean);
			Log.i(TAG, "SaveDataAsyncTask result----" + result);
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//setBloodGasList();
			if(result > 0){
				//发送消息，取消保存的异步，新建更新列表异步
				Message msg = Message.obtain();
				msg.what = MSG_CANCEL_SAVE_ASYNCTASK;	
				mUIHandler.sendMessage(msg);				
				exeSeachBloodGasTask();
			}			
		}		
	}
	
	/**更新血气记录**/
	private static class UpdateDataAsyncTask extends AsyncTask<BloodGasBean, Integer, Long>{

		private BloodGasBean mBloodGasBean;
		private WeakReference<BloodGasFragment> weakfrag;
		
        public UpdateDataAsyncTask(BloodGasFragment fragment,BloodGasBean bloodGasBean){
        	weakfrag = new WeakReference<BloodGasFragment>(fragment);
        	this.mBloodGasBean = bloodGasBean;
        }

		@Override
		protected Long doInBackground(BloodGasBean... params) {
			// TODO Auto-generated method stub
			long result = -1;
			BloodGasFragment fragment = weakfrag.get();
			if(null != fragment){
				String sampleTime = mBloodGasBean.getSampleTime();
				String liver_Num = PreferenceUtil.getInstance(fragment.getBaseActivity().getApplicationContext())
						.getStringValue(SharedConstants.LIVER_NUMBER, "");
				result = fragment.mDatabaseMgr.updateBloodGasResult(liver_Num, sampleTime, mBloodGasBean);	
				Log.i(TAG, "UpdateDataAsyncTask result----" + result);
			}			
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			BloodGasFragment fragment = weakfrag.get();
			if(null != fragment){
				fragment.isEditing = false;
				fragment.mBloodGasDataFragment.setSampleTimeEnable(true);
				//发送消息，取消保存的异步，新建更新列表异步
				Message msg = Message.obtain();
				msg.what = MSG_CANCEL_UPDATE_BLOODGAS_TASK;	
				fragment.mUIHandler.sendMessage(msg);				
				fragment.exeSeachBloodGasTask();
			}						
		}		
	}
	
	/**查询采样的时间**/
	private class SearchTimeAsyncTask extends AsyncTask<Void, Integer, ArrayList<BloodGasSamplingBean>>{
				
        
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();	
			mBloodGasTimeList.clear();
			
		}

		@Override
		protected ArrayList<BloodGasSamplingBean> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			String liverNum = PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.getStringValue(SharedConstants.LIVER_NUMBER, "");
			Log.i(TAG, "SearchTimeAsyncTask----" + "liverNum----" + liverNum + "  searchTime " + searchTime);
			mBloodGasTimeList = DatabaseMgr.getInstance(getActivity().getApplicationContext())
					.getBloodGasSamplingTimes(liverNum,searchTime);
			Log.i(TAG, "mSamplingTimeList----" + mBloodGasTimeList.size() );
			return mBloodGasTimeList;
		}

		@Override
		protected void onPostExecute(ArrayList<BloodGasSamplingBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setBloodGasSampleTimeList();	
			mUIHandler.sendEmptyMessage(MSG_CANCEL_SEARCH_TIME_ASYNCTASK);
		}	
		
	}
	
	/**
	 * 根据灌注的肝脏id和灌注的开始时间进行搜索
	 * @author 80657
	 */	
	static class SearchDataAsyncTask extends AsyncTask<Void, Integer, ArrayList<BloodGasBean>>{
				
		private WeakReference<BloodGasFragment> weakFragment;
		private ArrayList<BloodGasBean> resultList;
		
		public SearchDataAsyncTask(BloodGasFragment fragment){
			weakFragment = new WeakReference<BloodGasFragment>(fragment);
			resultList = new ArrayList<BloodGasBean>();
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();	
			BloodGasFragment fragment = weakFragment.get();
			if(null != fragment){
				fragment.mBloodGasList.clear();
				resultList.clear();
			}else{
				return;
			}
		}

		//后台进程的执行
		@Override
		protected ArrayList<BloodGasBean> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			BloodGasFragment fragment = weakFragment.get();
			if(null != fragment){
				String liverNum = PreferenceUtil.getInstance(fragment.getActivity().getApplicationContext())
						.getStringValue(SharedConstants.LIVER_NUMBER, "");
				Log.i(TAG, "SearchDataAsyncTask----liverNum----" + liverNum + "  searchTime " + fragment.searchTime);
				resultList = DatabaseMgr.getInstance(fragment.getActivity().getApplicationContext())
						.getBloodGasData(liverNum,fragment.searchTime);
				return resultList;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<BloodGasBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//hideProgressDialog();			
			
			BloodGasFragment fragment = weakFragment.get();
			if(null != fragment){
				fragment.setBloodGasList(result);
				fragment.setBloodGasListToData(result);
				fragment.mUIHandler.sendEmptyMessage(MSG_CANCEL_SEARCH_ASYNCTASK);
			}			
		}			
	}	

	public static class UIHandler extends Handler{
		
		WeakReference<BloodGasFragment> mFragReference;
		
		public UIHandler(BloodGasFragment mfragment){
			mFragReference = new WeakReference<BloodGasFragment>(mfragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final BloodGasFragment mFragment = mFragReference.get();
			if(null == mFragment){
				return;
			}
			switch (msg.what) {
			case MSG_CANCEL_SAVE_ASYNCTASK:	
				Log.i(TAG, "--------MSG_CANCEL_SAVE_ASYNCTASK-----");
				if(null != mFragment.mSaveBloodGasTask){
					mFragment.mSaveBloodGasTask.cancel(true);
					mFragment.mSaveBloodGasTask = null;
				}
				
				break;
			
			case MSG_CANCEL_SEARCH_ASYNCTASK:
				Log.i(TAG, "--------MSG_CANCEL_SEARCH_ASYNCTASK-----");
				if(null != mFragment.mSearchAsyncTask){
					mFragment.mSearchAsyncTask.cancel(true);
					mFragment.mSearchAsyncTask = null;
				}
				break;
				
			case MSG_CANCEL_SEARCH_TIME_ASYNCTASK:
				Log.i(TAG, "--------MSG_CANCEL_SEARCH_TIME_ASYNCTASK-----");
				if(null != mFragment.mSearchTimeTask){
					mFragment.mSearchTimeTask.cancel(true);
					mFragment.mSearchTimeTask = null;
				}
				break;
				
			case MSG_CANCEL_UPDATE_BLOODGAS_TASK:
				if(null != mFragment.mUpdateBloodGasTask){
					mFragment.mUpdateBloodGasTask.cancel(true);
					mFragment.mUpdateBloodGasTask = null;
				}
				break;
			default:
				break;
			}
		}
	}

}
