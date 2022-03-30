package com.gidi.bio_console.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.BileMsgBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BileRecordAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<BileMsgBean> mList = new ArrayList<BileMsgBean>();
	
	public BileRecordAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setRecordList(List<BileMsgBean> list){
		mList.clear();
		mList.addAll(list);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final BileMsgBean bean = mList.get(position);
		if(null == convertView){
			holder = new ViewHolder(); 
			convertView = mInflater.inflate(R.layout.module_list_item_record_bile,parent,false);
			holder.mTimeTxt = (TextView)convertView.findViewById(R.id.bile_time);
			holder.mBileCountTxt = (TextView)convertView.findViewById(R.id.bile_total_txt);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		if(position % 2 == 0){
			convertView.setBackgroundResource(R.color.color_alarm_msg_even);
		}else {
			convertView.setBackgroundResource(R.color.color_alarm_msg_odd);
		}
		holder.mTimeTxt.setText(bean.getTime());
		holder.mBileCountTxt.setText(String.valueOf(bean.getBileCount()));
		return convertView;
	}

	static class ViewHolder{
		TextView mTimeTxt;
		TextView mBileCountTxt;
	}
}
