package com.gidi.bio_console.adapter;

import java.util.ArrayList;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.AlarmMsgEnity;
import com.gidi.bio_console.utils.HandlerSerialUtil;
import com.gidi.bio_console.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmLogAdapter extends BaseAdapter {

	private ArrayList<AlarmMsgEnity> mAlarmMsgList = new ArrayList<AlarmMsgEnity>();
	private LayoutInflater mInflater;
	private HandlerSerialUtil mHandlerSerialUtil;
	
	public AlarmLogAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHandlerSerialUtil = new HandlerSerialUtil(context);
	}
	
	public void setAlarmList(ArrayList<AlarmMsgEnity> alarmMsglist){		
		mAlarmMsgList.clear();
		mAlarmMsgList.addAll(alarmMsglist);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAlarmMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mAlarmMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder mHolder;
		AlarmMsgEnity mAlarmMsgEnity  = mAlarmMsgList.get(position);
		if(null == convertView){
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_alert_layout,parent,false);
			mHolder.mLiverIdTxt = (TextView)convertView.findViewById(R.id.liver_id_txt);
			mHolder.mAlarmMsgTxt = (TextView)convertView.findViewById(R.id.alarm_item_msg_txt);
			mHolder.mLevelTxt = (TextView)convertView.findViewById(R.id.alarm_item_level);
			mHolder.mAlarmTimeTxt = (TextView)convertView.findViewById(R.id.alarm_item_time);
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder)convertView.getTag();
		}
		if(position % 2 == 0){
			convertView.setBackgroundResource(R.color.color_serial_msg_even_bg);
		}else {
			convertView.setBackgroundResource(R.color.color_serial_msg_odd_bg);
		}
		mHolder.mLevelTxt.setText(R.string.alarm);
		mHolder.mLiverIdTxt.setText(mAlarmMsgEnity.getLiverNum());
		mHolder.mAlarmMsgTxt.setText(getAlarmMsg(mAlarmMsgEnity.getErrorCode()));
		mHolder.mAlarmTimeTxt.setText(mAlarmMsgEnity.getAlarmTime());
		return convertView;
	}
	
	private String getAlarmMsg(String errorCode){
		int error_code = StringUtil.convertToInt(errorCode, 401);
		if(error_code == 401){
			return errorCode;
		}else{
			return mHandlerSerialUtil.getErrorMsg(error_code);
		}
	}
	//静态内部类
	static class ViewHolder{
		TextView mLiverIdTxt;
		TextView mLevelTxt;
		TextView mAlarmMsgTxt;
		TextView mAlarmTimeTxt;
	}
}
