package com.gidi.bio_console.remotectrl;

/**
The message returned by the login server**/
public class DeviceLoginReplyMsg {

	private int pro;
	private int did;
	private String ip;
	private int port;
	private String url;
//	private String state;
	private int port1;
	public int getPro() {
		return pro;
	}
	public void setPro(int pro) {
		this.pro = pro;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
//	public String getState() {
//		return state;
//	}
//	public void setState(String state) {
//		this.state = state;
//	}
	public int getPort1() {
		return port1;
	}
	public void setPort1(int port1) {
		this.port1 = port1;
	}
	
}
