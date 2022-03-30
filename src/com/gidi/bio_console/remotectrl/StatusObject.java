package com.gidi.bio_console.remotectrl;

public class StatusObject {


	private String statusname; 
	private int status; 
	private int flag;
	
	public StatusObject(String statusname, int status, int flag) {
		super();
		this.statusname = statusname;
		this.status = status;
		this.flag = flag;
	}
	public String getStatusname() {
		return statusname;
	}
	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	} 
}
