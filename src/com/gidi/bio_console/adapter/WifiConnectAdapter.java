package com.gidi.bio_console.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.MScanWifi;
import com.gidi.bio_console.utils.NetworkUtil;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiConnectAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private WifiManager wifiManager;
	private List<MScanWifi> mWifiResultList = new ArrayList<MScanWifi>();
	private List<WifiConfiguration> wifiConfigList = new ArrayList<WifiConfiguration>(); 
	
	public WifiConnectAdapter(Context context){
		this.mContext = context.getApplicationContext();
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		mWifiResultList =  new ArrayList<MScanWifi>();

	}
	
	public void setWifiList(List<MScanWifi> list){
		this.mWifiResultList = list;
		this.wifiConfigList = wifiManager.getConfiguredNetworks();
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null == mWifiResultList){
			return 0;
		}else{
			return mWifiResultList.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mWifiResultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void emptyWifiList() {
		if (this.mWifiResultList == null) {
			this.mWifiResultList = new ArrayList<MScanWifi>();
		}
		this.mWifiResultList.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		MScanWifi mScanWifi = mWifiResultList.get(position);
		final ScanResult scanResult= mScanWifi.getScanResult();
		int wifiLevel = mScanWifi.getWifiLevel();
		String securityMode = mScanWifi.getSecurityMode();
		if(null == convertView){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_wifi,parent,false);
			holder.mWifiNameTv = (TextView)convertView.findViewById(R.id.wifi_name_tv);
			holder.mWifiStatusTv = (TextView)convertView.findViewById(R.id.wifi_status_tv);
			holder.mWifiCnetImg = (ImageView)convertView.findViewById(R.id.wifi_status_img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		//已保存的如何显示
		holder.mWifiNameTv.setText(scanResult.SSID);
		holder.mWifiStatusTv.setText("");
		if(wifiConfigList != null ){
			if(wifiConfigList.size() > 0){
				for(WifiConfiguration configuration : wifiConfigList){
					String ssid = getCurrentWifiSSID(configuration.SSID);
					
					 if(scanResult.SSID.equals(ssid) && configuration.status == 0){
						 holder.mWifiStatusTv.setText(R.string.wlan_connect);
						 holder.mWifiStatusTv.setTextColor(mContext.getResources().getColor(R.color.blue));
					 }else if(scanResult.SSID.equals(ssid) && configuration.status != 0){
					 	 //没密码不用显示已保存
						 if(NetworkUtil.SecurityMode.OPEN.equals(NetworkUtil.getSecurityMode(scanResult))){
							 holder.mWifiStatusTv.setText("");
						 }else{
							 holder.mWifiStatusTv.setText(R.string.save);
							 holder.mWifiStatusTv.setTextColor(mContext.getResources().getColor(R.color.black));
						 }

					 }			
				}
			}
		}

		
		if("None".equals(securityMode)){
			if(0== wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_unlock_1);
			}else if(1 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_unlock_2);
			}else if(2 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_unlock_3);
			}else if(3 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_unlock_4);
			}
			//holder.mWifiStatusTv.setText(R.string.wlan_open);
		}else {
			if(0== wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_lock_1);
			}else if(1 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_lock_2);
			}else if(2 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_lock_3);
			}else if(3 == wifiLevel){
				holder.mWifiCnetImg.setImageResource(R.drawable.wifi_lock_4);
			}
		}		
		return convertView;
	}
	
	static class ViewHolder{
		TextView mWifiNameTv;
		TextView mWifiStatusTv;
		ImageView mWifiCnetImg;
	}
	
	private String getCurrentWifiSSID(String configurationSSID){
		String ssid = "";
        if (configurationSSID.substring(0, 1).equals("\"")
                && configurationSSID.substring(configurationSSID.length() - 1).equals("\"")) {
            ssid = configurationSSID.substring(1, configurationSSID.length() - 1);
        }
     
        return ssid;
	}
}
