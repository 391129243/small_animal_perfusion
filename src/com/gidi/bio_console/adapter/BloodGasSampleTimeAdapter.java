package com.gidi.bio_console.adapter;

import java.util.ArrayList;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.BloodGasSamplingBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**血气采样时间列表**/
public class BloodGasSampleTimeAdapter extends BaseAdapter {

    private ArrayList<BloodGasSamplingBean> mBloodTimesList = new ArrayList<BloodGasSamplingBean>();
	private LayoutInflater mInflater;
	
	public BloodGasSampleTimeAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setBloodTimeList(ArrayList<BloodGasSamplingBean> bloodTimelist){
		
		mBloodTimesList.clear();
		mBloodTimesList.addAll(bloodTimelist);
		Log.i("BloodGasSampleTimeAdapter", "--BloodGasSampleTimeAdapter-setBloodTimeList---" + mBloodTimesList.size());
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBloodTimesList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBloodTimesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		final BloodGasSamplingBean bean = mBloodTimesList.get(position);
		if(null == convertView){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_blood_gas_time_layout,parent,false);
			viewHolder.mSamplingTex = (TextView)convertView.findViewById(R.id.blood_gas_sampling_time_txt);
			viewHolder.mCheckbox = (CheckBox)convertView.findViewById(R.id.blood_gas_time_cb);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.mSamplingTex.setText(bean.getSamplingTime());
		int checked = bean.getIsChecked();
		if(checked ==1){
			viewHolder.mCheckbox.setChecked(true);
		}else{
			viewHolder.mCheckbox.setChecked(false);
		}

		return convertView;
	}
	
	static class ViewHolder{
		TextView mSamplingTex;
		CheckBox mCheckbox;
	}

}
