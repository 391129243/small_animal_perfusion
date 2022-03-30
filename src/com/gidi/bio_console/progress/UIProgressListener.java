package com.gidi.bio_console.progress;

import android.os.Message;
/***
 * 用于界面的回调
 * @author admin
 * 在Activity的界面使用中
 * final UIProgressListener uiProgressResponseListener = new UIProgressListener() 
 */
public abstract class UIProgressListener implements ProgressListener {

	private boolean isFirst = false;
	private UIHandler mUIHandler = new UIHandler(this);
	
	private static class UIHandler extends UIProgressHandler{

		
		public UIHandler(UIProgressListener uiProgressListener){
			super(uiProgressListener);
		}
		
		@Override
		public void start(UIProgressListener uiProgressListener,
				long currentBytes, long contentLength, boolean done) {
			// TODO Auto-generated method stub
			if(null != uiProgressListener){
				uiProgressListener.onUIStart(currentBytes, contentLength, done);
			}
		}

		@Override
		public void progress(UIProgressListener uiProgressListener,
				long currentBytes, long contentLength, boolean done) {
			// TODO Auto-generated method stub
			if(null != uiProgressListener){
				uiProgressListener.onUIProgress(currentBytes, contentLength, done);
			}
		}

		@Override
		public void finish(UIProgressListener uiProgressListener,
				long currentBytes, long contentLength, boolean done) {
			// TODO Auto-generated method stub
			if(null != uiProgressListener){
				uiProgressListener.onUIFinish(currentBytes, contentLength, done);
			}
		}
		
	}
	@Override
	public void onProgress(long currentBytes, long contentLength, boolean done) {
		// TODO Auto-generated method stub

		if(!isFirst){
			isFirst = true;
			Message startMsg = Message.obtain();
			startMsg.what = UIProgressHandler.START;
			startMsg.obj = new ProgressBean(currentBytes,contentLength,done);
			mUIHandler.sendMessage(startMsg);
		}
		
		Message updateMsg = Message.obtain();
		updateMsg.what = UIProgressHandler.UPDATE;
		updateMsg.obj = new ProgressBean(currentBytes, contentLength, done);
		mUIHandler.sendMessage(updateMsg);
		
		if(done){
			Message finishMsg = Message.obtain();
			finishMsg.what = UIProgressHandler.FINISH;
			finishMsg.obj = new ProgressBean(currentBytes, contentLength, done);
			mUIHandler.sendMessage(finishMsg);
		}
	}


	public abstract void onUIProgress(long currentBytes, long contentLength, boolean done);
	

	public abstract void onUIStart(long currentBytes, long contentLength, boolean done);
	

	public abstract void onUIFinish(long currentBytes, long contentLength, boolean done);
}
