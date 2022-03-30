package com.gidi.bio_console.kidney;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.bean.KidneyInfoBean;
import com.gidi.bio_console.bean.KidneyPerfusionLogBean;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseHelper;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.ExcelUtils;
import com.gidi.bio_console.utils.FileUtils;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.ProgressDialogUtil;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.DateTimeView;


public class KidneyPerfusionLogActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "PerfusionLogActivity";
	private Context mContext;
	private UIHandler mUIHandler;
	private RelativeLayout mTitleLayout;
	private TextView mTitleTxt;
	private DateTimeView mSystimeTxt;
	private TextView mUsbStateTxt;
	private ImageView mTitleImg;
	private ImageView mBackImg;
	private KidneyPerfusionLogAdapter mAdapter;
	private TextView mStartDateTxt;
	private TextView mEndDateTxt;
	private ListView mRecordListView;
	private TextView mSearchTxt;
	private TextView mDeleteTxt;
	private TextView mOutputTxt;
	private LogAsyncTask mSearchTask;
	private ExportAsycTask mExportTask;
	private DeleteAsycTask mDeleteAsycTask;
	private CustomDialog mExportDialog;
	private CustomDialog mDeleteDialog;
	private Dialog mProgressDialog;
	private CustomDialog mDatePickerDialog;
	private ArrayList<KidneyPerfusionLogBean> mList;
	private ArrayList<KidneyPerfusionLogBean> mCheckedList;
	private File UdiskDir;
	private Calendar mCalendar;
	private String startDate;
	private String endDate;
	private String[] excelTitle;
	private int startYear,startMonth,startDay;
	private int endYear,endMonth,endDay;
	private boolean isFirstSearch = true;
	private static final String RECORD_PERFUSION_DIR = "Perfusion_Record/";
	private String usb_path = "";
	
	private static final int MSG_EXPORT_RECORD = 500;//导出数据
	private static final int MSG_EXPORT_SUCCESS = 501;//导出成功
	private static final int MSG_EXPORT_FAIL = 502;//导出失败
	private static final int MSG_DELETE_RECORD = 503;//删除记录
	private static final int MSG_DELETE_SUCCESS = 504;//删除成功
	private static final int MSG_DELETE_FAIL = 505;	//删除失败
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		initData();	
		registerReceiver();			
		initUpdateSysTime();
	}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean is_usb_plung = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.IS_USB_PLUNG, false);
		if(is_usb_plung){
			mUsbStateTxt.setText(getString(R.string.usb_state) + "  " + getString(R.string.hint_usb_plung));
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver();	
		cancelDialog();
		clearList();
		releaseTask();
		mContext = null;
		mAdapter = null;
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);	
			mUIHandler = null;
			runnable = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy");					
	}


	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_kidney_perfusion_log;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mUsbStateTxt = (TextView)findViewById(R.id.usb_state_txt);
		mTitleLayout = (RelativeLayout)findViewById(R.id.perfusion_log_guide_title_layout);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text_s);	
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon_s);
		mBackImg = (ImageView)findViewById(R.id.title_back_img);
		mTitleImg.setImageResource(R.drawable.function_title_output_img);
		mTitleTxt.setText(R.string.record_pufersion_history_title);
		mSystimeTxt = (DateTimeView)mTitleLayout.findViewById(R.id.base_systime_txt_s);	
		mStartDateTxt = (TextView)findViewById(R.id.record_history_query_start_date);
		mEndDateTxt = (TextView)findViewById(R.id.record_history_query_end_date);
		mSearchTxt = (TextView)findViewById(R.id.record_history_search_txt);
		mDeleteTxt = (TextView)findViewById(R.id.record_history_delete_txt);
		mOutputTxt = (TextView)findViewById(R.id.record_history_output_txt);
		mRecordListView = (ListView)findViewById(R.id.record_history_log_listview);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub.
		mBackImg.setOnClickListener(this);
		mStartDateTxt.setOnClickListener(this);
		mEndDateTxt.setOnClickListener(this);
		mSearchTxt.setOnClickListener(this);
		mDeleteTxt.setOnClickListener(this);
		mOutputTxt.setOnClickListener(this);
		
		mRecordListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				KidneyPerfusionLogBean bean = mList.get(position);
				boolean isCheck = bean.isCheck();
				
				if(isCheck){
					bean.ischeck = false;
				}else{
					bean.ischeck = true;
				}
				mAdapter.setPerfusionLogList(mList);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		//当前的日期		
		mContext = this;
		mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"),Locale.CHINA);
		mUIHandler = new UIHandler(this);

		mList = new ArrayList<KidneyPerfusionLogBean>();
		mCheckedList = new ArrayList<KidneyPerfusionLogBean>();
		mAdapter = new KidneyPerfusionLogAdapter(getApplicationContext());
		mRecordListView.setAdapter(mAdapter);
		initExcelTitle();
		searchPerfusionLog();
		if(isCreateFileInUdik(RECORD_PERFUSION_DIR)){
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_USB_PLUNG, true);
			mUsbStateTxt.setText(getString(R.string.usb_state) + "  " + getString(R.string.hint_usb_plung));
		}else{
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_USB_PLUNG, false);
			mUsbStateTxt.setText(getString(R.string.usb_state) + "  " + getString(R.string.toast_usb_nofound));
		}
	}
	
	/****
	 * 初始化导出excel的标题
	 */
	private void initExcelTitle(){
		excelTitle = new String[]{DatabaseHelper.COLUMN_KIDNEY_NUM, 
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PDIAS, 
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PSYST, 
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PFREQ, 
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PMEAN,
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_FREAL,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PDIAS,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PSYST,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PFREQ,				 
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PMEAN,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_FREAL,
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_SPEED,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_SPEED,
				 DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_RESISTINDEX,
				 DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_RESISTINDEX,
				 DatabaseHelper.KIDNEY_INFO_TEMP,
				 DatabaseHelper.KIDNEY_INFO_TIME};
	}
	
	//定时更新时间
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
	
	/**
	 * 更新显示的查询日期
	 * @param type
	 * @param year
	 * @param month
	 * @param day
	 */
	private void updateDate(int type,final int year, final int month, final int day){
		if(type == 0){
			//startDate
			startYear = year;
			startMonth = month + 1;
			startDay = day;
			mStartDateTxt.setText((String.format("%04d-%02d-%02d",startYear,startMonth,startDay)));
		}else if(type == 1){
			//endDa结束时间
			//结束的时间不能小于开始的时间
			int end_month = month + 1;
			if(year < startYear){
				endYear = startYear;
				endMonth = startMonth;
				endDay = startDay;
				displayToast(R.string.toast_endtime_not_earlier_than_begin);
			}else{
				if(end_month < startMonth){
					endMonth = startMonth;
					endDay = startDay;
					displayToast(R.string.toast_endtime_not_earlier_than_begin);
				}else{
					if(day < startDay){
						endDay = startDay;
						displayToast(R.string.toast_endtime_not_earlier_than_begin);
					}else{
						endYear = year;
						endMonth = month+1;
						endDay = day;
					}
				}
			}
			mEndDateTxt.setText((String.format("%04d-%02d-%02d",endYear,endMonth,endDay)));
			
		}
	}
	
	/**
	 * seach perfusion Log
	 */
	private void searchPerfusionLog(){
		if(null == mSearchTask){
			mSearchTask = new LogAsyncTask(this);
			mSearchTask.execute();
		}		
	}
	
	/**导出数据**/
	private void exportRecord(){
		//判断当前有无接U盘等外设设备
		
		if(mList.size() >0){
			mCheckedList.clear();
			for(KidneyPerfusionLogBean bean :mList){
				if(bean.ischeck){
					mCheckedList.add(bean);
				}
			}		
			isCreateFileInUdik(RECORD_PERFUSION_DIR);
			Log.i(TAG, "exportRecord--" + usb_path);
			boolean isFileExit = FileUtils.isExistUdisk(usb_path);
			Log.i(TAG, "exportRecord isFileExit--" + isFileExit);
			if(isFileExit){
				//新建导出目录文件
				UdiskDir = FileUtils.createUdiskDir(usb_path,RECORD_PERFUSION_DIR);
				if(UdiskDir.exists()){
					Log.i(TAG, "create record dir");
					//开启线程导出数据 以时间为excel文件名称，表名称以灌注时间和perfusion ID
					exePerfusionInfoList();
				}else{
					displayToast(R.string.toast_fail_create_exportdir);
					updateLogList();
					return;
				}
			}else{
				displayToast(R.string.toast_usb_nofound);
				updateLogList();
				return;
			}
			
		}

	}
	
	/**删除数据
	 * mList:选中的灌注记录
	 * **/
	private void deletePerfusionLog(){
		if(mList.size()>0){
			mCheckedList.clear();
			for(KidneyPerfusionLogBean bean :mList){
				if(bean.ischeck){
					mCheckedList.add(bean);
				}
			}
			if(mDeleteAsycTask == null){
				mDeleteAsycTask = new DeleteAsycTask(this);
				mDeleteAsycTask.execute();
			}
					
		}
	}
	
	public void exePerfusionInfoList() {
		if(mExportTask == null){
			mExportTask = new ExportAsycTask();
			mExportTask.execute();
		}
		
	}
	
	public String getUPath(Context context){
		StorageManager mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
	   	Class<?> volumeInfoClazz = null;
	    Method getVolumes = null;
	    Method isMountedReadable = null;
	    Method getType = null;
	   	Method getPath = null;
	    List<?> volumes = null;
	    try {
	      	volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
	       	getVolumes = StorageManager.class.getMethod("getVolumes");
	       	isMountedReadable = volumeInfoClazz.getMethod("isMountedReadable");
	       
	       	getType = volumeInfoClazz.getMethod("getType");
	       	getPath = volumeInfoClazz.getMethod("getPath");
	        volumes = (List<?>) getVolumes.invoke(mStorageManager);
	        if (volumes.size()==0){
	        	return null;
	        }
	        for (Object vol : volumes) {
	        	
	          	if (vol != null && (Boolean)isMountedReadable.invoke(vol) && (Integer) getType.invoke(vol) == 0) {
	             	File path2 = (File) getPath.invoke(vol);
	                String p2 = path2.getPath();
	                Log.i("zbh","找到U盘路径:%s\n"+p2);
	                return p2;
	            }
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}
	
	/***
	 * 在U盘中创建文件夹
	 * @param dirName
	 * @return
	 */
	private boolean isCreateFileInUdik(String dirName){
		boolean result = false;
		usb_path = getUPath(mContext)+"/";
		Log.i(TAG, "isCreateFileInUdik" + usb_path);
		File dir = new File(usb_path + dirName);
		if (!dir.exists()|| !dir.isDirectory()) {  
			result = dir.mkdirs();   
        }else{
        	result = true;
        }
		Log.i(TAG, "isCreateFileInUdik" + result);
		return result;
	}
	
	private String getDateFileName(String date){
		String fileName = "";
		SimpleDateFormat  fullsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);  
		SimpleDateFormat  format = new SimpleDateFormat("yyyyMMdd-HH-mm-ss",Locale.CHINA);   
		try {
			Date timeDate = fullsdf.parse(date) ;
			fileName = format.format(timeDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}
	
	/**获取灌注记录**/
	private boolean  getExcelFile(File savepath,String fileName,ArrayList<KidneyInfoBean> msgList){
		ExcelUtils eu = new ExcelUtils();
		boolean result = false;
		Log.i(TAG, " fileName----" + fileName);
		eu.createExcel(savepath, fileName);
		//Log.i(TAG, " path----" + path);
		ArrayList<String[]> contentList = new ArrayList<String[]>();
		contentList = getContentList(msgList);
		Log.i(TAG, " contentList----" + contentList.size());		
		result = eu.saveDataToExcel(excelTitle, contentList);			
		return result;
	}
	
	
	
	/**
	 * 将灌注的串口数据转化为字符串数组格式
	 * **/
	private ArrayList<String[]> getContentList(ArrayList<KidneyInfoBean> msglist){
		ArrayList<String[]>contentList = new ArrayList<String[]>();
		for(int i = 0;i<msglist.size();i++){
			KidneyInfoBean kidneyInfoBean = msglist.get(i);
			String[]content = new String[excelTitle.length];
			content[0] = ""+kidneyInfoBean.getKidneyNum();
			content[1] = ""+kidneyInfoBean.getLeftKidneyArtDiasPre();
			content[2] = ""+kidneyInfoBean.getLeftKidneyArtSystPre();
			content[3] = ""+kidneyInfoBean.getLeftKidneyArtFreqPre();
			content[4] = ""+kidneyInfoBean.getLeftKidneyArtMeanPre();
			content[5] = ""+kidneyInfoBean.getLeftKidneyArtFreal();
			content[6] = ""+kidneyInfoBean.getRightKidneyArtDiasPre();
			content[7] = ""+kidneyInfoBean.getRightKidneyArtSystPre();
			content[8] = ""+kidneyInfoBean.getRightKidneyArtFreqPre();
			content[9] = ""+ kidneyInfoBean.getRightKidneyArtPmean();
			content[10] = ""+ kidneyInfoBean.getRightKidneyArtFreal();
			content[11] = "" + kidneyInfoBean.getLeftKidneyArtSpeed();
			content[12] = "" + kidneyInfoBean.getRightKidneyArtSpeed();
			content[13] = "" + kidneyInfoBean.getLeftKidneyArtResistIndex();
			content[14] = "" + kidneyInfoBean.getRightKidneyArtResistIndex();
			content[15] = "" + kidneyInfoBean.getTemp();
			content[16] = "" + kidneyInfoBean.getMsgTime();
			contentList.add(content);
		}
		return contentList;
	}
	
	

	/**进度对话框，可提示导出中等**/
	private void showProgressDialog(String title, String message){
		 if(null == mProgressDialog){
			 mProgressDialog = ProgressDialogUtil.createLoadingDialog(mContext, message);
	         mProgressDialog.setCancelable(false);
	         mProgressDialog.setCanceledOnTouchOutside(true);
	         mProgressDialog.show();
		 }	
	}
	
	/*
	 * 隐藏提示加载
	 */
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	/***设置开始时间**/
	private void startTimeChoice(){
		String endDate= mStartDateTxt.getText().toString().trim();
		startYear = DateFormatUtil.getYear(endDate);
		startMonth = DateFormatUtil.getMonth(endDate);
		startDay = DateFormatUtil.getDay(endDate);
		showDatePickerDialog(0,startYear,startMonth,startDay);
	}
	
	private void endTimeChoice(){
		String endDate= mEndDateTxt.getText().toString().trim();
		endYear = DateFormatUtil.getYear(endDate);
		endMonth = DateFormatUtil.getMonth(endDate);
		endDay = DateFormatUtil.getDay(endDate);
		showDatePickerDialog(1,endYear,endMonth,endDay);
	}
	
	private void registerReceiver(){
		detectUsbWithBroadcast();
	}
	
	private void unregisterReceiver(){
		unregisterReceiver(mUsbReceiver);
		mUsbReceiver = null;
	}
	
	private void detectUsbWithBroadcast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mUsbReceiver, filter);
	}
	
		
	/**导出成功后清空选中的列表**/
	private void updateLogList(){
		if(null != mCheckedList){
			mCheckedList.clear();
		}
		if(null != mList){
			for(KidneyPerfusionLogBean bean : mList){
				bean.ischeck = false;
			}
			mAdapter.setPerfusionLogList(mList);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "action---" + action);
			if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
				PreferenceUtil.getInstance(getApplicationContext())
					.setValueByName(SharedConstants.IS_USB_PLUNG, true);
				mUsbStateTxt.setText(getString(R.string.usb_state) + "  " + getString(R.string.hint_usb_plung));
				
				Log.i(TAG, "action---" + usb_path);
			}else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
				PreferenceUtil.getInstance(getApplicationContext())
					.setValueByName(SharedConstants.IS_USB_PLUNG, false);
				mUsbStateTxt.setText(getString(R.string.usb_state) + "  " +getString(R.string.hint_usb_detached));
			}
		}
	};


	
	/**
	 * 查询灌注记录的线程
	 * @author 80657
	 */
	static class LogAsyncTask extends AsyncTask<Void, Integer, ArrayList<KidneyPerfusionLogBean>>{
		
		private WeakReference<KidneyPerfusionLogActivity> weakAty;
		
        public LogAsyncTask(KidneyPerfusionLogActivity activity){
            weakAty = new WeakReference<KidneyPerfusionLogActivity>(activity);
        }
        
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();			
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(null != mActivity){
				if(null != mActivity.mList){
					mActivity.mList.clear();	
				}	
			}else{
				return;
			}			
		}

		//后台进程的执行
		@Override
		protected ArrayList<KidneyPerfusionLogBean> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(null != mActivity){
				if(mActivity.isFirstSearch){
					mActivity.isFirstSearch = false;
	            	//查询所有的记录
					mActivity.mList = DatabaseMgr.getInstance(mActivity.getApplicationContext()).getAllKidneyPerfusionLog();
		            
				}else{
					mActivity.startDate = mActivity.mStartDateTxt.getText().toString().trim();
					mActivity.endDate = mActivity.mEndDateTxt.getText().toString().trim();
					
					mActivity.mList = DatabaseMgr.getInstance(mActivity.getApplicationContext())
							.getKidneyPerfusionLogFromName(mActivity.startDate, mActivity.endDate);
					Log.i(TAG, " --startDate--" + mActivity.startDate + "---endDate ---" + mActivity.endDate + " "+ mActivity.mList.size());
				}			
				return mActivity.mList;
			}else{
				return null;
			}
            
		}

		
		@Override
		protected void onPostExecute(ArrayList<KidneyPerfusionLogBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(null != mActivity){
				if(null != mActivity.mAdapter){
					mActivity.mAdapter.setPerfusionLogList(mActivity.mList);
					mActivity.mAdapter.notifyDataSetChanged();
				}
				if(null != mActivity.mSearchTask){
					mActivity.mSearchTask .cancel(true);
					mActivity.mSearchTask = null;
				}
				if(null != mActivity.mList && mActivity.mList.size() > 0){
					mActivity.mStartDateTxt.setText(DateFormatUtil.getDate(mActivity.mList.get(0).getStartTime()));
					mActivity.mEndDateTxt.setText(DateFormatUtil.getDate(mActivity.mList.get(mActivity.mList.size()-1).getStartTime()));
	            }else{
	            	mActivity.mStartDateTxt.setText(String.format("%04d-%02d-%02d", mActivity.mCalendar.get(Calendar.YEAR)
	            			,mActivity.mCalendar.get(Calendar.MONTH) +1, mActivity.mCalendar.get(Calendar.DAY_OF_MONTH)));
	            	mActivity.mEndDateTxt.setText(String.format("%04d-%02d-%02d",mActivity.mCalendar.get(Calendar.YEAR)
	            			,mActivity.mCalendar.get(Calendar.MONTH) +1, mActivity.mCalendar.get(Calendar.DAY_OF_MONTH)));
	            }
				if(null != mActivity.mSearchTask){
					mActivity.mSearchTask.cancel(true);
					mActivity.mSearchTask = null;
				}
			}
		}					
	}
	
	/**导出文件的线程**/
	class ExportAsycTask extends AsyncTask<Void,Integer, Long>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();			
			showProgressDialog(mContext.getString(R.string.prompt),
						mContext.getString(R.string.dialog_export_record));						
		}

		//后台进程的执行
		@Override
		protected Long doInBackground(Void... arg0) {
			// TODO Auto-generated method stub	
			long result = 0;
			if(isCancelled()){
                return result; 
            }			
			ArrayList<KidneyInfoBean> mExportList = new ArrayList<KidneyInfoBean>();
			
			mExportList.clear();
			
			if(mCheckedList.size() > 0){
				for(int i = 0; i< mCheckedList.size(); i++){
					String kidneyNum = mCheckedList.get(i).getKidneyName();
					String startTime = mCheckedList.get(i).getStartTime();
					String searchTime = DateFormatUtil.getDate(startTime);
					Log.i(TAG, " ----" + startTime);
					//根据灌注信息
					mExportList = DatabaseMgr.getInstance(getApplicationContext())
							.getKidneyPerfusionFromStarttime(kidneyNum, startTime);
					//导出血气数据
					
					if(UdiskDir.isDirectory()){
						Log.i(TAG, " mList1----" + mList.size());
						//给每个导出的数据一个文件夹，方便日后导出其他类型的数据
						File exportDir = FileUtils.createDir(UdiskDir,
								getDateFileName(startTime) + "-" + kidneyNum);
						if(exportDir.isDirectory()){
							String fileName = kidneyNum + "_" +getDateFileName(startTime)+ FileUtils.EXCEL_SUFFIX;	
							boolean save_result = getExcelFile(exportDir, fileName, mExportList);
							result++;
							Log.i(TAG, " save_result----" + save_result);
						}						
					}
				}
			}
			if(null != mExportList){
				mExportList.clear();
				mExportList = null;
			}
									    		
			return result;
		}

		
		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//导出成功
			if(result > 0){
				mUIHandler.sendEmptyMessageDelayed(MSG_EXPORT_SUCCESS, 10000);
			}else{
				mUIHandler.sendEmptyMessageDelayed(MSG_EXPORT_FAIL, 10000);
			}
			
		}					
	}
	
	/**算出灌注数据和记录的线程**/
	static class  DeleteAsycTask extends AsyncTask<Void,Integer, Integer>{
		int del_log_result = -1; 
		private WeakReference<KidneyPerfusionLogActivity> weakAty;
        public DeleteAsycTask(KidneyPerfusionLogActivity activity){
            weakAty = new WeakReference<KidneyPerfusionLogActivity>(activity);
        }

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(null != mActivity){
				mActivity.showProgressDialog(mActivity.getString(R.string.prompt),
						mActivity.getString(R.string.dialog_delete_record));
			}								
		}

		//后台进程的执行
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			int result = 0;
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(null != mActivity){
				if(mActivity.mCheckedList.size() > 0){
					for(int i = 0; i< mActivity.mCheckedList.size(); i++){
						String kidneyNum = mActivity.mCheckedList.get(i).getKidneyName();
						String startTime = mActivity.mCheckedList.get(i).getStartTime();
						Log.i(TAG, " DeleteAsycTask startTime----" + startTime);
						//根据灌注信息
						result = DatabaseMgr.getInstance(mActivity.getApplicationContext())
								.deleteKidneyPerfusion(kidneyNum, startTime);
						//删除灌注记录中选中的
						del_log_result = DatabaseMgr.getInstance(mActivity.getApplicationContext())
								.deleteKidneyPerfusionLog(kidneyNum);
						 
					}
				}        
			}
			  		
			return result;
		}

		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//导出成功
			KidneyPerfusionLogActivity mActivity = weakAty.get();
			if(mActivity != null){
				if(result > 0 && del_log_result >0){
					Log.i(TAG, " -delete---" + result);
					mActivity.mUIHandler.sendEmptyMessageDelayed(MSG_DELETE_SUCCESS, 4000);
				}else{
					Log.i(TAG, " -delete- fail--" + result);
					mActivity.mUIHandler.sendEmptyMessageDelayed(MSG_DELETE_FAIL, 4000);
				}
			}			
		}					
	}
	
	public static class UIHandler extends Handler{
		
		WeakReference<KidneyPerfusionLogActivity> mActivityReference;
			
		public UIHandler(KidneyPerfusionLogActivity mActivity){
			mActivityReference = new WeakReference<KidneyPerfusionLogActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final KidneyPerfusionLogActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			switch (msg.what) {
	
			case MSG_EXPORT_RECORD:
				mActivity.exportRecord();
				break;
				
			case MSG_DELETE_RECORD:
				//删除灌注记录
				mActivity.deletePerfusionLog();
				break;
			
			case Constants.MSG_UPDATE_TIME:
				String sysTime = DateFormatUtil.getNowTime();
				String sysDate = DateFormatUtil.getNowDate();
				mActivity.mSystimeTxt.setDateText(sysDate);
				mActivity.mSystimeTxt.setTimeText(sysTime);
				break;
				
			case MSG_EXPORT_SUCCESS:
				mActivity.hideProgressDialog();
				mActivity.displayToast(R.string.toast_export_record_success);				
				if(null != mActivity.mExportTask){
					mActivity.mExportTask.cancel(true);
					mActivity.mExportTask = null;
				}
				mActivity.updateLogList();
				Log.i(TAG, "mActivity.mExportTask--" + mActivity.mExportTask);
				break;
			
			case MSG_EXPORT_FAIL:
				mActivity.hideProgressDialog();
				mActivity.displayToast(R.string.toast_export_record_fail);
				if(null != mActivity.mExportTask){
					mActivity.mExportTask.cancel(true);
					mActivity.mExportTask = null;
				}
				mActivity.updateLogList();
				
				break;
			case MSG_DELETE_SUCCESS:
				mActivity.hideProgressDialog();
				mActivity.displayToast(R.string.toast_delete_record_success);
				mActivity.isFirstSearch = true;
				mActivity.searchPerfusionLog();
				if(null != mActivity.mDeleteAsycTask){
					mActivity.mDeleteAsycTask.cancel(true);
					mActivity.mDeleteAsycTask = null;
				}
				if(null!= mActivity.mCheckedList){
					mActivity.mCheckedList.clear();
				}
				break;
			
			case MSG_DELETE_FAIL:
				mActivity.hideProgressDialog();
				mActivity.displayToast(R.string.toast_delete_record_fail);
				mActivity.updateLogList();
				if(null != mActivity.mDeleteAsycTask){
					mActivity.mDeleteAsycTask.cancel(true);
					mActivity.mDeleteAsycTask = null;
				}
				break;	

				
			default:
				break;
			}
		}				
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.record_history_query_start_date:
			startTimeChoice();
			break;

		case R.id.record_history_query_end_date:
			//选择时间
			endTimeChoice();			
			break;
			
		case R.id.record_history_search_txt:
//			//点击查询时判断起始时间和结束时间，如果起始时间晚于结束时间则提示
			String startdate = mStartDateTxt.getText().toString().trim();
			String enddate = mEndDateTxt.getText().toString().trim();
			if(DateFormatUtil.compare_date(startdate, enddate)){
				searchPerfusionLog();
			}else{
				//提示
				displayToast(R.string.toast_endtime_not_earlier_than_begin);
			}			
			break;
		case R.id.record_history_delete_txt:
			//删除相关的灌注记录
			showDeleteDialog();
			break;
			
		case R.id.record_history_output_txt:
			//弹出确认是否导出的提示
			showConfirmDialog();
			break;
		case R.id.title_back_img:
			 this.finish();
			 overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			break;
		default:
			break;
		}
	}
	
	private void showDatePickerDialog(int type,int year, int month, int dayOfMonth){
		final int mType = type;
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
					 Calendar calendar = Calendar.getInstance();
					 calendar.set(year, month, day);					
				}
			});
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(pickView);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_choice_date));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Log.i(TAG, "Year--" + datePicker.getYear() + " month---" + datePicker.getMonth() + "day---" + datePicker.getDayOfMonth());
					updateDate(mType,datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
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
	
	/**
	 * 删除提示对话框
	 **/
	private void showDeleteDialog(){
		if(null != mDeleteDialog){
			mDeleteDialog.dismiss();
			mDeleteDialog = null;
		}
		
		if(null == mDeleteDialog){
			mDeleteDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.record_perfusion_delete_hint);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.prompt));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mUIHandler.sendEmptyMessage(MSG_DELETE_RECORD);
					mDeleteDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mDeleteDialog.dismiss();
					mDeleteDialog = null;
				}
			});
			mDeleteDialog = builder.create();
			mDeleteDialog.show();
		}
		
	}
	
	private void showConfirmDialog(){
		if(null != mExportDialog){
			mExportDialog.dismiss();
			mExportDialog = null;
		}
		if(null == mExportDialog){
			mExportDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.record_perfusion_export_hint);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.prompt));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mUIHandler.sendEmptyMessage(MSG_EXPORT_RECORD);
					mExportDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mExportDialog.dismiss();
					mExportDialog = null;
				}
			});
			mExportDialog = builder.create();
			mExportDialog.show();
		}
	}
	
	private void cancelDialog(){
		if(null != mExportDialog){
			mExportDialog.dismiss();
			mExportDialog = null;
		}
		
		if(null != mDatePickerDialog){
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		
		if(null != mProgressDialog){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		
		if(mDeleteDialog != null){
			mDeleteDialog.dismiss();
			mDeleteDialog = null;
		}
	}
	
	private void clearList(){
		if(null != mList){
			mList.clear();
			mList = null;
		}
		
		if(null != mCheckedList){
			mCheckedList.clear();
			mCheckedList = null;
		}
		
	}
	
	private void releaseTask(){
		if(mExportTask != null){
			mExportTask.cancel(true);
			mExportTask = null;
		}
		
		if(mSearchTask != null){
			mSearchTask.cancel(true);
			mSearchTask = null;
		}
		
		if(mDeleteAsycTask != null){
			mDeleteAsycTask.cancel(true);
			mDeleteAsycTask = null;
		}
		Log.i(TAG, "onstop mExportTask" + mExportTask);	
	}

}
