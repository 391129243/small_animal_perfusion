package com.gidi.bio_console.alarm.model;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gidi.bio_console.bean.AlarmMsgEnity;
import com.gidi.bio_console.db.DatabaseMgr;


/**model用于实际的处理数据**/
public class AlarmLogModelImpl implements IAlarmLogModel{

	private IOnLoadAlarmLogListener mListener;
	private ArrayList<AlarmMsgEnity> mList;
	private Context mContext;
	private AlarmLogAsyncTask mAsyncTask;
	private String searchTime;
	
	public AlarmLogModelImpl(Context context,IOnLoadAlarmLogListener listener){
		this.mContext = context;
		this.mListener = listener;
		this.mList = new ArrayList<AlarmMsgEnity>();
	}

	@Override
	public void searchAlarmLog(String searchtime) {
		// TODO Auto-generated method stub
		Log.i("ggg", "---searchAlarmLog---" + searchTime);
		this.searchTime = searchtime;
		if(mAsyncTask != null){
			mAsyncTask.cancel(true);		
		}
		mAsyncTask = new AlarmLogAsyncTask();
		mAsyncTask.execute();
		
	}
	
	public void destroy(){
		//取消所有的线程操作释放对象
		mContext = null;
		if(null != mAsyncTask){
			mAsyncTask.cancel(true);
			mAsyncTask = null;
		}		
		if(null != mList){
			mList.clear();
			mList = null;
		}
	}

	class AlarmLogAsyncTask extends AsyncTask<Void, Integer, ArrayList<AlarmMsgEnity>>{

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList.clear();
		}

		//后台进程的执行
		@Override
		protected ArrayList<AlarmMsgEnity> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			mList = DatabaseMgr.getInstance(mContext.getApplicationContext()).getAlarmMsgFromTime(searchTime);
			return mList;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
		}
		
		
		@Override
		protected void onPostExecute(ArrayList<AlarmMsgEnity> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(null != mListener){
				if(result.size()>0){
					mListener.success(result);
				}
				
			}
			
		}	
		
	}
}
