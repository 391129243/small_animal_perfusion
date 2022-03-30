package com.gidi.bio_console.alarm.presenter;

import java.util.ArrayList;


import android.content.Context;
import android.util.Log;

import com.gidi.bio_console.alarm.model.AlarmLogModelImpl;
import com.gidi.bio_console.alarm.model.IOnLoadAlarmLogListener;
import com.gidi.bio_console.alarm.view.IAlarmLogView;
import com.gidi.bio_console.bean.AlarmMsgEnity;

public class AlarmLogPresenterImpl implements IAlarmLogPresenter,IOnLoadAlarmLogListener{

	private AlarmLogModelImpl mAlarmLogModel;
	private IAlarmLogView mView;
	
	public AlarmLogPresenterImpl(Context context,IAlarmLogView view){
		this.mAlarmLogModel = new AlarmLogModelImpl(context,this);
		this.mView = view;
			
	}

	@Override
	public void getAlarmMsgList(String searchTime) {
		// TODO Auto-generated method stub
		mAlarmLogModel.searchAlarmLog(searchTime);
		Log.i("ggg", "---getAlarmMsgList---" + searchTime);
	}

	
	@Override
	public void success(ArrayList<AlarmMsgEnity> list) {
		// TODO Auto-generated method stub
		if(null != mView){
			Log.i("ggg", "---success---" + list.size());
			mView.setAlarmLogList(list);
		}
	}

	
	@Override
	public void searchFail() {
		// TODO Auto-generated method stub
		if(null != mView){
			mView.onSearchFail();
		}
	}

	public void destroy(){
		mView = null;
		if(null != mAlarmLogModel){
			mAlarmLogModel.destroy();
		}
	}	
}
