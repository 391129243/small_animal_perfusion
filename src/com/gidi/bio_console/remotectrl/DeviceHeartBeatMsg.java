package com.gidi.bio_console.remotectrl;

public class DeviceHeartBeatMsg {

	public DeviceHeartBeatMsg(int pro, int did) {
		super();
		this.pro = pro;
		this.did = did;
	}
	private int pro;
	private int did;
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

}
