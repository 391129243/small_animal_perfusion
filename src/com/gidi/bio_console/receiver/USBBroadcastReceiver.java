package com.gidi.bio_console.receiver;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class USBBroadcastReceiver extends BroadcastReceiver {

	private List<OnUsbChangedListener> mUsbChangedListeners = new ArrayList<OnUsbChangedListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
        	Log.i("usb","attched");
        	PreferenceUtil.getInstance(context.getApplicationContext())
				.setValueByName(SharedConstants.IS_USB_PLUNG, true);
        }else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
        	PreferenceUtil.getInstance(context.getApplicationContext())
				.setValueByName(SharedConstants.IS_USB_PLUNG, false);
        	Log.i("usb","detached");
        }
    }

   
	public void registerListener(OnUsbChangedListener listener){
		if(!mUsbChangedListeners.contains(listener)){
			mUsbChangedListeners.add(listener);
		}
	}
	
	public void unregisterListener(OnUsbChangedListener listener){
		if(mUsbChangedListeners.contains(listener)){
			mUsbChangedListeners.remove(listener);
		}
	}
   
    public interface OnUsbChangedListener {
    	void onUsbChange(boolean isConnected);
    }
}