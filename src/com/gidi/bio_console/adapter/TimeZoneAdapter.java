package com.gidi.bio_console.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimeZoneAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> timeZoneList = new ArrayList<String>();
	
	public TimeZoneAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setTimeZoneList(List<String> list){
		timeZoneList.clear();
		timeZoneList.addAll(list);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null == timeZoneList){
			return 0;
		}else{
			return timeZoneList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(null == timeZoneList){
			return null;
		}else{
			return timeZoneList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		String[] timeZone = splitTimeZoneStr(timeZoneList.get(position));
		if(null == convertView){
			convertView = mInflater.inflate(R.layout.module_list_item_timezone, parent,false);
			holder.timezoneTxt = (TextView)convertView.findViewById(R.id.item_timezone_txt);
			holder.timeZoneGMTTxt = (TextView)convertView.findViewById(R.id.item_gmt_txt);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		if(timeZone != null && timeZone.length == 2){
			holder.timezoneTxt.setText(timeZone[0]);
			holder.timeZoneGMTTxt.setText(timeZone[1]);
		}
		return convertView;
	}

	static class ViewHolder{
		TextView timezoneTxt;
		TextView timeZoneGMTTxt;
	}
	
	private String[] splitTimeZoneStr(String timeZone){
		 if(timeZone != null && timeZone.contains("/")){
			 String[] strArr = timeZone.split("\\/");
			 return strArr;
		 }
		 return null;
	}
}
