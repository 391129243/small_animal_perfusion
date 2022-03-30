package com.gidi.bio_console.remotectrl;

public class DeviceHeartBeatReplyMsg {

	private int pro;
	private int did;
	private int id;
	private String flag;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
