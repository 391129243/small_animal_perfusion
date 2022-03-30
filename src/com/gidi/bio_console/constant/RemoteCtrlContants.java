package com.gidi.bio_console.constant;

public class RemoteCtrlContants {

	public static final String UUID = "BIOC000001";
	public static final String LOGIN_PWD = "12345678";
	public static final String SERVER_IP = "112.74.216.117";
	public static final int LOGIN_PORT = 9200;
	public static final int SERVER_PORT = 9100;
	public static final int REMOTE_CTRL_MSG = 1008;
	public static final int REMOTE_HEART_BEAT_MSG = 1007;
	public static final int MSG_RELOGIN_SERVER = 1006;
	public static final int LOGIN_SERVER_SUCCESS = 1009;
	public static final int CONNECT_SERVER_SUCCESS = 1010;//返回20004消息
	public static final int CONNECT_SERVER_FAILED = 1011;
	public static final int MSG_SERVER_DISCONNECT = 1012;//消息服务器断连
	public static final int MSG_RECONNECT_SERVER = 1013;
	
	public static final int MSG_CONNECT_BIOINDEX = 1020;//连接血气分析仪
	public static final int MSG_RECONNECT_BIOINDEX = 1021;
	public static final int MSG_DISCONNECT_BIOINDEX = 1022;//wifi断开
	public static final int MSG_RECEIVE_BIOINDEX_DATA = 1023;//收到血气分析仪的数据
}
