package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.gidi.bio_console.constant.RemoteCtrlContants;
import com.gidi.bio_console.utils.DateFormatUtil;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**接收的线程**/
public class TcpMsgRecThread extends Thread {

	private InputStream is;
	private Socket mSocket;
	private Handler mHandler;
	private boolean isStop;
	private static ByteBuffer bytebuffer = ByteBuffer.allocate(102400);
	
	public TcpMsgRecThread(Socket clientSocket, Handler handler){
		this.mSocket = clientSocket;
		this.mHandler = handler;
		try {
			is = mSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public Socket getmSocket() {
		return mSocket;
	}


	public void setmSocket(Socket mSocket) {
		this.mSocket = mSocket;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (!isStop) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				//解析服务器消息.关闭后不进行操作
				if(mSocket != null){
					if(!mSocket.isClosed()){
						byte[] bt = inputStreamToByte(is); //解析读字节
						bytebuffer.put(bt);
						splitByte();
					}					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean isStop() {
		return isStop;
	}


	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}


	/***提取字节信息*/
	private void splitByte() {
		
		int p = bytebuffer.position();
		int l = bytebuffer.limit();
		Log.i("RemoteService", "p---" + p);
		Log.i("RemoteService", "l---" + l);
		bytebuffer.flip();
		byte[] byten = new byte[bytebuffer.limit()]; // 可用的字节数
		bytebuffer.get(byten, bytebuffer.position(), bytebuffer.limit()); // 得到目前为止缓冲区所有的数据

		// 进行基本检查，保证包含一组数据
		if (checkByte(byten)){
			byte[] len = new byte[2];

			// 数组源，数组源拷贝的开始位子，目标，目标填写的开始位子，拷贝的长度
			System.arraycopy(byten, 0, len, 0, 2);
			int length = bytesToInt(len); // 每个字节流的最开始肯定是定义本条消息数据的长度
			Log.i("RemoteService", "length---" + length);
			byte[] deco = new byte[length]; //  deco 就是这条数据体
			System.arraycopy(byten, 2, deco, 0, length);
			String ajsontext = "";
			try {
				ajsontext = new String(deco,0,deco.length,"utf-8");
				Log.i("RemoteService", "splitByte---" + ajsontext);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendReplyMsg(ajsontext);

			if (byten.length > length+2){
				byte[] temp = new byte[bytebuffer.limit() - length-2];
				// 数组源，数组源拷贝的开始位子，目标，目标填写的开始位子，拷贝的长度
				System.arraycopy(byten, length+2, temp, 0, bytebuffer.limit() - length-2);
				// 清除缓存
				bytebuffer.clear();
				// 重新定义缓存
				bytebuffer.put(temp);
				// 递归回调
				splitByte();
			}else if(byten.length == length+2){  // 如果只有一条数据，则直接重置缓冲就可以了
				// 清空缓存
				bytebuffer.clear();
			}
		}else {
			// 如果没有符合格式包含数据，则还原缓冲变量属�??
			bytebuffer.position(p);
			bytebuffer.limit(l);
		}
	}	
	
	/**解析字节后发送结果**/
	private void sendReplyMsg(String s){
		Log.i("TcpMsgRecThread", "s---" + s );
		Log.i("TcpMsgRecThread", "time---" + DateFormatUtil.getSysFullTime() );
		if(s.contains("pro")){
			if(s.contains("\"pro\":10004")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.REMOTE_CTRL_MSG;
				msg.obj = s;
				mHandler.sendMessage(msg);
			}else if(s.contains("\"pro\":20008")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.REMOTE_HEART_BEAT_MSG;
				msg.obj = s;
				mHandler.sendMessage(msg);
			}else if(s.contains("\"pro\":20004")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.CONNECT_SERVER_SUCCESS;
				msg.obj = s;
				mHandler.sendMessage(msg);
			} else if(s.contains("\"pro\":20000")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.LOGIN_SERVER_SUCCESS;
				msg.obj = s;
				mHandler.sendMessage(msg);
			}else {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = s;
				mHandler.sendMessage(msg);
			}
		}			
	}

	private boolean checkByte(byte[] byten) {
		// TODO Auto-generated method stub
		String s = "";
		try {
			s = new String(byten, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s.contains("\n");
	}
	
	private byte[] inputStreamToByte(InputStream is) throws Exception {

		int count = 0;
		while (count == 0) {
			count = is.available();
		}
		byte[] b = new byte[count];
		is.read(b);
		return b;
	}

	/**
	 * @方法功能 字节数组和整型的转换
	 * @param
	 * @return 整型
	 */
	private  int bytesToInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		return num;
	}

	public void close(){
		try {
			if(is != null){
				is.close();
			}
			if(mSocket != null){
				mSocket.close();
				mSocket = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
