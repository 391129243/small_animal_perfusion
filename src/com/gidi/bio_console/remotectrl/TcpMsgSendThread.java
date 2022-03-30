package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.gidi.bio_console.constant.RemoteCtrlContants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TcpMsgSendThread extends Thread {

	// 定义向UI线程发送消息的Handler对象	
	private UpDateHandler mUpDateHandler;
	private Handler mHandler;

	private OutputStream os = null;
	//线程结束的标志
	private boolean stop = true;

	private Socket mSendSocket;


	public boolean isStop(){
		return stop;
	}
	public void setStop(boolean stop){
		this.stop = stop;
	}

	public TcpMsgSendThread(Socket clientSocket, Handler mHandler){
		this.mHandler = mHandler;
		mSendSocket = clientSocket;
		mUpDateHandler = new UpDateHandler(this);
	}
	
	

	
	public Socket getmSendSocket() {
		return mSendSocket;
	}
	
	public void setmSendSocket(Socket mSendSocket) {
		this.mSendSocket = mSendSocket;
	}
	
	@Override
	public void run(){

//		while (!stop) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//			try {
//				//解析服务器消息
//				byte[] bt = inputStreamToByte(is); //解析读字节
//				bytebuffer.put(bt);
//				splitByte();
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
//		}

	}

	
	/**发送json数据**/
	public void send_Json_msg(String msg) {
		Log.i("RemoteService","send_Json_msg----" + msg );
		Log.i("RemoteService","mSendSocket----" + mSendSocket );
		if(mSendSocket != null){

			ByteBuffer buffer = ByteBuffer.allocate(10240);
			byte[] bytes = null;
			try {
				bytes = msg.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			int len = bytes.length;
			byte byte1 = (byte)((len>>8) & 0xff);
			byte byte2 = (byte)(len& 0xff);
			buffer.put(byte2);
			buffer.put(byte1);
			buffer.put(bytes);
			try {
				os = mSendSocket.getOutputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			buffer.flip();
			byte[] bs = new byte[buffer.limit()]; // 可用的字节数
			buffer.get(bs, buffer.position(), buffer.limit()); // 得到目前为止缓冲区所有的数据
			try {
				os.write(bs);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(RemoteCtrlContants.MSG_SERVER_DISCONNECT);
			}
			// 清空内容
			System.out.println("发送消息");
		}
			
	
	}

	public void close(){
		try {
			if(null != mSendSocket){
				mSendSocket.close();
				mSendSocket = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class UpDateHandler extends Handler{
		private final WeakReference<TcpMsgSendThread> mClientThread;

		public UpDateHandler(TcpMsgSendThread clientThread) {
			mClientThread = new WeakReference<TcpMsgSendThread>(clientThread);
		}
		@Override
		public void handleMessage(Message msg) {
			if (mClientThread.get() == null) return;
			if (msg.what == 1){
				mClientThread.get().send_Json_msg((String)msg.obj);
			}
		}
	}
}
