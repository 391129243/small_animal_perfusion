package com.gidi.bio_console.receiver;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.listener.OnNetWorkChangedListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetWorkChangeReceiver extends BroadcastReceiver {

	private List<OnNetWorkChangedListener> mNetworkChangedListeners = new ArrayList<OnNetWorkChangedListener>();
	
	public void registerListener(OnNetWorkChangedListener listener){
		if(!mNetworkChangedListeners.contains(listener)){
			mNetworkChangedListeners.add(listener);
		}
	}
	
	public void unregisterListener(OnNetWorkChangedListener listener){
		if(mNetworkChangedListeners.contains(listener)){
			mNetworkChangedListeners.remove(listener);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Constants.NETWORK_CONNECTION_CHANGE)){
			ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(null != mNetworkInfo && mNetworkInfo.isAvailable()){
				if(mNetworkChangedListeners != null && mNetworkChangedListeners.size()>0){
					for(OnNetWorkChangedListener listener : mNetworkChangedListeners){
						listener.onNetWorkChange(mNetworkInfo.isConnected(), mNetworkInfo.getType());
					}
				}
			}else{				
				
				if(mNetworkChangedListeners != null && mNetworkChangedListeners.size()>0){
					for(OnNetWorkChangedListener listener : mNetworkChangedListeners){
						listener.onNetWorkChange(false, 0);
					}
				}
			}
		}	
	}	
}
