package com.gidi.bio_console.progress;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public abstract class UIProgressHandler extends Handler {

	public static final int START = 0x01;
	public static final int UPDATE = 0x02;
	public static final int FINISH = 0x03;
	
	private WeakReference<UIProgressListener> mWeakUIProgressListener;
	
	public UIProgressHandler(UIProgressListener uiProgressListener){
		mWeakUIProgressListener = new WeakReference<UIProgressListener>(uiProgressListener);
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what) {
		case START:{
			UIProgressListener mUIProgressListener = mWeakUIProgressListener.get();
			if(null != mUIProgressListener){
				ProgressBean mProgressBean = (ProgressBean)msg.obj;
				start(mUIProgressListener, mProgressBean.getCurrentBytes(), mProgressBean.getContentLength(), mProgressBean.isDone());
			}
			break;
		}
			
		case UPDATE:{
			UIProgressListener mUIProgressListener = mWeakUIProgressListener.get();
			if(null != mUIProgressListener){
				ProgressBean mProgressBean = (ProgressBean)msg.obj;
				progress(mUIProgressListener, mProgressBean.getCurrentBytes(), mProgressBean.getContentLength(), mProgressBean.isDone());
			}
			break;
		}
			
		case FINISH:{
			UIProgressListener mUIProgressListener = mWeakUIProgressListener.get();
			if(null != mUIProgressListener){
				ProgressBean mProgressBean = (ProgressBean)msg.obj;
				finish(mUIProgressListener, mProgressBean.getCurrentBytes(), mProgressBean.getContentLength(), mProgressBean.isDone());
			}
			break;
		}
			
		default:
			break;
		}
	}

	public abstract void start(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
	public abstract void progress(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
	public abstract void finish(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
	
}
