package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.constant.RemoteCtrlContants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.NetworkUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoginServerMgr{

	private String loginMsg;
	private Socket mLoginSocket;
	private boolean isLinkFail = false;
	private TcpMsgSendThread mTcpMSendThread;
	private TcpMsgRecThread mTcpMsgRecThread;
	private OnLoginMsgRevListener mListener;

	public interface OnLoginMsgRevListener{
		void onLoginRepMsgReceiver(String serverUrl , int deviceId);
	}
	public void setOnLoginMsgRevListener(OnLoginMsgRevListener listener){
		this.mListener = listener;
	}
	
	public LoginServerMgr(String message){
		this.loginMsg = message;
	}
	
	public void Login(){
		Log.i("LoginServerMgr", "--Login--" + NetworkUtil.isNetConnected());
		if(NetworkUtil.isNetConnected()){

			Thread login = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						mLoginSocket = new Socket(RemoteCtrlContants.SERVER_IP, RemoteCtrlContants.LOGIN_PORT);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						isLinkFail = true;						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						isLinkFail = true;
					}
				}					
			};
			login.start();
			try {
	            login.join(); // 等待conn线程结束
	        } catch (InterruptedException e) {
	            isLinkFail = true;
	            e.printStackTrace();
	        }
			if (isLinkFail) {
//	            if (handler != null){
//	                handler.sendEmptyMessage();
				Log.i("xxs", "login fail");
//	            }
	        } else {
	        	//连接后向服务器发送json的登录消息
	        	mTcpMSendThread = new TcpMsgSendThread(mLoginSocket,mHandler);
	        	mTcpMSendThread.setStop(false);
	        	mTcpMSendThread.start();
	        	mTcpMSendThread.send_Json_msg(loginMsg);
	        	mTcpMsgRecThread = new TcpMsgRecThread(mLoginSocket,mHandler);
	        	mTcpMsgRecThread.setStop(false);
	        	mTcpMsgRecThread.start();
	        }
		}
		
	}
	

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RemoteCtrlContants.LOGIN_SERVER_SUCCESS){
                if (mTcpMSendThread != null){
                	mTcpMSendThread.setStop(true);
                	mTcpMSendThread.close();
                	mTcpMSendThread.interrupt();
                }
                
                if (mTcpMsgRecThread != null){
                	mTcpMsgRecThread.setStop(true);
                	mTcpMsgRecThread.close();
                	mTcpMsgRecThread.interrupt();
                }
                Log.i("xxs", "result--" + (String)msg.obj);
                //解析
                String loginResult = (String)msg.obj;
                if(null != loginResult){
                	parseLoginReplyMsg((String)msg.obj);
                }
                
            }
        }
    };
    
    private void parseLoginReplyMsg(String replyMsg){
    	Gson gson = new Gson();
    	DeviceLoginReplyMsg deviceLoginReplyMsg = gson.fromJson(replyMsg, DeviceLoginReplyMsg.class);
    	PreferenceUtil.getInstance(BioConsoleApplication.getInstance().getApplicationContext())
    			.setValueByName(SharedConstants.REMOTECTRL_SERVER_URL, deviceLoginReplyMsg.getUrl());
    	PreferenceUtil.getInstance(BioConsoleApplication.getInstance().getApplicationContext())
				.setValueByName(SharedConstants.REMOTECTRL_SERVER_DID, deviceLoginReplyMsg.getDid());
    	if(null != mListener){
    		mListener.onLoginRepMsgReceiver(deviceLoginReplyMsg.getUrl(),deviceLoginReplyMsg.getDid());
    	}
    }
}
