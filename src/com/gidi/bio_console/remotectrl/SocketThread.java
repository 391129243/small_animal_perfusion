package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.gidi.bio_console.constant.RemoteCtrlContants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SocketThread extends Thread {

	private String mHost;
	private int mPort;
	private Handler mHandler;
	private Socket serCtrSocket;
	private InputStream is;
	private OutputStream os;
	private boolean stop = true;
	private static ByteBuffer bytebuffer = ByteBuffer.allocate(102400);
	
	public SocketThread(String strHost, int port, Handler handler){
		this.mHost = strHost;
		this.mPort = port;
		this.mHandler = handler;
	}
	
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while(!stop){
			try {
				serCtrSocket = new Socket(mHost, mPort);
				if(serCtrSocket.isConnected() && !serCtrSocket.isClosed()){
					Log.i("zzz", "socket connect");	
					this.is = serCtrSocket.getInputStream();
					mHandler.sendEmptyMessage(RemoteCtrlContants.CONNECT_SERVER_SUCCESS);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				mHandler.sendEmptyMessage(RemoteCtrlContants.CONNECT_SERVER_FAILED);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.sendEmptyMessage(RemoteCtrlContants.CONNECT_SERVER_FAILED);
			}
			
			try {
				//解析服务器消息
				byte[] bt = inputStreamToByte(is); //解析读字节
				bytebuffer.put(bt);
				splitByte();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
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
	
	/***提取字节信息*/
	private void splitByte() {
		
		int p = bytebuffer.position();
		int l = bytebuffer.limit();

		bytebuffer.flip();
		byte[] byten = new byte[bytebuffer.limit()]; // 可用的字节数
		bytebuffer.get(byten, bytebuffer.position(), bytebuffer.limit()); // 得到目前为止缓冲区所有的数据

		// 进行基本检查，保证包含一组数据
		if (checkByte(byten)){
			byte[] len = new byte[2];

			// 数组源，数组源拷贝的开始位子，目标，目标填写的开始位子，拷贝的长度
			System.arraycopy(byten, 0, len, 0, 2);
			int length = bytesToInt(len); // 每个字节流的最开始肯定是定义本条消息数据的长度
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
			sendReplyMessage(ajsontext);

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
	
	/**发送返回结果**/
	private void sendReplyMessage(String message){
		if(message.contains("pro")){
			if(message.contains("\"pro\":10004")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.REMOTE_CTRL_MSG;
				msg.obj = message;
				mHandler.sendMessage(msg);
			}else if(message.contains("\"pro\":20008")){
				Message msg = Message.obtain();;
				msg.what = RemoteCtrlContants.REMOTE_HEART_BEAT_MSG;
				msg.obj = message;
				mHandler.sendMessage(msg);
			}else{
				Message msg = new Message();
				msg.what = 1;
				msg.obj = message;
				mHandler.sendMessage(msg);
			}
		}			
	}
	
	/**当Socket的isConnected return true &&　isClosed 返回 false**/
	
	public void send_Json_msg(String msg) {
		
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
			// 获得Socket的输出流
			if (serCtrSocket != null) {
				os = serCtrSocket.getOutputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

			buffer.flip();
			byte[] bs = new byte[buffer.limit()]; // 可用的字节数
			buffer.get(bs, buffer.position(), buffer.limit()); // 得到目前为止缓冲区所有的数据
		try {
			os.write(bs);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("zzz", "send msg fail---");
		}
		// 清空内容
		System.out.println("发送消息");
	}
	
	public void close(){
		try {
			if(null != os){
				os.close();
			}
			if(null != is){
				is.close();
			}
			if(serCtrSocket != null){
				serCtrSocket.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
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
}
