package com.gidi.bio_console.remotectrl;

import java.util.List;


public class UserCtrlDeviceMsg {

	private int pro;
	private int pid;
	private int did;
	private String uuidkey;
	private List<StatusObject> list;
	public int getPro() {
		return pro;
	}
	public void setPro(int pro) {
		this.pro = pro;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public String getUuidkey() {
		return uuidkey;
	}
	public void setUuidkey(String uuidkey) {
		this.uuidkey = uuidkey;
	}
	public List<StatusObject> getList() {
		return list;
	}
	public void setList(List<StatusObject> list) {
		this.list = list;
	}
	
}
