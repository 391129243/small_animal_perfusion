package com.gidi.bio_console.adapter;

import java.util.ArrayList;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.SerialMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**数据信息**/
public class SerialMsgAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<SerialMessage> mList = new ArrayList<SerialMessage>();
	private LayoutInflater mInflater;
	
	public SerialMsgAdapter(Context context){
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setList(ArrayList<SerialMessage> list){
		this.mList.clear();
		this.mList.addAll(list);
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
		final ViewHolder mHolder;
		final SerialMessage serialMessage = mList.get(position);
		if(null == convertView){
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.serialmsg_list_item, null);
			mHolder.mArtMeanPreTxt = (TextView)convertView.findViewById(R.id.item_meanpre_content_txt);
			mHolder.mArtDiasPreTxt = (TextView)convertView.findViewById(R.id.item_dias_pre_content_txt);
			mHolder.mArtSystPreTxt = (TextView)convertView.findViewById(R.id.item_syst_pre_content_txt);
			mHolder.mArtFreqPreTxt = (TextView)convertView.findViewById(R.id.item_prefreq_content_txt);
			mHolder.mArtFrealTxt = (TextView)convertView.findViewById(R.id.item_artflow_content_txt);
			mHolder.mArtSpeedTxt = (TextView)convertView.findViewById(R.id.item_artspeed_content_txt);
			mHolder.mArtResistIndexTxt = (TextView)convertView.findViewById(R.id.item_artresistindex_content_txt);
			mHolder.mArtEHBFTxt = (TextView)convertView.findViewById(R.id.item_seial_artery_ehbf_content_txt);
			mHolder.mVeinSpeedTxt = (TextView)convertView.findViewById(R.id.item_veinspeed_content_txt);
			mHolder.mVeinPrealTxt = (TextView)convertView.findViewById(R.id.item_vein_meanpre_content_txt);
			mHolder.mVeinFrealTxt = (TextView)convertView.findViewById(R.id.item_veinflow_content_txt);
			mHolder.mVeinResistIndexTxt = (TextView)convertView.findViewById(R.id.item_veinresistindex_content_txt);
			mHolder.mVeinEHBFTxt = (TextView)convertView.findViewById(R.id.item_seial_vein_ehbf_content_txt);
			mHolder.mMsgTimeTxt = (TextView)convertView.findViewById(R.id.seial_msg_time);
			mHolder.mTempTxt = (TextView)convertView.findViewById(R.id.artery_temp_text);
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder)convertView.getTag();
		}
		if(position % 2 == 0){
			convertView.setBackgroundResource(R.color.color_serial_msg_even_bg);
		}else {
			convertView.setBackgroundResource(R.color.color_serial_msg_odd_bg);
		}
		if(null != serialMessage){
			mHolder.mArtMeanPreTxt.setText(serialMessage.getArtMeanPre());
			mHolder.mArtDiasPreTxt.setText(serialMessage.getArtDiasPre());
			mHolder.mArtSystPreTxt.setText(serialMessage.getArtSystPre());
			mHolder.mArtFreqPreTxt.setText(serialMessage.getArtFreqPre());
			mHolder.mArtFrealTxt.setText(serialMessage.getArtFreal());
			mHolder.mArtSpeedTxt.setText(serialMessage.getArtSpeed());
			mHolder.mArtResistIndexTxt.setText(serialMessage.getArtResistIndex());
			mHolder.mArtEHBFTxt.setText(serialMessage.getArtFlowEHBF());
			mHolder.mVeinFrealTxt.setText(serialMessage.getVeinFreal());
			mHolder.mVeinPrealTxt.setText(serialMessage.getVeinPreal());
			mHolder.mVeinSpeedTxt.setText(serialMessage.getVeinSpeed());
			mHolder.mVeinResistIndexTxt.setText(serialMessage.getVeinResistIndex());
			mHolder.mVeinEHBFTxt.setText(serialMessage.getVeinFlowEHBF());
			mHolder.mTempTxt.setText(serialMessage.getTemp());
			if(null != serialMessage.getMsgTime()){
				//显示需要改变时区
				mHolder.mMsgTimeTxt.setText(serialMessage.getMsgTime());
			
			}else{
				mHolder.mMsgTimeTxt.setText(R.string.string_null);
			}
			
			
		}
		return convertView;
	}
	
	static class ViewHolder{
		TextView mArtMeanPreTxt;
		TextView mArtFreqPreTxt;
		TextView mArtDiasPreTxt;
		TextView mArtSystPreTxt;
		TextView mArtFrealTxt;	
		TextView mArtSpeedTxt;
		TextView mArtResistIndexTxt;
		TextView mArtEHBFTxt;
		TextView mVeinSpeedTxt;		
		TextView mVeinFrealTxt;
		TextView mVeinPrealTxt;
		TextView mMsgTimeTxt;
		TextView mVeinResistIndexTxt;
		TextView mVeinEHBFTxt;
		TextView mTempTxt;
	}
}
